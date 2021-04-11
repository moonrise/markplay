package com.mark.main;

import com.mark.Prefs;
import com.mark.Utils;
import com.mark.utils.TableCellButton;

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
        public String pref = "";
        public String path = "";

        public RootPref(String pref, String path) {
            this.pref = pref;
            this.path = path;
        }

        public RootPref(String prefAndPath) {
            String[] pair = prefAndPath.split(":");
            if (pair.length > 0) {
                this.pref = pair[0];
            }
            if (pair.length > 1) {
                this.path = pair[1];
            }
        }

        public String toString() {
            return String.format("%s:%s", pref, path);
        }
    }

    static class RootTableModel  extends AbstractTableModel {
        private String[] columnNames = {"", "Prefix", "Path"};
        private int[] columnWidths = {30, 50, 200};
        private ArrayList<RootPref> data = new ArrayList<>();

        public RootTableModel() {
            // initialize the data from preferences
            for (String prefix : Prefs.getRootPrefixes()) {
                data.add(new RootPref(prefix));
            }

            // append the last row always to support new addition
            data.add(new RootPref(""));
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
            switch (columnIndex) {
                case 0:
                    return rowIndex;        // delete column retains the row index as the value
                case 1:
                    return rootPref.pref;
                case 2:
                    return rootPref.path;
            }
            return "<na>";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            boolean newRowAppended = false;
            String newValue = ((String)value).trim();

            RootPref rootPref =  data.get(rowIndex);
            if (columnIndex == 1 && !newValue.isEmpty()) {
                rootPref.pref = newValue;
                if (rowIndex == data.size()-1) {
                    // the last row edited for an addition. provide another empty row for add
                    data.add(new RootPref(""));
                    newRowAppended = true;
                }
            }
            else if (columnIndex == 2) {        // Path can be empty
                rootPref.path = newValue;
            }

            if (newRowAppended) {
                fireTableStructureChanged();
            }
            else {
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        public int getColumnWidth(int column) {
            return columnWidths[column];
        }

        public void save() {
            ArrayList<String> prefs = new ArrayList<>();
            for (SettingsDialog.RootPref rootPrf : data) {
                // skip the empty row (the last one likely)
                if (!rootPrf.pref.trim().isEmpty()) {
                    prefs.add(rootPrf.toString());
                }
            }
            Prefs.setRootPrefixes(prefs.toArray(new String[] {}));
        }
    }

    private JTable table;
    private RootTableModel tableModel;

    public SettingsDialog(Frame parent) {
        super(parent, "Settings", true, parent.getGraphicsConfiguration());
        setMaximumSize(new Dimension(400, 200));

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(15, 30, 10, 30));
        topPanel.setLayout(new BorderLayout());

        topPanel.add(buildPathRootTitle(), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(buildRootTable()));

        topPanel.add(buildDialogButtonPanel(), BorderLayout.SOUTH);
        getContentPane().add(topPanel);
        pack();
    }

    private JLabel buildPathRootTitle() {
        JLabel label = new JLabel("Resource Path Root Prefixes");
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        return label;
    }

    private JScrollPane buildRootTable() {
        table = new JTable();
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        tableModel = new RootTableModel();
        table.setModel(tableModel);
        setTableColumnWidths(table, tableModel);

        ImageIcon deleteIcon = new ImageIcon(Utils.getResourcePath("/icons/cross.png"));
        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellButton(deleteIcon));
        table.getColumnModel().getColumn(0).setCellEditor(new TableCellButton(deleteIcon));

        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        return scrollPane;
    }

    private void setTableColumnWidths(JTable table, RootTableModel tableModel) {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int width = tableModel.getColumnWidth(i);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }
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
                tableModel.save();
                setVisible(false);
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