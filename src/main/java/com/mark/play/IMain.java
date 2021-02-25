package com.mark.play;

import javax.swing.*;

public interface IMain {
    JFrame getAppFrame();
    void displayErrorMessage(String message);
    void exitApplication();
}
