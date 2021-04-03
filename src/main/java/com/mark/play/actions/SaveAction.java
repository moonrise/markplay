package com.mark.play.actions;

import com.mark.Utils;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SaveAction extends AbstractAction {
    private IMain main;

    public SaveAction(IMain main) {
        super("Save");
        // TODO: icon version will need a tooltip
        //super("Save", new ImageIcon(Utils.getResourcePath("/icons/disk.png"), "Save"));
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        main.saveCurrentResourceList(false);
    }
}
