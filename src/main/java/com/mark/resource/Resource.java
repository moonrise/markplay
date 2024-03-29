package com.mark.resource;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import com.mark.main.ResourceListTableModel;
import com.mark.utils.HashStore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Resource {
    private static final long MinForwardDelta = 50;         // milli seconds
    private static final long MinMarkerMergeGap = 500;      // milli seconds

    public boolean checked;
    public float rating;
    public String tag = "";
    private String path = "";
    public long duration;
    public long fileSize;
    public String fileHash = "";

    // user data modification time; path, duration, fileSize or fileHash do not count as they are computed values.
    // notice 'path' does not count because it is derived from the file name.
    // this time primarily used to know the age of the stored data (see HashStore)
    public long userDataModifiedTime;

    public ArrayList<Marker> markers = new ArrayList<>();

    private static int keyBase = 0;   // ever increasing to support the key
    public transient int key;        // runtime key that uniquely identifies this instance
    private transient ArrayList<IResourceChangeListener> resourceChangeListeners;
    private transient ResourceList parentList;

    public transient int temp;         // temporary work variable

    // silence change notifications (like when deserializing or batch processing)
    private transient boolean silentMode;

    public Resource(String path, ResourceList parentList) {
        assignKey();
        this.parentList = parentList;       // assign parentList before setting the path
        setNormalizedPath(path);

        // one marker at the starting point always to support the selected span play for the first segment.
        // subsequently added markers will dictate the segment to the right.
        markers.add(new Marker(0));
    }

    // this version creates a temporary working instance for deserialization from the hash table
    // input should have the right count of values in the right sequence.
    private Resource(String[] splits) {
        this.checked = splits[1].equals("1");
        this.rating = Float.parseFloat(splits[2]);
        this.tag = splits[3];

        String[] markers = splits[4].split(";");
        for (String marker : markers) {
            this.markers.add(new Marker(marker));
        }
    }

    public void assignKey() {
        if (this.key == 0) {
            this.key = ++keyBase;
        }
    }

    @Override
    public boolean equals(Object obj) {
        Resource other = (Resource)obj;
        return key == other.key;
        //return getPath().equals(other.getPath()) && fileSize == other.fileSize;   // not good enough
    }

    public ResourceList getParentList() {
        return parentList;
    }

    // we need this when the parent list file is de-serialized to establish the child to parent link
    public void setParentList(ResourceList parentList) {
        this.parentList = parentList;
    }

    public String getPath() {
        return Utils.normPath(getParentList().getRoot() + path);
    }

    public String getMidPath() {
        return FilenameUtils.getPath(Utils.normPath(path));
    }

    public boolean updateMidPath(String currentMidPath, String newMidPath) {
        if (!Utils.normPathIsEqual(currentMidPath, getMidPath())) {
            return false;
        }

        // compute the current/source path and the target path
        String currentPath = FilenameUtils.getFullPath(getPath());
        String pathWithNoMidPath = currentPath.substring(0, currentPath.length() - getMidPath().length());
        String targetPath = Utils.normPath(pathWithNoMidPath + newMidPath + getName());
        //Log.log("updating midPath: %s -> %s; (%s -> %s)", getMidPath(), newMidPath, getPath(), targetPath);

        // source already in target?
        // Also possible when there are multiple entries with the same file paths (one is moved and others won't need move)
        if (new File(targetPath).exists()) {
            String message = String.format("updated midPath: file '%s' already in the target directory.", getName());
            Log.log(message);
            parentList.getMain().displayStatusMessage(message);

            setNormalizedPath(targetPath);
            return true;
        }

        try {
            if (new File(currentPath).exists()) {
                FileUtils.moveFile(new File(getPath()), new File(targetPath));
            }
            else {
                String message = String.format("updated midPath: file '%s' does not exist in the source directory.", getName());
                Log.log(message);
                parentList.getMain().displayStatusMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            parentList.getMain().displayErrorMessage(e.toString());
            return false;
        }

        setNormalizedPath(targetPath);
        return true;
    }

    public void setDuration(long duration) {
        if (duration > 0 && this.duration != duration) {
            this.duration = duration;
            notifyChangeListeners(EResourceChangeType.AttributesUpdated);
        }
    }

    public boolean fileExists() {
        return new File(getPath()).exists();
    }

    public boolean isFileHashed() {
        return this.fileHash != null && !this.fileHash.isEmpty();
    }

    public void initFileSizeAndHash() {
        if (!isFileHashed()) {
            setFileSizeAndHash();
            notifyChangeListeners(EResourceChangeType.AttributesUpdated);
        }
    }

    public void setFileSizeAndHash() {
        this.setFileSizeAndHash(true);
    }

    public void setFileSizeAndHash(boolean notify) {
        this.fileSize = new File(getPath()).length();
        //Log.log(String.format("Hashing %s...", this.getPath()));
        if (this.fileSize > 0) {
            this.fileHash = Utils.computeFileHash(this.getPath());
        }

        if (notify) {
            notifyChangeListeners(EResourceChangeType.AttributesUpdated);
        }
    }

    public void clearFileSizeAndHash() {
        this.fileSize = 0;
        this.fileHash = "";
    }

    public boolean isFileContentEqual(Resource other) {
        initFileSizeAndHash();      // ensure we have the file size known and hash computed
        other.initFileSizeAndHash();
        return fileSize > 0 && fileSize == other.fileSize && fileHash.equals(other.fileHash);
    }

    public String getName() {
        return FilenameUtils.getName(path);
    }

    /* rename really */
    public void setName(String name) {
        String currentBaseName = FilenameUtils.getBaseName(getName());
        String newBaseName = FilenameUtils.getBaseName(name);
        if (!newBaseName.equals(currentBaseName)) {
            String currentPath = getPath();
            String newPath = Utils.normPath(FilenameUtils.getFullPath(currentPath) + newBaseName + "." + FilenameUtils.getExtension(getName()));
            if (parentList.getMain().renameMediaFile(currentPath, newPath)) {
                setNormalizedPath(newPath);
                parentList.getMain().saveCurrentResourceList(false); // save is required to sync with the file system
            }
        }
    }

    public void registerChangeListener(IResourceChangeListener listener) {
        if (resourceChangeListeners == null) {
            resourceChangeListeners = new ArrayList<>();
        }

        resourceChangeListeners.add(listener);
    }

    public void unRegisterChangeListener(IResourceChangeListener listener) {
        if (resourceChangeListeners != null) {
            resourceChangeListeners.remove(listener);
        }
    }

    private void notifyChangeListeners(EResourceChangeType changeType) {
        if (silentMode) {
            return;
        }

        // change notification to the parent is direct
        if (changeType != EResourceChangeType.AttributesUpdated) {
            onUserDataModified();       // set the timestamp
        }
        parentList.onChildResourceChange(this, changeType);

        // notify all others who are registered
        if (resourceChangeListeners != null) {
            for (IResourceChangeListener listener : resourceChangeListeners) {
                listener.onResourceChange(this, changeType);
            }
        }
    }

    // used only by LegacyFileReader
    public void addMarker(long time) {
        markers.add(new Marker(time));
    }

    // used only by LegacyFileReader
    public void postProcessLegacyResource() {
        if (markers.size() <= 1) {
            return;     // no markers were transferred from the legacy source
        }

        // legacy source has the marker selection to the left while the new format the selection is to the right.
        Collections.sort(markers);
        for (int i=1; i<markers.size(); i++) {
            markers.get(i-1).select = markers.get(i).select;
        }

        // the ending marker should be dropped as the new format has the beginning marker (at zero index always)
        markers.remove(markers.size()-1);
    }


    // used only by LegacyFileReader right after addMarker call
    public void setMarkerSelect(boolean select) {
        if (markers.size() > 0) {
            markers.get(markers.size()-1).select = select;      // set it to the last marker as it is jsut added
        }
    }

    public void toggleMarker(long currentTime) {
        int timeFuzzyFactor = Prefs.getTimeFuzzyFactor();

        if (currentTime < timeFuzzyFactor*2) {
            return;     // the first marker is not editable (should be there always)
        }

        int spanIndex = getMarkerSpanIndex(currentTime);
        if (currentTime - markers.get(spanIndex).time < timeFuzzyFactor*1.5) {
            markers.remove(spanIndex);
            notifyChangeListeners(EResourceChangeType.MarkerRemoved);
        }
        else {
            markers.add(new Marker(currentTime));
            Collections.sort(markers);
            notifyChangeListeners(EResourceChangeType.MarkerAdded);
        }
    }

    public void toggleFavorite() {
        setFavorite(!this.checked);
    }

    public void setFavorite(boolean checked) {
        this.checked = !this.checked;
        notifyChangeListeners(EResourceChangeType.SelectUpdated);
    }

    public void setRating(float rating) {
        this.rating = rating;
        notifyChangeListeners(EResourceChangeType.RatingUpdated);
    }

    public void setTag(String tag) {
        this.tag = tag;
        notifyChangeListeners(EResourceChangeType.TagUpdated);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Marker marker : markers) {
            builder.append(String.format("marker: %d, %b\n", marker.time, marker.select));
        }

        return String.format("path: %s, rating: %d, checked: %b, duration %d, fileSize: %d, modified: %d\n%s\n",
                             path, rating, checked, duration, fileSize, userDataModifiedTime, builder.toString());
    }

    public String toStore() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d,%d,%.3f,%s,", userDataModifiedTime, checked ? 1 : 0, rating, tag == null ? "" : tag));
        builder.append(markers.get(0).toStore());
        for (int i=1; i<markers.size(); i++) {
            builder.append(";");        // ":" is used within the marker as a delimiter
            builder.append(markers.get(i).toStore());
        }
        return builder.toString();
    }

    public boolean updateToStore(HashStore hashStore) {
        // ensure we have the hash value of this resource
        if (!isFileHashed()) {
            return false;
        }

        // store it
        String stored = hashStore.get(fileHash);
        if (stored == null || amIModifiedSince(stored)) {
            hashStore.put(fileHash, toStore(), false);
            //Log.log("--- user data updated to hash store (%s) ---\n%s", fileHash, toStore());
            return true;
        }

        return false;
    }

    /**
     * @param hashStore
     * @return 1 : restore merged, 0: nothing to merge, -1: not in the hash table, -2: error
     */
    public int restoreFromStore(HashStore hashStore) {
        // ensure we have the hash value of this resource
        if (!isFileHashed()) {
            this.temp = ResourceListTableModel.TEMP_ERROR;
            return ResourceListTableModel.TEMP_ERROR;
        }

        // restore from the hash store db
        String stored = hashStore.get(fileHash);
        if (stored == null) {
            this.temp = ResourceListTableModel.TEMP_NOT_IN_HASHSTORE;
            return ResourceListTableModel.TEMP_NOT_IN_HASHSTORE;
        }

        String[] splits = stored.split(",");
        if (splits.length < 5) {
            this.temp = ResourceListTableModel.TEMP_ERROR;
            return ResourceListTableModel.TEMP_ERROR;
        }

        boolean isRestored = false;
        Resource restored = new Resource(splits);
        if (restored.checked && !this.checked) {
            this.checked = true;
            isRestored = true;
        }

        if (restored.rating > 0 && this.rating <= 0) {
            this.rating = restored.rating;
            isRestored = true;
        }

        if (!restored.tag.isEmpty() && this.tag.isEmpty()) {
            this.tag = restored.tag;
            isRestored = true;
        }

        // markers are merged rather than copied
        int markersAdded = mergeMarkersWith(restored);
        if (markersAdded > 0) {
            isRestored = true;
        }

        this.temp = isRestored ? ResourceListTableModel.TEMP_RESTORE_MERGED : 0;
        return this.temp;
    }

    public int isInHashStore(HashStore hashStore) {
        temp = 0;

        if (!isFileHashed()) {
            return -1;
        }

        if (hashStore.get(fileHash) == null) {
            return 0;
        }

        temp = 1;
        return 1;
    }

    private boolean amIModifiedSince(String stored) {
        long storedModifiedTime = Long.parseLong(stored.substring(0, stored.indexOf(",")));
        return userDataModifiedTime > storedModifiedTime;
    }

    private void onUserDataModified() {
        userDataModifiedTime = new Date().getTime();
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    // marker span index is the marker index to the left of the current time inclusive.
    // For example, consider the marker array [0, 50, 120, 3000, 6700]. Current time to index maps like this:
    //  0 -> 0, 1 -> 0, 49 -> 0
    //  120 -> 2, 500 -> 2
    //  6700 -> 4, 9000 -> 4
    // note that the most left timer marker is always zero and is guaranteed to be there.
    public int getMarkerSpanIndex(long currentTime) {
        int index = Collections.binarySearch(markers, new Marker(currentTime));
        return index >= 0 ? index : -index-2;
    }

    // the marker time to the left (forward) or to the right (!forward)
    public long getAdjacentMarkerTime(long currentTime, long duration, boolean forward, boolean paused) {
        long fuzzyFactor = Prefs.getTimeFuzzyFactor();
        int spanIndex = getMarkerSpanIndex(currentTime);
        int markersSize = markers.size();
        boolean lastSpan = spanIndex == markersSize - 1;

        if (forward) {
            if (paused && currentTime == duration) {
                return 0;               // wrap to the beginning
            }
            if (lastSpan) {
                return duration;        // to the end
            }
            return markers.get(spanIndex + 1).time;
        }
        else {  // backward
            long markerTime = markers.get(spanIndex).time;
            if (paused && currentTime == 0) {
                return duration;        // wrap to the end
            }
            else if (paused && currentTime == markerTime) {
                return markers.get(spanIndex - 1).time;
            }
            else if (spanIndex == 0) {
                return currentTime - markerTime < fuzzyFactor ? duration - fuzzyFactor*2 : markerTime;
            }
            return currentTime - markerTime < fuzzyFactor ? markers.get(spanIndex - 1).time : markerTime;
        }
    }

    public void toggleMarkerSelection(long currentTime) {
        Marker marker = markers.get(getMarkerSpanIndex(currentTime));
        marker.select = !marker.select;
        notifyChangeListeners(EResourceChangeType.MarkerSelectionToggled);
    }

    // marker time to jump to if only selected markers are being played
    public long getSelectedMarkerTime(long currentTime, boolean backwardHint) {
        if (markers.size() <= 1) {
            return -1;
        }

        int spanIndex = getMarkerSpanIndex(currentTime);
        Marker marker = markers.get(spanIndex);

        //Log.log("selected marker processor - direction:%s, time:%d, span:%d, select:%s", backwardHint ? "<":">", currentTime, spanIndex, marker.select?"x":"");
        if (marker.select) {
            if (backwardHint && (currentTime - marker.time < Prefs.getTimeFuzzyFactor())) {
                for (int i = Utils.mod(spanIndex - 1, markers.size()); i != spanIndex; i = Utils.mod(i-1, markers.size())) {
                    if (markers.get(i).select) {
                        //Log.log("selected marker processor - jump back:%d,%d", i, markers.get(i).time);
                        return markers.get(i).time;
                    }
                }
            }
            //Log.log("selected marker processor - do nothing");
            return -1;      // do not bother as the play head in the selected marker
        }

        for (int i = (spanIndex + 1) % markers.size(); i != spanIndex; i = (i+1) % markers.size()) {
            if (markers.get(i).select) {
                //Log.log("selected marker processor - jump:%d,%d", i, markers.get(i).time);

                // if the jump target is too close from the current time, the playback can reverse and create a loop
                // because while we're processing all these, the play head is moving forward.
                long jumpTo = markers.get(i).time;
                long forwardDelta = Math.abs(jumpTo - currentTime);

                // one solution is ensuring the minimum jump like 50-100 milli seconds. This tends to have disruption
                // in playback either as if it skips a few frames (not a good solution - 'minimum forward delta').
                //return forwardDelta < MinForwardDelta ? (jumpTo + MinForwardDelta - forwardDelta) : jumpTo;


                // other solution is if the jump target is very close from the current time, simply ignore it and let
                // it play. This seems to work better. MinForwardDelta will determine how quickly/accurately it reaches
                // the target when the current time is away from it - needs to experiment for the best value.
                return forwardDelta < MinForwardDelta ? -1 : jumpTo;
            }
        }

        return -1;
    }

    public boolean validateRoot(String root) {
        return getPath().startsWith(root);
    }

    public void setNormalizedPath(String path) {
        this.path = path.substring(parentList.getRoot().length());
    }

    public boolean normalizePathToRoot(String root) {
        if (validateRoot(root)) {
            path = getPath().substring(root.length());
            return true;
        }
        return false;
    }

    public int mergeMarkersWith(Resource from) {
        int markersAdded = 0;

        if (from.markers.size() < 1) {
            return 0;     // nothing to merge
        }

        // append from-markers temporarily
        ArrayList<Marker> tempMarkers = (ArrayList<Marker>)markers.clone();
        for (Marker marker : from.markers) {
            marker.work = 1;        // tag the merged marker
            tempMarkers.add(marker);
        }

        // sort them for right time sequence
        Collections.sort(tempMarkers);


        // add/merge sensible markers only (i.e. remove matching or close enough markers)
        //Log.log("-------------- tag merge: %s", getName());
        for (int i=0; i<tempMarkers.size(); i++) {
            Marker m = tempMarkers.get(i);
            if (m.work != 1) {
                continue;
            }

            long prevTime = (i==0) ? 0 : tempMarkers.get(i-1).time;
            long nextTime = (i==tempMarkers.size()-1) ? -1 : tempMarkers.get(i+1).time;
            //Log.log("tag merge [%d]: %d(%d) <- %d -> %d(%d)", i, prevTime, m.time-prevTime, m.time, nextTime, nextTime-m.time);
            if ((m.time - prevTime) >= MinMarkerMergeGap && (nextTime == -1 || (nextTime - m.time) >= MinMarkerMergeGap)) {
                //Log.log(" - OK to merge -");
                this.markers.add(m);
                markersAdded++;
            }
        }

        // final sort with the merged ones
        Collections.sort(this.markers);
        return markersAdded;
    }
}