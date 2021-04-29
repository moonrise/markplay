package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RestoreFromHashStoreAction extends AbstractAction {
    private IMain main;

    public RestoreFromHashStoreAction(IMain main) {
        super("Restore...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String promptMessage = "Merge user data from Hash Store?";
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return;
        }

        int updateCount = main.getResourceList().restoreAllFromHashStore();
        main.displayInfoMessage(String.format("%d out of %d resources restore-merged.", updateCount, main.getResourceList().getResources().size()));
    }
}
