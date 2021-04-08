package com.mark.resource;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Resource {
    public String path;
    public int rating;
    public boolean checked;
    public long duration;
    public long fileSize;
    public Date modifiedTime;
    public Date accessedTime;

    public ArrayList<Marker> markers = new ArrayList<>();

    private transient ArrayList<IResourceChangeListener> resourceChangeListeners;

    // silence change notifications (like when deserializing or batch processing)
    private transient boolean silentMode;

    public Resource(String path) {
        this.path = path;

        // one marker at the starting point always to support the selected span play for the first segment.
        // subsequently added markers will dictate the segment to the right.
        markers.add(new Marker(0));
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

        for (IResourceChangeListener listener : this.resourceChangeListeners) {
            listener.onResourceChange(this, changeType);
        }
    }

    // used only by LegacyFileReader
    public void addMarker(long time) {
        if (markers.size() == 1 && time == duration) {
            // the first marker entry should be ignored (a marker with the duration time - no purpose in the new format)
            Log.log("The first marker entry should have the time equal to duration, which should be ignored", time);
        }
        else {
            markers.add(new Marker(time));
        }
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
        }
        else {
            markers.add(new Marker(currentTime));
            Collections.sort(markers);
        }

        notifyChangeListeners(EResourceChangeType.MarkerAdded);
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
    }

    // marker time to jump to if only selected markers are being played
    public long getSelectedMarkerTime(long currentTime, boolean backwardHint) {
        //Log.log("selected marker detection: %d", currentTime);

        if (markers.size() <= 1) {
            return -1;
        }

        int spanIndex = getMarkerSpanIndex(currentTime);
        Marker marker = markers.get(spanIndex);

        if (marker.select) {
            if (backwardHint && currentTime - marker.time < Prefs.getTimeFuzzyFactor()) {
                for (int i = Utils.mod(spanIndex - 1, markers.size()); i != spanIndex; i = Utils.mod(i-1, markers.size())) {
                    if (markers.get(i).select) {
                        return markers.get(i).time;
                    }
                }
            }
            return -1;      // do not bother as the play head in the selected marker
        }

        for (int i = (spanIndex + 1) % markers.size(); i != spanIndex; i = (i+1) % markers.size()) {
            if (markers.get(i).select) {
                return markers.get(i).time;
            }
        }

        return -1;
    }

    public long getSelectedMarkerTime_work_in_progress(long currentTime) {
        int index = Collections.binarySearch(markers, new Marker(currentTime));
        if (index > 0) {        // right on the marker, so we ignore it
            return -1;
        }

        Log.log("Marker binary search: %d, %d", index, currentTime);
        index = -index - 1;
        Log.log("Marker binary search - : %d", index);

        /*
        if (index == 0) {       // TODO: take care of 0/boundary condition
            if (markers.size() > 0) {
                return markers.get(0).time;
            }
        }
         */
        if (index > 0){
            Marker currentMarker = markers.get(--index);
            if (!currentMarker.select) {
                for (int i=index; i<markers.size(); i++) {
                    Marker marker = markers.get(i);
                    if (marker.select) {
                        Log.log("Marker binary search ==> : %d, %d", i, marker.time);
                        return marker.time;
                    }
                }
            }
        }

        return markers.size() > 0 ? markers.get(0).time : -1;
    }
}