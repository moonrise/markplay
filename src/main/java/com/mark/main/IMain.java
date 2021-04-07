package com.mark.main;

import com.mark.resource.IResourceListChangeListener;
import com.mark.resource.ResourceList;
import com.mark.resource.ResourceListUpdate;

public interface IMain {
    MainFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerResourceListChangeListener(IResourceListChangeListener listener);
    void notifyResourceListChange(ResourceList resourceList, ResourceListUpdate update);
    void processFile(String filePath);
    void saveCurrentResourceList(boolean saveAs);
    void navigateResourceList(boolean forward, boolean favorite);
}
