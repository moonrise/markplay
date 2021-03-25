package com.mark.resource;

import com.mark.io.ResourceList;

import javax.swing.table.AbstractTableModel;

public class ResourceTableModel extends AbstractTableModel {
    private ResourceList resourceList;
    private String[] columnNames = {"Select", "Path", "Rating"};

    public ResourceTableModel(ResourceList resourceList) {
        this.resourceList = resourceList;
    }

    public ResourceList getResourceList() {
        return resourceList;
    }

    public void setResourceList(ResourceList resourceList) {
        this.resourceList = resourceList;
    }

    @Override
    public int getRowCount() {
        return resourceList.getResources().size();
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
        Resource resource = resourceList.getResources().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return resource.checked;
            case 1:
                return resource.path;
            case 2:
                return resource.rating;
        }
        return null;
    }
}
