package com.mark.main;

import com.mark.Log;
import com.mark.Utils;
import com.mark.resource.*;

import javax.swing.table.AbstractTableModel;

public class ResourceListTableModel extends AbstractTableModel implements IResourceListChangeListener {
    public static enum COL { Row, Select, Rating, Tag, Markers, Path, Name, Duration, FileSize, FileHash, Temp, Delete  };

    private ResourceList resourceList;
    private String[] columnNames = {"Row", "Select", "Rating", "Tag", "Makers", "Path", "Name", "Duration", "File Size", "File Hash", "Temp", "Delete"};
    private int[] columnWidths = {20, 16, 24, 32, 20, 70, 140, 40, 80, 100, 16, 16};

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
            case 4:
            case 10:
                return Integer.class;
            case 2:
                return Float.class;
            case 1:
                return Boolean.class;
            case 7:
            case 8:
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
                return resource.rating;
            case 3:
                return resource.tag;
            case 4:
                return resource.markers.size()-1;
            case 5:
                return resource.getMidPath();
            case 6:
                return resource.getName();
            case 7:
                return resource.duration;
            case 8:
                return resource.fileSize;
            case 9:
                return resource.fileHash;
            case 10:
                return resource.temp;
            case 11:
                return rowIndex;
        }

        return "<na>";
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            resourceList.getResources().get(rowIndex).setFavorite((boolean)value);
        }
        else if (columnIndex == 2) {
            resourceList.getResources().get(rowIndex).setRating((float)value);
        }
        else if (columnIndex == 3) {
            resourceList.getResources().get(rowIndex).setTag(((String)value).trim());
        }
        else if (columnIndex == 5) {
            String oldMidPath = resourceList.getResources().get(rowIndex).getMidPath();

            // quick validation check
            String newMidPath = ((String)value).trim();
            if (newMidPath.startsWith("/") || newMidPath.startsWith("\\")) {
                newMidPath = newMidPath.substring(1);
            }
            if (!newMidPath.endsWith("/") && !newMidPath.endsWith("\\")) {
                newMidPath += "/";
            }

            if (!Utils.normPathIsEqual(newMidPath, oldMidPath)) {
                resourceList.updateMidPaths(oldMidPath, newMidPath);
            }
        }
        else if (columnIndex == 6) {
            resourceList.getResources().get(rowIndex).setName(((String)value).trim());
        }
        super.setValueAt(value, rowIndex, columnIndex);
    }

    @Override
    public void onResourceListChange(ResourceList resourceList, ResourceListUpdate update) {
        if (update.type == EResourceListChangeType.RowsAdded) {
            fireTableRowsInserted(update.startRow, update.endRow);
        }
        else if (update.type == EResourceListChangeType.RowsRemoved) {
            //Log.log("Table model rows removed: %d->%d", update.startRow, update.endRow);
            fireTableRowsDeleted(update.startRow, update.endRow);
        }
        else if (update.type == EResourceListChangeType.ChildResourceChanged) {
            fireTableRowsUpdated(update.startRow, update.endRow);
        }
        else if (update.type == EResourceListChangeType.AllRowsUpdated) {
            fireTableRowsUpdated(0, resourceList.size()-1);
        }
    }
}
