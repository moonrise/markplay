package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AboutAction extends AbstractAction {
    private IMain main;

    public AboutAction(IMain main) {
        super("About...");
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        //Log.log("About action");
        // [TODO] On MacOS, non null first param does not show the content area the second time and on
        JOptionPane.showMessageDialog(null, //main.getAppFrame(),
                "Mark and Play 0.1",
                String.format("About %s", Utils.AppName),
                JOptionPane.INFORMATION_MESSAGE);
    }
}
