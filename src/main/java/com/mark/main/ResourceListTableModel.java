package com.mark.main;

import com.mark.Utils;
import com.mark.resource.*;

import javax.swing.table.AbstractTableModel;

public class ResourceListTableModel extends AbstractTableModel implements IResourceListChangeListener {
    private ResourceList resourceList;
    private String[] columnNames = {"Row", "Favorite", "Makers", "Path", "Duration", "File Size"};
    private int[] columnWidths = {10, 10, 10, 300, 20, 65};

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
                return Integer.class;
            case 1:
                return Boolean.class;
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
                return resource.path;
            case 4:
                return Utils.getTimelineFormatted(resource.duration, false);
            case 5:
                return String.format("%,d", resource.fileSize);
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
    }
}
