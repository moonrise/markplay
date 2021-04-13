package com.mark.main;

import com.mark.Utils;
import com.mark.resource.ResourceList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
        columnModel.getColumn(6).setCellRenderer(new ZeroSpaceRenderer());
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