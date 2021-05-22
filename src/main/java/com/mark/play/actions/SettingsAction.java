package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.settings.SettingsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SettingsAction extends AbstractAction {
    private IMain main;

    public SettingsAction(IMain main) {
        super("Settings...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //Log.log("Settings action");
        SettingsDialog settingsDialog = new SettingsDialog(main);
        settingsDialog.setLocation(main.getAppFrame().getOffsetLocation(100, 50));
        settingsDialog.setVisible(true);
    }
}