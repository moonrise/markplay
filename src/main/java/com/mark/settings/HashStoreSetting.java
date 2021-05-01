package com.mark.settings;

import com.mark.main.IMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HashStoreSetting extends JPanel {
    private IMain main;

    public HashStoreSetting(IMain main) {
        this.main = main;

        setBorder(new EmptyBorder(8, 10, 8, 10));
        setLayout(new BorderLayout());

        // header
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(buildHashDBHeaderPanel());
        add(leftPanel, BorderLayout.NORTH);

        // content
        add(new JScrollPane(hashDBContentPanel()));

        // buttons
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton setFilePathButton = new JButton("Set File Path");
        setFilePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton) e.getSource()).requestFocus();    // if the cell is still in edit mode
            }
        });
        panel.add(setFilePathButton);

        return panel;
    }

    private JPanel buildHashDBHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));

        panel.add(labelWithFG("Hash Store DB Path (*.db):", Color.DARK_GRAY), gbcSimple(0, 0));
        JTextField pathEditor = new JTextField("Hello");
        pathEditor.setPreferredSize(new Dimension(350, 22));
        panel.add(pathEditor, gbcFill(1, 0));

        panel.add(labelWithFG("Current Entry Count:", Color.DARK_GRAY), gbcSimple(0, 1));
        panel.add(labelWithFG("123", Color.BLUE), gbcFill(1, 1));

        return panel;
    }

    private GridBagConstraints gbcSimple(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 5, 10);
        return gbc;
    }

    private GridBagConstraints gbcFill(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(0, 0, 5, 10);
        return gbc;
    }

    private JLabel labelWithFG(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        return label;
    }

    private JScrollPane hashDBContentPanel() {
        JTextArea textArea = new JTextArea("Text...");
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        return scrollPane;
    }
}