package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearRecentFilesAction extends AbstractAction {
    private IMain main;

    public ClearRecentFilesAction(IMain main) {
        super("Clear Recent Files...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String promptMessage = "Clear all recent files?";
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            Prefs.clearRecentFiles();
        }
    }
}
