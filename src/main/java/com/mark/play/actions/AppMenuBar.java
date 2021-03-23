package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.play.IMain;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class AppMenuBar extends JMenuBar {
    private IMain main;

    public AppMenuBar(IMain main) {
        this.main = main;
        this.add(buildFileMenu());
        this.add(buildViewMenu());
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

    private JMenu buildViewMenu() {
        JMenu menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);

        // [TODO] is there a way to encapsulate the initial check state with the action?
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(new ViewNavigatorAction(this.main));
        checkBoxMenuItem.setState(Prefs.isNavigatorVisible());
        menu.add(checkBoxMenuItem);

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
