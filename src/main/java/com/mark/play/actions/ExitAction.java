package com.mark.play.actions;

import com.mark.play.IMain;
import com.mark.play.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExitAction extends AbstractAction {
    private IMain main;

    public ExitAction(IMain main) {
        super("Exit");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Log.log("Exit action");
        this.main.exitApplication();
    }
}
