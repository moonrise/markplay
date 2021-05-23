package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.resource.ResourceList;

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
        ResourceList.HashStat hashStat = main.getResourceList().areAllFilesHashed();
        if (hashStat.notHashed > 0) {
            main.displayInfoMessage(String.format("Not all files are hashed (%d not hashed, %d zero size files out of %d). Hash them first to find duplicates.",
                    hashStat.notHashed, hashStat.zeroSize, main.getResourceList().size()));
        }
        else {
            main.getResourceList().findDuplicates();
        }
    }
}