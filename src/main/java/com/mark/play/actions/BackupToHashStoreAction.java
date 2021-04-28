package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BackupToHashStoreAction extends AbstractAction {
    private IMain main;

    public BackupToHashStoreAction(IMain main) {
        super("Backup...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String promptMessage = "Backup all modified user data to HashStore?";
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        int updateCount = main.getResourceList().updateToHashStoreAll();
        main.displayInfoMessage(String.format("%d out of %d resources backed up.", updateCount, main.getResourceList().getResources().size()));
    }
}
