package com.mark.main;

import com.mark.resource.EResourceListChangeType;
import com.mark.resource.IResourceListChangeListener;
import com.mark.resource.ResourceList;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerResourceListChangeListener(IResourceListChangeListener listener);
    void notifyResourceListChange(ResourceList resourceList, EResourceListChangeType changeType);
    void processFile(String filePath);
}
