package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.resource.ResourceList;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CheckHashStore extends AbstractAction {
    private IMain main;

    public CheckHashStore(IMain main) {
        super("Check Store...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int total = main.getResourceList().getResources().size();
        ResourceList.HashStat hashStat = main.getResourceList().areAllFilesHashed();
        if (hashStat.notHashed > 0) {
            String promptMessage = String.format("Not all files are hashed (total: %d, hashed: %d, not-hashed: %d, no-files:%d). Do you want to continue Hash Store check for hashed files only?",
                    total, total - hashStat.zeroSize - hashStat.notHashed, hashStat.notHashed, hashStat.zeroSize);
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }

        int yesCount = main.getResourceList().checkHashStore();
        if (yesCount >= 0) {
            main.displayInfoMessage(String.format("%d in Hash Store out of Hashed Files: %d, Zero files: %d (see Temp column for details).",
                    yesCount, total - hashStat.notHashed - hashStat.zeroSize, hashStat.zeroSize));
        }
    }
}
