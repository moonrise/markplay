package com.mark.io;

import com.mark.Log;

public abstract class AppDataChangeListenerAdaptor implements IAppDataChangeListener {
    @Override
    public void onResourceListLoaded(ResourceList resourceList) {
        Log.log("resource list '%s' loaded", resourceList.getName());
    }

    @Override
    public void onResourceListUnloaded(ResourceList resourceList) {
        Log.log("resource list '%s' unloaded", resourceList.getName());
    }

    @Override
    public void onResourceListChanged(ResourceList resourceList) {
        Log.log("resource list '%s' changed", resourceList.getName());
    }
}
