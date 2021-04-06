package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PlayOnLoadAction extends AbstractAction {
    private IMain main;

    public PlayOnLoadAction(IMain main) {
        super("Play On Load") ;
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof JCheckBoxMenuItem) {
            Prefs.setPlayOnLoad(!Prefs.isPlayOnLoad());
            JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)actionEvent.getSource();
            checkBoxMenuItem.setState(Prefs.isPlayOnLoad());

        }
        else if (source instanceof JToolBar) {
            // TODO: toolbar counter part?
            JToolBar toolBar = (JToolBar) actionEvent.getSource();
        }
    }
}
