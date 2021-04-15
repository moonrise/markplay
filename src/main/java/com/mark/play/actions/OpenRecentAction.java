package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class OpenRecentAction extends AbstractAction {
    private IMain main;
    private String path;

    public OpenRecentAction(IMain main, String path) {
        super(path);
        this.main = main;
        this.path = path;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (new File(path).exists()) {
            main.processFile(path);
        }
        else {
            main.displayInfoMessage(String.format("File '%s' does not exist", path));
            Prefs.removeRecentFile(path);
        }
    }
}
