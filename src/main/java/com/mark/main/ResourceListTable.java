package com.mark.main;

import com.mark.Log;
import com.mark.Utils;
import com.mark.resource.ResourceList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class ResourceListTable extends JTable {
    static class LongRenderer extends DefaultTableCellRenderer {
        public LongRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        protected void setValue(Object value) {
            super.setValue(String.format("%,d", (long)value));
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

    static class MyRenderer2 extends JLabel implements TableCellRenderer {
        public MyRenderer2() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setFont(new Font("helvetica", Font.PLAIN, 12));
            setForeground(Color.DARK_GRAY);
            setText(value.toString());
            //setToolTipText("tooltip here");
            return this;
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
    }

    public void init() {
        setTableColumnWidths();
        setRenderers();
    }

    public void setRenderers() {
        columnModel.getColumn(4).setCellRenderer(new DurationRenderer());
        columnModel.getColumn(5).setCellRenderer(new LongRenderer());
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
}