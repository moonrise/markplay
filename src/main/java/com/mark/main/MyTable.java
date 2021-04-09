package com.mark.main;

import com.mark.resource.ResourceList;
import com.mark.resource.ResourceTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class MyTable extends JTable {
    static class MyRenderer extends JLabel implements TableCellRenderer {
        public MyRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setToolTipText("tooltip here");
            return this;
        }
    }

    private IMain main;
    private ResourceTableModel tableModel;
    private TableColumnModel columnModel;

    public MyTable(IMain main, ResourceList resourceList) {
        super(new ResourceTableModel(resourceList));

        this.main = main;
        this.tableModel = (ResourceTableModel) getModel();
        this.columnModel = getColumnModel();

        main.registerResourceListChangeListener(this.tableModel);

        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);
    }

    public void init() {
        setRenderers();
    }

    public void setRenderers() {
        MyTable.MyRenderer mr = new MyTable.MyRenderer();
        columnModel.getColumn(4).setCellRenderer(mr);
        columnModel.getColumn(5).setCellRenderer(mr);
    }

    public void setTableColumnWidths() {
        init();

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
}