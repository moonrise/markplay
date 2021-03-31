package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveAsAction extends AbstractAction {
    private IMain main;

    public SaveAsAction(IMain main) {
        super("Save As");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.saveCurrentResourceList(true);
    }
}
