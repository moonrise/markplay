package com.mark.play.actions;

import com.mark.Utils;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ViewNavigatorAction extends AbstractAction {
    private IMain main;

    public ViewNavigatorAction(IMain main) {
        super("Show List", new ImageIcon(Utils.getResourcePath("/icons/text_list_bullets.png"), "Show List"));
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem)actionEvent.getSource();
        checkBoxMenuItem.setState(main.flipShowNavigator());
    }
}
