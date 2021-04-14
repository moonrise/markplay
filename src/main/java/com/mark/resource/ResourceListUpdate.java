package com.mark.resource;

public class ResourceListUpdate {
    public EResourceListChangeType type;
    public int startRow;
    public int endRow;

    public Resource childResource;
    public EResourceChangeType childResourceChangeType;

    public static ResourceListUpdate Loaded = new ResourceListUpdate(EResourceListChangeType.Loaded);

    public static ResourceListUpdate Unloaded = new ResourceListUpdate(EResourceListChangeType.Unloaded);

    public static ResourceListUpdate IndexChanged = new ResourceListUpdate(EResourceListChangeType.IndexChanged);

    public static ResourceListUpdate AllRowsUpdated = new ResourceListUpdate(EResourceListChangeType.AllRowsUpdated);

    public static ResourceListUpdate Saved = new ResourceListUpdate(EResourceListChangeType.Saved);

    public static ResourceListUpdate RowsAdded(int startRow, int endRow) {
        ResourceListUpdate resourceListUpdate = new ResourceListUpdate(EResourceListChangeType.RowsAdded);
        resourceListUpdate.startRow = startRow;
        resourceListUpdate.endRow = endRow;
        return resourceListUpdate;
    }

    public static ResourceListUpdate RowsRemoved(int startRow, int endRow) {
        ResourceListUpdate resourceListUpdate = new ResourceListUpdate(EResourceListChangeType.RowsRemoved);
        resourceListUpdate.startRow = startRow;
        resourceListUpdate.endRow = endRow;
        return resourceListUpdate;
    }

    public static ResourceListUpdate ChildResourceChanged(int rowIndex, Resource childResource, EResourceChangeType childResourceChangeType) {
        ResourceListUpdate resourceListUpdate = new ResourceListUpdate(EResourceListChangeType.ChildResourceChanged);
        resourceListUpdate.startRow = rowIndex;
        resourceListUpdate.endRow = rowIndex;
        resourceListUpdate.childResource = childResource;
        resourceListUpdate.childResourceChangeType = childResourceChangeType;
        return resourceListUpdate;
    }

    public ResourceListUpdate(EResourceListChangeType changeType) {
        this.type = changeType;
    }
}