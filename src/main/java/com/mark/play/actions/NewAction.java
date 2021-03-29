package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class NewAction extends AbstractAction {
    private IMain main;

    public NewAction(IMain main) {
        super("New");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.processFile(null);
    }
}
