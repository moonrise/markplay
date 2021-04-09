package com.mark.main;

import com.mark.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SettingsDialog extends JDialog {
    static class RootPref {
        String pref;
        String path;

        public RootPref(String pref, String path) {
            this.pref = pref;
            this.path = path;
        }

        public RootPref(String prefAndPath) {
            String[] pair = prefAndPath.split(":");
            this.pref = pair[0];
            this.path = pair[1];
        }

        public String toString() {
            return String.format("%s:%s", pref, path);
        }
    }

    static class RootTableModel  extends AbstractTableModel {
        private String[] columnNames = {"Prefix", "Path"};
        private int[] columnWidths = {50, 200};
        private ArrayList<RootPref> data = new ArrayList<>();

        public RootTableModel() {
            data.add(new RootPref("a", "b"));
            data.add(new RootPref("x", "y"));
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RootPref rootPref =  data.get(rowIndex);
            return columnIndex == 0 ? rootPref.pref : rootPref.path;
        }

        public int getColumnWidth(int column) {
            return columnWidths[column];
        }
    }

    private RootTableModel tableModel;

    public SettingsDialog(Frame parent) {
        super(parent, "Settings", true, parent.getGraphicsConfiguration());
        setMaximumSize(new Dimension(400, 200));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));
        topPanel.setLayout(new BorderLayout());

        topPanel.add(getPathRootTitle(), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(getRootTable()));

        topPanel.add(getDialogButtonPanel(), BorderLayout.SOUTH);
        getContentPane().add(topPanel);
        pack();
    }

    private void setTableColumnWidths(JTable table, RootTableModel tableModel) {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int width = tableModel.getColumnWidth(i);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }
    }

    private JLabel getPathRootTitle() {
        JLabel label = new JLabel("Resource Path Root Prefixes");
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        return label;
    }

    private JScrollPane getRootTable() {
        JTable table = new JTable();
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);

        RootTableModel tableModel = new RootTableModel();
        table.setModel(tableModel);
        setTableColumnWidths(table, tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        return scrollPane;
    }

    private JPanel getDialogButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(getDialogOKButton());
        panel.add(getDialogCancelButton());
        return panel;
    }

    private JButton getDialogOKButton() {
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        return button;
    }

    private JButton getDialogCancelButton() {
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