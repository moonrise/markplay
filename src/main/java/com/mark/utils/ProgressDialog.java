package com.mark.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProgressDialog extends JDialog {
    public interface ProgressCancelListener {
        void onProgressCancel();
    }

    private JProgressBar progressBar;
    private JLabel statusText;
    private JButton cancelCloseButton;

    public ProgressDialog(Frame owner, String title, int jobCount) {
        super(owner, title, true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        build(jobCount);
        setLocationRelativeTo(getOwner());
    }

    private void build(int jobCount) {
        statusText = new JLabel("...");
        statusText.setPreferredSize(new Dimension(420, 20));
        statusText.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statusText);
        centerPanel.add((progressBar = new JProgressBar(0, jobCount)));
        centerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        add(BorderLayout.CENTER, centerPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        buttonPanel.add((cancelCloseButton = new JButton("Cancel")));
        add(BorderLayout.SOUTH, buttonPanel);

        pack();
    }

    public void setStatusText(String text) {
        statusText.setText(text);
    }

    public void setProgressValue(int value) {
        progressBar.setValue(value);
    }

    public void cancelToCloseButton() {
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        cancelCloseButton.setText("Close");
    }

    public void setCancelListener(ProgressCancelListener listener) {
        cancelCloseButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null && getDefaultCloseOperation() == JDialog.DO_NOTHING_ON_CLOSE) {
                    listener.onProgressCancel();
                }
                setVisible(false);
            }
        });
    }
}