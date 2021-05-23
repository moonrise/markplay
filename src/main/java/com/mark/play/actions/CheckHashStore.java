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
            String promptMessage = String.format("Not all files are hashed (%d/%d not hashed). Do you want to continue Hash Store check for hashed files only?",
                    hashStat.notHashed, total - hashStat.zeroSize);
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }

        int yesCount = main.getResourceList().checkHashStore();
        if (yesCount >= 0) {
            main.displayInfoMessage(String.format("In hash store: %d, out of Hashed: %d, Zero files: %d (see Temp column for details).",
                    yesCount, total-hashStat.notHashed-hashStat.zeroSize, hashStat.zeroSize));
        }
    }
}
