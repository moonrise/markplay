package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearFileSizesAndHashesAction extends AbstractAction {
    private IMain main;

    public ClearFileSizesAndHashesAction(IMain main) {
        super("Clear File Sizes/Hashes...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String promptMessage = "Clear all media file sizes and hashes (will be recomputed on load)?";
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            main.getResourceList().clearAllFileSizesAndHashes();
        }
    }
}
