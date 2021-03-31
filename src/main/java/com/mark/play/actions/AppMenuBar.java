package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class AppMenuBar extends JMenuBar {
    private IMain main;

    public AppMenuBar(IMain main) {
        this.main = main;
        add(buildFileMenu());
        add(buildViewMenu());
        add(buildHelpMenu());
    }

    private JMenu buildFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        menu.add(new JMenuItem(new NewAction(main)));
        menu.add(new JMenuItem(new OpenAction(main)));
        menu.add(new JMenuItem(new SaveAction(main)));
        menu.add(new JMenuItem(new SaveAsAction(main)));
        menu.add(new JMenuItem(new CloseAction(main)));
        menu.addSeparator();
        menu.add(new JMenuItem(new ExitAction(main)));

        return menu;
    }

    private JMenu buildViewMenu() {
        JMenu menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);

        // [TODO] is there a way to encapsulate the initial check state with the action?
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(new ViewNavigatorAction(main));
        checkBoxMenuItem.setState(Prefs.isNavigatorVisible());
        menu.add(checkBoxMenuItem);

        return menu;
    }

    private JMenu buildHelpMenu() {
        JMenu menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

        menu.add(new JMenuItem(new HelpAction(main)));
        menu.addSeparator();
        menu.add(new JMenuItem(new AboutAction(main)));

        return menu;
    }
}
