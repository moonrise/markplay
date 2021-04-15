package com.mark.play.actions;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ScanAction extends AbstractAction {
    private IMain main;

    public ScanAction(IMain main) {
        super("Scan...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a directory to scan and add media files from");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String directory = Prefs.getRecentDirectory();
        if (!directory.isEmpty() && new File(directory).exists()) {
            fileChooser.setCurrentDirectory(new File(directory));
        }

        int option = fileChooser.showOpenDialog(main.getAppFrame());
        if(option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            //Log.log("chosen scan directory: %s", file.getPath());
            Prefs.setRecentDirectory(file.getPath());
            main.processDirectory(file.getPath());
        }
    }
}