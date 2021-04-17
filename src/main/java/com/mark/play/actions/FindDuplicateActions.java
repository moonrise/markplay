package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FindDuplicateActions extends AbstractAction {
    private IMain main;

    public FindDuplicateActions(IMain main) {
        super("Find Duplicates...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int duplicateSets = main.getResourceList().findDuplicates();
        main.displayInfoMessage(String.format("%d duplicate entries found based on file sizes and hashes (see duplicate column).", duplicateSets));
    }
}
