package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FindDuplicates extends AbstractAction {
    private IMain main;

    public FindDuplicates(IMain main) {
        super("Find duplicates...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.getResourceList().findDuplicates();
    }
}