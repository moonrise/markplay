package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class HashFiles extends AbstractAction {
    private IMain main;

    public HashFiles(IMain main) {
        super("Hash Files...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.getResourceList().hashFiles();
    }
}