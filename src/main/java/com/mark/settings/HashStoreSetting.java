package com.mark.settings;

import com.mark.Prefs;
import com.mark.main.IMain;
import com.mark.utils.HashStore;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class HashStoreSetting extends JPanel {
    private IMain main;
    private JTextField pathEditor;
    private JTextArea contentArea;

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

        panel.add(buildSetFilePathAction());
        panel.add(buildLoadAllFromHashDB());

        return panel;
    }

    private JButton buildSetFilePathAction() {
        JButton setFilePathButton = new JButton("Set File Path");
        setFilePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton) e.getSource()).requestFocus();    // if the cell is still in edit mode
                setHashStoreDBPath();
            }
        });
        return setFilePathButton;
    }

    private JButton buildLoadAllFromHashDB() {
        JButton setFilePathButton = new JButton("Load All");
        setFilePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton) e.getSource()).requestFocus();    // if the cell is still in edit mode
                loadAllFromHashStore();
            }
        });
        return setFilePathButton;
    }

    private void setHashStoreDBPath() {
        String currentPath = Prefs.getHashStoreDBPath();
        String newPath = pathEditor.getText().trim();
        String promptMessage = "";

        if (newPath.isEmpty()) {
            promptMessage = "Empty path. Remove DB path? Hash store operations will not be performed.";
            if (JOptionPane.YES_NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                Prefs.setHashStoreDBPath("");
            }
            return;
        }

        // normalize the new path with the extension
        if (!FilenameUtils.getExtension(newPath).equalsIgnoreCase("db")) {
            newPath = new File(newPath + ".db").getAbsolutePath();
        }
        else {
            newPath = new File(newPath).getAbsolutePath();
        }

        // sanity check
        if (currentPath.equals(newPath)) {
            main.displayInfoMessage("New Hash Store DB Path is the same as the current path; Not set.");
            return;
        }

        File dbFile = new File(newPath);
        if (dbFile.exists()) {
            promptMessage = String.format("'%s' exists and can be used. Continue?", newPath);
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                return;
            }

            // now set the new path
            Prefs.setHashStoreDBPath(dbFile.getPath());
        }
        else {
            promptMessage = String.format("'%s' does not exist. A new DB will be created. Continue?", newPath);
            if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
                return;
            }

            // now set the new path and create one
            Prefs.setHashStoreDBPath(dbFile.getPath());
            HashStore hashStore = new HashStore();
            hashStore.close();
        }
    }

    private void loadAllFromHashStore() {
        HashStore hashStore = new HashStore();
        contentArea.setText(hashStore.getAllHashEntriesAsString());
        hashStore.close();
    }

    private JPanel buildHashDBHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));

        panel.add(labelWithFG("Hash Store DB Path (*.db):", Color.DARK_GRAY), gbcSimple(0, 0));
        pathEditor = new JTextField(Prefs.getHashStoreDBPath());
        pathEditor.setPreferredSize(new Dimension(330, 22));
        panel.add(pathEditor, gbcFill(1, 0));

        panel.add(labelWithFG("Current Entry Count:", Color.DARK_GRAY), gbcSimple(0, 1));
        panel.add(labelWithFG("" + HashStore.getCurrentHashStoreSize(), Color.BLUE), gbcFill(1, 1));

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
        contentArea = new JTextArea("");
        contentArea.setFont(new Font("courier", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        return scrollPane;
    }
}