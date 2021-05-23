package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.resource.ResourceList;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BackupToHashStoreAction extends AbstractAction {
    private IMain main;

    public BackupToHashStoreAction(IMain main) {
        super("Backup to Store...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ResourceList.HashStat hashStat = main.getResourceList().areAllFilesHashed();
        if (hashStat.notHashed > 0) {
            main.displayInfoMessage(String.format("%d files are not hashed. All files should be hashed for backup operation.", hashStat.notHashed));
            return;
        }

        String promptMessage = "Backup user data to Hash Store (Only more recent data will be backed up)?";
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        int updateCount = main.getResourceList().updateAllToHashStore();
        if (updateCount >= 0) {
            main.displayInfoMessage(String.format("%d out of %d resources backed up.", updateCount, main.getResourceList().getResources().size()));
        }
    }
}
