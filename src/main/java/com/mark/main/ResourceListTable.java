package com.mark.main;

import com.mark.Utils;
import com.mark.resource.ResourceList;
import com.mark.utils.ITableCellButtonClickListener;
import com.mark.utils.TableCellButton;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.*;
import java.util.EventObject;

public class ResourceListTable extends JTable implements KeyListener {
    static class LongRenderer extends DefaultTableCellRenderer {
        public LongRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(String.format("%,d", (long)value));
        }
    }

    static class FloatRenderer extends DefaultTableCellRenderer {
        public FloatRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(0 == ((float)value) ? "" : String.format("%.1f", (float)value));
        }
    }

    static class ZeroSpaceRenderer extends DefaultTableCellRenderer {
        public ZeroSpaceRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            int n = (int)value;
            super.setValue(n == 0 ? "" : String.format("%,d", n));
        }
    }

    static class DurationRenderer extends DefaultTableCellRenderer {
        public DurationRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(Utils.getTimelineFormatted((long)value, false));
        }
    }

    static class TempValueRenderer extends ZeroSpaceRenderer {
        public TempValueRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            int n = (int)value;

            switch (n) {
                case ResourceListTableModel.TEMP_ERROR:
                    setText("Error");
                    break;
                case ResourceListTableModel.TEMP_NO_FILE:
                    setText("No file");
                    break;
                case ResourceListTableModel.TEMP_HASHED:
                    setText("Hashed");
                    break;
                case ResourceListTableModel.TEMP_NOT_HASHED:
                    setText("Not hashed");
                    break;
                case ResourceListTableModel.TEMP_IN_HASHSTORE:
                    setText("In hash");
                    break;
                case ResourceListTableModel.TEMP_NOT_IN_HASHSTORE:
                    setText("Not in hash");
                    break;
                case ResourceListTableModel.TEMP_RESTORE_MERGED:
                    setText("Merged");
                    break;
                case ResourceListTableModel.TEMP_MERGED:
                    setText("Merged");
                    break;
                case ResourceListTableModel.TEMP_ADDED:
                    setText("Added");
                    break;
                default:
                    super.setValue(n);
                    break;
            }
        }
    }

    static class MyTextEditor extends DefaultCellEditor {
        public MyTextEditor() {
            super(new JTextField());
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent)e;
                if (mouseEvent.isControlDown()) {
                    return super.isCellEditable(e);
                }
            }
            return false;
        }
    }

    static class MyFloatEditor extends MyTextEditor implements ActionListener {
        public MyFloatEditor() {
            JTextField textField = (JTextField)getComponent();
            textField.addActionListener(this);
        }

        public void actionPerformed(ActionEvent evt) {
            JTextField textField = (JTextField)evt.getSource();
            String textValue = textField.getText();

            try {
                Float.parseFloat(textValue);
            } catch (Exception e) {
                textField.setText(validateRateValue(textValue));
            }
        }

        private String validateRateValue(String value) {
            String floatValue = "";
            int decimalCount = 0;
            for (char c : value.toCharArray()) {
                if (Character.isDigit(c) || (c == '.' && ++decimalCount == 1)) {
                    floatValue += c;
                }
            }
            return floatValue;
        }
    }

    private IMain main;
    private ResourceListTableModel tableModel;
    private TableColumnModel columnModel;

    public ResourceListTable(IMain main, ResourceList resourceList) {
        super(new ResourceListTableModel(resourceList));

        this.main = main;
        this.tableModel = (ResourceListTableModel) getModel();
        this.columnModel = getColumnModel();

        main.registerResourceListChangeListener(this.tableModel);

        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);
        setAutoCreateRowSorter(true);

        addKeyListener(this);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // the last row (the empty row for new addition) should not be deletable
        return columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 5 || columnIndex == 6 || columnIndex == 11;
    }

    public void init() {
        setTableColumnWidths();
        setRenderers();
        setEditors();
    }

    private void setDoubleClicksForEdit(ResourceListTableModel.COL columnIndex) {
        // the default is 2 and it does not help. Click count does not care about the time span between clicks.
        ((DefaultCellEditor)getDefaultEditor(getColumnClass(columnIndex.ordinal()))).setClickCountToStart(2);
    }

    public void setRenderers() {
        columnModel.getColumn(2).setCellRenderer(new FloatRenderer());
        columnModel.getColumn(7).setCellRenderer(new DurationRenderer());
        columnModel.getColumn(8).setCellRenderer(new LongRenderer());
        columnModel.getColumn(ResourceListTableModel.COL.Temp.ordinal()).setCellRenderer(new TempValueRenderer());

        ImageIcon deleteIcon = new ImageIcon(Utils.getResourcePath("/icons/cross.png"));
        columnModel.getColumn(11).setCellRenderer(new TableCellButton(deleteIcon, null));
        columnModel.getColumn(11).setCellEditor(new TableCellButton(deleteIcon, new ITableCellButtonClickListener() {
            @Override
            public void onTableCellButtonClick(int rowIndex) {
                main.getResourceList().removeResource(rowIndex);
            }
        }));
    }

    public void setEditors() {
        columnModel.getColumn(ResourceListTableModel.COL.Rating.ordinal()).setCellEditor(new MyFloatEditor());
        columnModel.getColumn(ResourceListTableModel.COL.Tag.ordinal()).setCellEditor(new MyTextEditor());
        columnModel.getColumn(ResourceListTableModel.COL.Path.ordinal()).setCellEditor(new MyTextEditor());
        columnModel.getColumn(ResourceListTableModel.COL.Name.ordinal()).setCellEditor(new MyTextEditor());
    }

    private void setTableColumnWidths() {
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            int width = tableModel.getColumnWidth(i);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }
    }

    public void updateResourceList(ResourceList resourceList) {
        tableModel.setResourceList(resourceList);
    }

    public int getCurrentViewIndex() {
        int selectedRows[] = getSelectedRows();
        return selectedRows.length > 0 ? selectedRows[0] : -1;
    }

    public int getCurrentModelIndex() {
        int viewIndex = getCurrentViewIndex();
        return viewIndex < 0 ? viewIndex : convertRowIndexToModel(viewIndex);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        main.processPlayerKeys(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}