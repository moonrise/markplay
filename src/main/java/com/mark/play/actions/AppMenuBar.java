package com.mark.play.actions;

import com.mark.play.IMain;
import com.mark.play.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class AppMenuBar extends JMenuBar {
    private IMain main;

    public AppMenuBar(IMain main) {
        this.main = main;
        this.add(buildFileMenu());
        this.add(buildHelpMenu());
    }

    private JMenu buildFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        menu.add(new JMenuItem(new SaveAction()));
        menu.addSeparator();
        menu.add(new JMenuItem(new ExitAction(this.main)));

        return menu;
    }

    private JMenu buildHelpMenu() {
        JMenu menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

        menu.add(new JMenuItem(new HelpAction(this.main)));
        menu.addSeparator();
        menu.add(new JMenuItem(new AboutAction(this.main)));

        return menu;
    }
}
