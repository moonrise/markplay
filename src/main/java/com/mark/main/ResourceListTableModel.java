package com.mark.main;

import com.mark.Utils;
import com.mark.resource.*;

import javax.swing.table.AbstractTableModel;

public class ResourceListTableModel extends AbstractTableModel implements IResourceListChangeListener {
    private ResourceList resourceList;
    private String[] columnNames = {"Row", "Favorite", "Makers", "Path", "Duration", "File Size", "Duplicate"};
    private int[] columnWidths = {10, 10, 10, 250, 20, 65, 10};

    public ResourceListTableModel(ResourceList resourceList) {
        this.resourceList = resourceList;

        fireTableStructureChanged();
    }

    public ResourceList getResourceList() {
        return resourceList;
    }

    public void setResourceList(ResourceList resourceList) {
        this.resourceList = resourceList;
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return resourceList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getColumnWidth(int column) {
        return columnWidths[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 2:
            case 6:
                return Integer.class;
            case 1:
                return Boolean.class;
            case 4:
            case 5:
                return Long.class;
        }

        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Resource resource = resourceList.getResources().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return resource.checked;
            case 2:
                return resource.markers.size()-1;
            case 3:
                return resource.getPathWithNoRoot();
            case 4:
                return resource.duration;
            case 5:
                return resource.fileSize;
            case 6:
                return resource.temp;
        }

        return "<na>";
    }

    @Override
    public void onResourceListChange(ResourceList resourceList, ResourceListUpdate update) {
        if (update.type == EResourceListChangeType.RowsAdded) {
            fireTableRowsInserted(update.startRow, update.endRow);
        }
        else if (update.type == EResourceListChangeType.ChildResourceChanged) {
            fireTableRowsUpdated(update.startRow, update.endRow);
        }
        else if (update.type == EResourceListChangeType.AllRowsUpdated) {
            fireTableRowsUpdated(0, resourceList.size()-1);
        }
    }
}
