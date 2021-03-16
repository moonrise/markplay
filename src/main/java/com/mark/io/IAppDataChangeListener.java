package com.mark.io;

public interface IAppDataChangeListener {
    void onResourceListLoaded(ResourceList resourceList);
    void onResourceListUnloaded(ResourceList resourceList);
    void onResourceListChanged(ResourceList resourceList);
}
