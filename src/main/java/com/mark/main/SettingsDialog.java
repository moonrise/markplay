package com.mark.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog implements ActionListener {
    private String[] data;
    private JTextField descBox;
    private JComboBox<String> colorList;
    private JButton btnOk;
    private JButton btnCancel;

    public SettingsDialog(Frame parent) {
        super(parent, "Enter data", true);
        Point loc = parent.getLocation();
        setLocation(loc.x + 80, loc.y + 80);
        data = new String[2]; // set to amount of data items
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        JLabel descLabel = new JLabel("Description:");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(descLabel, gbc);
        descBox = new JTextField(30);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(descBox, gbc);
        JLabel colorLabel = new JLabel("Choose color:");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(colorLabel, gbc);
        String[] colorStrings = {"red", "yellow", "orange", "green", "blue"};
        colorList = new JComboBox<String>(colorStrings);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(colorList, gbc);
        JLabel spacer = new JLabel(" ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(spacer, gbc);
        btnOk = new JButton("Ok");
        btnOk.addActionListener(this);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(btnOk, gbc);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnCancel, gbc);
        getContentPane().add(panel);
        pack();
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == btnOk) {
            data[0] = descBox.getText();
            data[1] = (String) colorList.getSelectedItem();
        } else {
            data[0] = null;
        }
        dispose();
    }

    public String[] run() {
        this.setVisible(true);
        return data;
    }
}