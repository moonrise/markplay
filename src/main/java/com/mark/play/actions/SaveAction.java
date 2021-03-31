package com.mark.play.actions;

import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SaveAction extends AbstractAction {
    private IMain main;

    public SaveAction(IMain main) {
        super("Save");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.saveCurrentResourceList(false);
    }
}
