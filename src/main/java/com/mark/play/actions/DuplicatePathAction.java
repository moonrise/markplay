package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DuplicatePathAction extends AbstractAction {
    private IMain main;

    public DuplicatePathAction(IMain main) {
        super("Allow Duplicate Path") ;
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof JCheckBoxMenuItem) {
            Prefs.setAllowDuplicateResourcePath(!Prefs.isAllowDuplicateResourcePath());
            JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)actionEvent.getSource();
            checkBoxMenuItem.setState(Prefs.isAllowDuplicateResourcePath());
        }
        else if (source instanceof JToolBar) {
            // TODO: toolbar counter part?
            JToolBar toolBar = (JToolBar) actionEvent.getSource();
        }
    }
}
