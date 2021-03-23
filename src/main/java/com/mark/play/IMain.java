package com.mark.play;

import com.mark.io.IAppDataChangeListener;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
    boolean flipShowNavigator();
    void registerAppDataChangeListener(IAppDataChangeListener listener);
}
