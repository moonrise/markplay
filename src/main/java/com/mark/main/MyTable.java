package com.mark.main;

import com.mark.resource.ResourceList;
import com.mark.resource.ResourceTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class MyTable extends JTable {
    private IMain main;

    public MyTable(IMain main, ResourceList resourceList) {
        super(new ResourceTableModel(resourceList));

        this.main = main;
        main.registerResourceListChangeListener(getMyModel());

        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setRowSelectionAllowed(true);
    }

    private ResourceTableModel getMyModel() {
        return (ResourceTableModel)getModel();
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        return super.getCellRenderer(row, column);
    }

    public void setTableColumnWidths() {
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        for (int i = 0; i < getModel().getColumnCount(); i++) {
            TableColumn column = getColumnModel().getColumn(i);
            int width = getMyModel().getColumnWidth(i);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
        }
    }

    public void updateResourceList(ResourceList resourceList) {
        getMyModel().setResourceList(resourceList);
    }
}
