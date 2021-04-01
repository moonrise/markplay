package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
        main.processFile(path);
    }
}
