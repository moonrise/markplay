package com.mark.settings;

import com.mark.main.IMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    private IMain main;

    public SettingsDialog(IMain main) {
        super(main.getAppFrame(), "Settings", true, main.getAppFrame().getGraphicsConfiguration());
        this.main = main;

        setPreferredSize(new Dimension(600, 400));
        setMaximumSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(400, 300));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));
        topPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Root Path", new RootPathSetting(main));
        tabbedPane.add("Hash Store", new HashStoreSetting(main));

        topPanel.add(tabbedPane);
        topPanel.add(buildDialogButtonPanel(), BorderLayout.SOUTH);

        getContentPane().add(topPanel);
        pack();
    }

    private JPanel buildDialogButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        //panel.add(buildDialogOKButton()); // each tab has its own handler now
        panel.add(buildDialogCloseButton());
        return panel;
    }

    private JButton buildDialogOKButton() {
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton)e.getSource()).requestFocus();    // if the cell is still in edit mode
            }
        });
        return button;
    }

    private JButton buildDialogCloseButton() {
        JButton button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        return button;
    }
}