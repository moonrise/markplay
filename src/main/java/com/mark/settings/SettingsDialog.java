package com.mark.settings;

import com.mark.main.IMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private IMain main;
    private RootPathSetting rootPathSetting;

    public SettingsDialog(IMain main) {
        super(main.getAppFrame(), "Settings", true, main.getAppFrame().getGraphicsConfiguration());
        this.main = main;

        setPreferredSize(new Dimension(600, 400));
        setMaximumSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(400, 300));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));
        topPanel.setLayout(new BorderLayout());

        this.rootPathSetting = new RootPathSetting(main);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Root Path", this.rootPathSetting);

        topPanel.add(tabbedPane);
        topPanel.add(buildDialogButtonPanel(), BorderLayout.SOUTH);

        getContentPane().add(topPanel);
        pack();
    }

    private JPanel buildDialogButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(buildDialogOKButton());
        panel.add(buildDialogCancelButton());
        return panel;
    }

    private JButton buildDialogOKButton() {
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton)e.getSource()).requestFocus();    // if the cell is still in edit mode
                setVisible(!rootPathSetting.onSettingsDialogOKButtonPressed());
            }
        });
        return button;
    }

    private JButton buildDialogCancelButton() {
        JButton button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        return button;
    }

    public void display() {
        Point parentLocation = getParent().getLocation();
        parentLocation.translate(100, 50);
        setLocation(parentLocation);
        setVisible(true);
    }
}