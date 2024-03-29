package com.mark.settings;

import com.mark.Prefs;
import com.mark.Utils;
import com.mark.main.IMain;
import com.mark.resource.ResourceList;
import com.mark.utils.ITableCellButtonClickListener;
import com.mark.utils.TableCellButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RootPathSetting extends JPanel {
    static class RootPref {
        // Separator should not be ":" to support Windows path like 'C:\'
        // Separator should not be ";" as it is used for multiple value delimiters in preferences
        final private String SEPARATOR = "=";

        public String pref = "";
        public String path = "";

        public RootPref(String pref, String path) {
            this.pref = pref;
            this.path = path;
        }

        public RootPref(String prefAndPath) {
            String[] pair = prefAndPath.split(SEPARATOR);
            if (pair.length > 0) {
                this.pref = pair[0];
            }
            if (pair.length > 1) {
                this.path = pair[1];
            }
        }

        public String toString() {
            return String.format("%s%s%s", pref, SEPARATOR, path);
        }
    }

    static class RootTableModel  extends AbstractTableModel {
        private String[] columnNames = {"Curr", "New", "Prefix", "Path", "Del"};
        private int[] columnWidths = {30, 30, 50, 350, 30};
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
                case 0:     // copy to current
                case 1:     // copy to new
                    return rowIndex;
                case 2:
                    return rootPref.pref;
                case 3:
                    return rootPref.path;
                case 4:     // delete row
                    return rowIndex;
            }
            return "<na>";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // the last row (the empty row for new addition) should not be deletable
            return !(rowIndex == data.size()-1 && columnIndex == 4);
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            boolean newRowAppended = false;
            String newValue = ((String)value).trim();

            RootPref rootPref =  data.get(rowIndex);
            if (columnIndex == 2 && !newValue.isEmpty()) {
                rootPref.pref = newValue;
                if (rowIndex == data.size()-1) {
                    // the last row edited for an addition. provide another empty row for add
                    data.add(new RootPref(""));
                    newRowAppended = true;
                }
            }
            else if (columnIndex == 3) {        // Path can be empty
                rootPref.path = newValue;
            }

            fireTableCellUpdated(rowIndex, columnIndex);
            if (newRowAppended) {
                fireTableRowsInserted(data.size()-1, data.size()-1);
            }
        }

        public int getColumnWidth(int column) {
            return columnWidths[column];
        }

        public void save() {
            ArrayList<String> prefs = new ArrayList<>();
            for (RootPathSetting.RootPref rootPrf : data) {
                // skip the empty row (the last one likely)
                if (!rootPrf.pref.trim().isEmpty()) {
                    prefs.add(rootPrf.toString());
                }
            }
            Prefs.setRootPrefixes(prefs.toArray(new String[] {}));
        }

        public void deleteRow(int rowIndex) {
            data.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    private IMain main;
    private JTable table;
    private RootTableModel tableModel;
    private String currentRootPath = "";
    private String newRootPath = "";
    private JLabel currentRoot;
    private JLabel newRoot;

    public RootPathSetting(IMain main) {
        this.main = main;

        setBorder(new EmptyBorder(8, 10, 8, 10));
        setLayout(new BorderLayout());
        add(buildPathRootTitle(), BorderLayout.NORTH);
        add(new JScrollPane(buildRootTable()));
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton setRootButton = new JButton("Set New Root");
        setRootButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JButton)e.getSource()).requestFocus();    // if the cell is still in edit mode
                tableModel.save(); // save the list to pref (in case edited)
                checkNewRootAndSave(); // save new root value
            }
        });
        panel.add(setRootButton);

        return panel;
    }

    private JPanel buildPathRootTitle() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 0, 14, 0));

        ResourceList resourceList = main.getResourceList();

        JLabel title = new JLabel(String.format("Resource Path Root (%s)", resourceList.getShortName()));
        title.setFont(new Font("helvetica", Font.PLAIN, 13));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title);

        currentRoot = new JLabel(getCurrentRootPathFormatted(resourceList.getRoot()));
        currentRoot.setFont(new Font("courier", Font.PLAIN, 12));
        currentRoot.setBorder(new EmptyBorder(0, 0, 6, 0));
        panel.add(currentRoot);

        newRoot = new JLabel(getNewRootPathFormatted(""));
        newRoot.setFont(new Font("courier", Font.BOLD, 12));
        newRoot.setForeground(Color.BLUE);
        panel.add(newRoot);

        return panel;
    }

    private String getCurrentRootPathFormatted(String newValue) {
        return String.format("current  : %s", newValue);
    }

    private String getNewRootPathFormatted(String newValue) {
        return String.format("new root : %s", newValue);
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

        buildButtonColumns();

        // do not select any rows as it will update the new root value (we do not want that).
        // if we select a row without setting the new value, then currently selected row cannot be assigned to the new
        // root until re-selected by user.
        /*
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
        */

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        return scrollPane;
    }

    private void buildButtonColumns() {
        // copy to current field
        ImageIcon upIcon = new ImageIcon(Utils.getResourcePath("/icons/navigation-090-white.png"));
        table.getColumnModel().getColumn(0).setCellRenderer(new TableCellButton(upIcon, null));
        table.getColumnModel().getColumn(0).setCellEditor(new TableCellButton(upIcon, new ITableCellButtonClickListener() {
            @Override
            public void onTableCellButtonClick(int rowIndex) {
                currentRootPath = tableModel.data.get(rowIndex).path;
                currentRoot.setText(getCurrentRootPathFormatted(currentRootPath));
            }
        }));

        // copy to current field
        ImageIcon upIcon2 = new ImageIcon(Utils.getResourcePath("/icons/navigation-090-button.png"));
        table.getColumnModel().getColumn(1).setCellRenderer(new TableCellButton(upIcon2, null));
        table.getColumnModel().getColumn(1).setCellEditor(new TableCellButton(upIcon2, new ITableCellButtonClickListener() {
            @Override
            public void onTableCellButtonClick(int rowIndex) {
                newRootPath = tableModel.data.get(rowIndex).path;
                newRoot.setText(getNewRootPathFormatted(newRootPath));
            }
        }));

        // delete column
        ImageIcon deleteIcon = new ImageIcon(Utils.getResourcePath("/icons/cross.png"));
        ImageIcon newIcon = new ImageIcon(Utils.getResourcePath("/icons/new.png"));
        TableCellButton button1 = new TableCellButton(deleteIcon, null);
        button1.setLastRowIcon(newIcon);
        button1.setTransparentButton(true);
        TableCellButton button2 = new TableCellButton(deleteIcon, new ITableCellButtonClickListener() {
            @Override
            public void onTableCellButtonClick(int rowIndex) {
                tableModel.deleteRow(rowIndex);
            }
        });
        button2.setLastRowIcon(newIcon);
        button2.setTransparentButton(true);
        table.getColumnModel().getColumn(4).setCellRenderer(button1);
        table.getColumnModel().getColumn(4).setCellEditor(button2);

        /* we have now up buttons for this with two targets
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                //Log.log("table row selected %d - %d, %s; %d", e.getFirstIndex(), e.getLastIndex(), e.getValueIsAdjusting(), selectedRows.length > 0 ? selectedRows[0] : -1);
                int selectedRows[] = table.getSelectedRows();
                if (selectedRows.length < 1) {
                    return;
                }

                newRootPath = tableModel.data.get(selectedRows[0]).path;
                newRoot.setText(getNewRootPathFormatted(newRootPath));
            }
        });
        */
    }

    private void setTableColumnWidths(JTable table, RootTableModel tableModel) {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            int width = tableModel.getColumnWidth(i);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }
    }

    private boolean checkNewRootAndSave() {
        if (main.getResourceList().getRoot().equals(newRootPath)) {
            return true;
        }

        String promptMessage = newRootPath.isEmpty() ? "Unset the root (empty root)?" : String.format("Set the root to '%s'?", newRootPath);
        if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(main.getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return true;
        }

        String errorMessage = main.getResourceList().setRoot(currentRootPath, newRootPath);
        if (errorMessage!=null) {
            main.displayErrorMessage(errorMessage);
            return false;
        }

        return true;
    }
}