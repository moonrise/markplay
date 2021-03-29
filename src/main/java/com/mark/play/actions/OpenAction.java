package com.mark.play.actions;

import com.mark.Log;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class OpenAction extends AbstractAction {
    private IMain main;

    public OpenAction(IMain main) {
        super("Open");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        chooseFile2();
    }

    private void chooseFile1() {
        JFileChooser chooser = new JFileChooser();
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
        //chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(main.getAppFrame());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            Log.log("You chose to open this file: " + chooser.getSelectedFile().getName());
        }
    }

    private void chooseFile2() {
        FileDialog dialog = new FileDialog(main.getAppFrame(), "Select File to Open", FileDialog.LOAD);
        dialog.setVisible(true);

        if (dialog.getFile() != null) {
            //Log.log("chosen file: %s in directory: %s", dialog.getFile(), dialog.getDirectory());
            openFile(dialog.getDirectory() + dialog.getFile());
        }
    }

    private void openFile(String filePath) {
        main.processFile(filePath);
    }
}
