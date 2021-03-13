package com.mark.play;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void updateAppHeader();
    void displayErrorMessage(String message);
    void exitApplication();
}
