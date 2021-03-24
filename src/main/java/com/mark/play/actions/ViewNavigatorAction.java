package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ViewNavigatorAction extends AbstractAction {
    private IMain main;

    public ViewNavigatorAction(IMain main) {
        super("Navigator");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)actionEvent.getSource();
        checkBoxMenuItem.setState(main.flipShowNavigator());
    }
}
