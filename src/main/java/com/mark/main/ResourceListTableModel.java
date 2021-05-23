package com.mark.main;

import com.mark.Log;
import com.mark.Utils;
import com.mark.resource.*;

import javax.swing.table.AbstractTableModel;

public class ResourceListTableModel extends AbstractTableModel implements IResourceListChangeListener {
    // table column ordinals via enum
    public static enum COL { Row, Select, Rating, Tag, Markers, Path, Name, Duration, FileSize, FileHash, Temp, Delete  };

    // TEMP column negative number usages. 0 is rendered as empty, 1 and above as they are.
    public static final int TEMP_ERROR = -1;
    public static final int TEMP_NO_FILE= -2;               // file does not exist
    public static final int TEMP_HASHED= -3;                // file hashed
    public static final int TEMP_NOT_HASHED= -4;            // file not hashed
    public static final int TEMP_IN_HASHSTORE= -5;          // in hash store
    public static final int TEMP_NOT_IN_HASHSTORE = -6;     // no matching hash found in the hash store
    public static final int TEMP_RESTORE_MERGED = -7;       // merged from the hash store
    public static final int TEMP_MERGED = -8;               // merged from user file operations
    public static final int TEMP_ADDED = -9;                // new addition from the user file operations


    private ResourceList resourceList;
    private String[] columnNames = {"Row", "Select", "Rating", "Tag", "Makers", "Path", "Name", "Duration", "File Size", "File Hash", "Temp", "Delete"};
    private int[] columnWidths = {20, 16, 24, 32, 20, 70, 140, 40, 80, 100, 40, 16};

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
        if (columnIndex == COL.Row.ordinal() || columnIndex == COL.Markers.ordinal()) {
            return Integer.class;
        }
        else if (columnIndex == COL.Select.ordinal()) {
            return Boolean.class;
        }
        else if (columnIndex == COL.Rating.ordinal()) {
            return Float.class;
        }
        else if (columnIndex == COL.Duration.ordinal() || columnIndex == COL.FileSize.ordinal()) {
            return Long.class;
        }

        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Resource resource = resourceList.getResources().get(rowIndex);

        if (columnIndex == COL.Row.ordinal()) {
            return rowIndex + 1;
        }
        else if (columnIndex == COL.Select.ordinal()) {
            return resource.checked;
        }
        else if (columnIndex == COL.Rating.ordinal()) {
            return resource.rating;
        }
        else if (columnIndex == COL.Tag.ordinal()) {
            return resource.tag;
        }
        else if (columnIndex == COL.Markers.ordinal()) {
            return resource.markers.size()-1;
        }
        else if (columnIndex == COL.Path.ordinal()) {
            return resource.getMidPath();
        }
        else if (columnIndex == COL.Name.ordinal()) {
            return resource.getName();
        }
        else if (columnIndex == COL.Duration.ordinal()) {
            return resource.duration;
        }
        else if (columnIndex == COL.FileSize.ordinal()) {
            return resource.fileSize;
        }
        else if (columnIndex == COL.FileHash.ordinal()) {
            return resource.fileHash;
        }
        else if (columnIndex == COL.Temp.ordinal()) {
            return resource.temp;
        }
        else if (columnIndex == COL.Delete.ordinal()) {
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
            resourceList.getResources().get(rowIndex).setRating(Float.parseFloat(((String)value).trim()));
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
