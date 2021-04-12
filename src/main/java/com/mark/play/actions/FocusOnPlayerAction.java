package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FocusOnPlayerAction extends AbstractAction {
    private IMain main;

    public FocusOnPlayerAction(IMain main) {
        super("Focus On Player") ;
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof JCheckBoxMenuItem) {
            Prefs.setFocusOnPlayer(!Prefs.isFocusOnPlayer());
            JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)actionEvent.getSource();
            checkBoxMenuItem.setState(Prefs.isFocusOnPlayer());

        }
        else if (source instanceof JToolBar) {
            // TODO: toolbar counter part?
            JToolBar toolBar = (JToolBar) actionEvent.getSource();
        }
    }
}
