package com.mark.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Resource {
    public String path;
    public int rating;
    public boolean checked;
    public float duration;
    public long fileSize;
    public Date modifiedTime;
    public Date accessedTime;

    public ArrayList<Marker> markers = new ArrayList<>();

    private transient ArrayList<IResourceChangeListener> resourceChangeListeners;

    // silence change notifications (like when deserializing or batch processing)
    private transient boolean silentMode;

    public Resource(String path) {
        this.path = path;
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

    public void addMarker(long position) {
        markers.add(new Marker(position));
        Collections.sort(markers);
        notifyChangeListeners(EResourceChangeType.MarkerAdded);
    }

    public void setMarkerSelect(boolean select) {
        if (markers.size() > 0) {
            markers.get(markers.size()-1).select = select;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Marker marker : markers) {
            builder.append(String.format("marker: %d, %b\n", marker.time, marker.select));
        }

        return String.format("path: %s, rating: %d, checked: %b, duration %.2f, fileSize: %d, modified: %s, accessed: %s\n%s\n",
                             path, rating, checked, duration, fileSize, modifiedTime, accessedTime, builder.toString());
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public long getMarkerTime(long currentTime, boolean forward) {
        if (forward) {
            for (Marker marker : markers) {
                if (marker.time > currentTime) {
                    return marker.time;
                }
            }
        }
        else {
            for (int i=markers.size()-1; i>=0; i--) {
                Marker marker = markers.get(i);
                if (marker.time < currentTime) {
                    return marker.time;
                }
            }
        }

        return -1;
    }

    public void toggleMarker(long currentTime) {
        for (int i=markers.size()-1; i>=0; i--) {
            Marker marker = markers.get(i);
            if (marker.time < currentTime) {
                marker.select = !marker.select;
                break;
            }
        }
    }
}