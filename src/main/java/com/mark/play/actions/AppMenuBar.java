package com.mark.play.actions;

import com.mark.Prefs;
import com.mark.main.IMain;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
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

        menu.add(new NewAction(main));
        menu.add(new OpenAction(main));
        menu.add(new SaveAction(main));
        menu.add(new SaveAsAction(main));
        menu.add(new CloseAction(main));
        menu.addSeparator();
        menu.add(buildRecentFilesMenu());
        menu.addSeparator();
        menu.add(new ExitAction(main));

        return menu;
    }

    private JMenu buildRecentFilesMenu() {
        JMenu recentFilesMenu = new JMenu("Recent Files...");

        recentFilesMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                addRecentFiles((JMenu)e.getSource());
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

        return recentFilesMenu;
    }

    private void addRecentFiles(JMenu menu) {
        menu.removeAll();

        String[] recentFiles = Prefs.getRecentFiles();
        for (String recentFile : recentFiles) {
            menu.add(new OpenRecentAction(main, recentFile));
        }
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

        menu.add(new HelpAction(main));
        menu.addSeparator();
        menu.add(new AboutAction(main));

        return menu;
    }
}
