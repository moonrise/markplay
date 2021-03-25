package com.mark.resource;

import java.util.ArrayList;
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

    private transient ArrayList<IResourceChangeListener> resourceChangeListeners = new ArrayList<>();

    public Resource(String path) {
        this.path = path;
    }

    public void registerChangeListener(IResourceChangeListener listener) {
        this.resourceChangeListeners.add(listener);
    }

    public void unRegisterChangeListener(IResourceChangeListener listener) {
        this.resourceChangeListeners.remove(listener);
    }

    private void notifyChangeListeners(EResourceChangeType changeType) {
        for (IResourceChangeListener listener : this.resourceChangeListeners) {
            listener.onResourceChange(this, changeType);
        }
    }

    public void addMarker(float position) {
        markers.add(new Marker(position));
        notifyChangeListeners(EResourceChangeType.MarkerAdded);
    }

    /**
     * used only from legacy XML Sax parser
     * @param select
     */
    public void setMarkerSelect(boolean select) {
        if (markers.size() > 0) {
            markers.get(markers.size()-1).select = select;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Marker marker : markers) {
            builder.append(String.format("marker: %.2f, %b\n", marker.position, marker.select));
        }

        return String.format("path: %s, rating: %d, checked: %b, duration %.2f, fileSize: %d, modified: %s, accessed: %s\n%s\n",
                             path, rating, checked, duration, fileSize, modifiedTime, accessedTime, builder.toString());
    }
}
