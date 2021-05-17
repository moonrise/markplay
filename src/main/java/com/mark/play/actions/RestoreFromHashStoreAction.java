package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.resource.ResourceList;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RestoreFromHashStoreAction extends AbstractAction {
    private IMain main;

    public RestoreFromHashStoreAction(IMain main) {
        super("Hash Restore...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        ResourceList.HashStat hashStat = main.getResourceList().areAllFilesHashed();
        if (hashStat.notHashed > 0) {
            main.displayInfoMessage(String.format("%d files are not hashed. All files should be hashed for restore operation.", hashStat.notHashed));
            return;
        }

        String promptMessage = "Merge user data from Hash Store?";
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        ResourceList.RestoreHashResult result = main.getResourceList().restoreAllFromHashStore();
        main.displayInfoMessage(String.format("Total: %d, Merged[1]: %d, Not in hash store[-1]: %d, Error[-2]: %d (refer to Temp column for [N]).",
                main.getResourceList().getResources().size(), result.merged, result.notInHashStore, result.error));
    }
}
