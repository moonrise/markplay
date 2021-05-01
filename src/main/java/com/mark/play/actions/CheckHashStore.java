package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CheckHashStore extends AbstractAction {
    private IMain main;

    public CheckHashStore(IMain main) {
        super("Hash Check...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int total = main.getResourceList().getResources().size();
        int notHashed = main.getResourceList().areAllFilesHashed();
        if (notHashed > 0) {
            String promptMessage = String.format("Not all files are hashed (%d/%d not hashed). Do you want to continue Hash Store check for hashed files only?", notHashed, total);
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                return;
            }
        }

        int yesCount = main.getResourceList().checkHashStore();
        main.displayInfoMessage(String.format("Total: %d, Hashed: %d, In Hash Store: %d (see temp column).", total, total-notHashed, yesCount));
    }
}
