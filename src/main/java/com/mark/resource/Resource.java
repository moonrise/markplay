package com.mark.resource;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Resource {
    private static final long MinForwardDelta = 50;         // milli seconds
    private static final long MinMarkerMergeGap = 500;      // milli seconds

    private String path;
    public int rating;
    public boolean checked;
    public long duration;
    public long fileSize;
    public Date modifiedTime;
    public Date accessedTime;

    public ArrayList<Marker> markers = new ArrayList<>();

    private transient ArrayList<IResourceChangeListener> resourceChangeListeners;
    private transient ResourceList parentList;

    public transient int temp;         // temporary work variable

    // silence change notifications (like when deserializing or batch processing)
    private transient boolean silentMode;

    public Resource(String path, ResourceList parentList) {
        this.parentList = parentList;       // assign parentList before setting the path
        this.path = normalizePath(path);

        // one marker at the starting point always to support the selected span play for the first segment.
        // subsequently added markers will dictate the segment to the right.
        markers.add(new Marker(0));
    }

    @Override
    public boolean equals(Object obj) {
        Resource other = (Resource)obj;
        return getPath().equals(other.getPath()) && fileSize == other.fileSize;
    }

    public ResourceList getParentList() {
        return parentList;
    }

    // we need this when the parent list file is de-serialized to establish the child to parent link
    public void setParentList(ResourceList parentList) {
        this.parentList = parentList;
    }

    public String getPath() {
        return getParentList().getRoot() + path;
    }

    public String getPathWithNoRoot() {
        return path;
    }

    public void setDuration(long duration) {
        if (duration > 0 && this.duration != duration) {
            this.duration = duration;
            notifyChangeListeners(EResourceChangeType.AttributesUpdated);
        }
    }

    public void setFileSize(long fileSize) {
        if (this.fileSize != fileSize) {
            this.fileSize = fileSize;
            notifyChangeListeners(EResourceChangeType.AttributesUpdated);
        }
    }

    public String getName() {
        return FilenameUtils.getName(path);
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
        this.checked = !this.checked;
        notifyChangeListeners(EResourceChangeType.FavoriteToggled);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Marker marker : markers) {
            builder.append(String.format("marker: %d, %b\n", marker.time, marker.select));
        }

        return String.format("path: %s, rating: %d, checked: %b, duration %d, fileSize: %d, modified: %s, accessed: %s\n%s\n",
                             path, rating, checked, duration, fileSize, modifiedTime, accessedTime, builder.toString());
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

    public String normalizePath(String path) {
        return path.substring(parentList.getRoot().length());
    }

    public boolean normalizePathToRoot(String root) {
        if (validateRoot(root)) {
            path = getPath().substring(root.length());
            return true;
        }
        return false;
    }

    public void mergeWith(Resource from) {
        if (from.checked) {     // checked-true has the precedence
            checked = true;
        }

        if (from.markers.size() < 1) {
            return;     // nothing to merge
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
            }
        }

        // final sort with the merged ones
        Collections.sort(this.markers);
    }
}