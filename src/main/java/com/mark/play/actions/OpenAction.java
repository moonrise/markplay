package com.mark.play.actions;

import com.mark.Log;
import com.mark.Utils;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class OpenAction extends AbstractAction {
    private IMain main;

    public OpenAction(IMain main) {
        super("Open...");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        FileDialog dialog = new FileDialog(main.getAppFrame(), "Select File to Open", FileDialog.LOAD);
        dialog.setVisible(true);

        if (dialog.getFile() != null) {
            //Log.log("chosen file: %s in directory: %s", dialog.getFile(), dialog.getDirectory());
            main.processFile(dialog.getDirectory() + dialog.getFile());
        }
    }
}
