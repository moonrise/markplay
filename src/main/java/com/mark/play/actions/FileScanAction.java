package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FileScanAction extends AbstractAction {
    private IMain main;

    public FileScanAction(IMain main) {
        super("Scan Files...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.getResourceList().findDuplicates();
    }
}