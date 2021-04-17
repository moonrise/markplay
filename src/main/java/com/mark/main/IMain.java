package com.mark.main;

import com.mark.resource.IResourceListChangeListener;
import com.mark.resource.ResourceList;
import com.mark.resource.ResourceListUpdate;

import java.awt.event.KeyEvent;

public interface IMain {
    MainFrame getAppFrame();
    void processPlayerKeys(KeyEvent e);
    void updateAppHeader();
    void displayInfoMessage(String message);
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerResourceListChangeListener(IResourceListChangeListener listener);
    void notifyResourceListChange(ResourceList resourceList, ResourceListUpdate update);
    void processFile(String filePath);
    void processPaths(String[] paths, boolean dndAlternate);
    void processDirectory(String directoryPath);
    void saveCurrentResourceList(boolean saveAs);
    void navigateResourceList(boolean forward, boolean favorite);
    ResourceList getResourceList();
}
