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

    public static ResourceListUpdate Saved = new ResourceListUpdate(EResourceListChangeType.Saved);

    public static ResourceListUpdate RowsAdded(int startRow, int endRow) {
        ResourceListUpdate resourceListUpdate = new ResourceListUpdate(EResourceListChangeType.RowsAdded);
        resourceListUpdate.startRow = startRow;
        resourceListUpdate.endRow = endRow;
        return resourceListUpdate;
    }

    public static ResourceListUpdate ChildResourceChanged(Resource childResource, EResourceChangeType childResourceChangeType) {
        ResourceListUpdate resourceListUpdate = new ResourceListUpdate(EResourceListChangeType.ChildResourceChanged);
        resourceListUpdate.childResource = childResource;
        resourceListUpdate.childResourceChangeType = childResourceChangeType;
        return resourceListUpdate;
    }

    public ResourceListUpdate(EResourceListChangeType changeType) {
        this.type = changeType;
    }
}