package com.mark.main;

import com.mark.io.IAppDataChangeListener;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerAppDataChangeListener(IAppDataChangeListener listener);
    void processFile(String filePath);
}
