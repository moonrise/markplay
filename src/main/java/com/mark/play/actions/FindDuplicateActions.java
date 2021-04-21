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
        main.getResourceList().findDuplicates();
    }
}
