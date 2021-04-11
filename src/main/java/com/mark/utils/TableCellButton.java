package com.mark.utils;

import com.mark.Log;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableCellButton extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private JButton button;
    private int rowIndex;

    public TableCellButton() {
        button = new JButton("Y");
        button.addActionListener(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        rowIndex = (int)value;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "";      // not important for a button editor really
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Log.log("cell button pressed for delete: %d", rowIndex);
        fireEditingStopped();
    }
}
