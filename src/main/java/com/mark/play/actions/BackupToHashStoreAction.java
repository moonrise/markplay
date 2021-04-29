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
        int notHashed = main.getResourceList().areAllFilesHashed();
        if (notHashed > 0) {
            main.displayInfoMessage(String.format("%d files are not hashed. All files should be hashed for backup operation.", notHashed));
            return;
        }

        String promptMessage = "Backup user data to Hash Store (Only more recent data will be backed up)?";
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        int updateCount = main.getResourceList().updateAllToHashStore();
        main.displayInfoMessage(String.format("%d out of %d resources backed up.", updateCount, main.getResourceList().getResources().size()));
    }
}
