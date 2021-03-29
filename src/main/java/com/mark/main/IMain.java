package com.mark.main;

import com.mark.resource.EResourceListChangeType;
import com.mark.resource.IResourceListChangeListener;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerResourceListChangeListener(IResourceListChangeListener listener);
    void processFile(String filePath);
}
