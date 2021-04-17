package com.mark.utils;

import com.mark.Log;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * the value for the cell is expected to be the row index
 */
public class TableCellButton extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
    private JButton button;
    private ImageIcon rowIcon;
    private ImageIcon lastRowIcon;
    private int rowIndex;
    private boolean isTransparentButton = false;
    private ITableCellButtonClickListener buttonClickListener;

    public TableCellButton(String text, ITableCellButtonClickListener listener) {
        this(text, null, listener);
    }

    public TableCellButton(ImageIcon icon, ITableCellButtonClickListener listener) {
        this(null, icon, listener);
    }

    public TableCellButton(String text, ImageIcon icon, ITableCellButtonClickListener listener) {
        this.rowIcon = icon;
        this.button = new JButton(text, icon);
        this.buttonClickListener = listener;

        button.addActionListener(this);
    }

    public void setLastRowIcon(ImageIcon lastRowIcon) {
        this.lastRowIcon = lastRowIcon;
    }

    public void setTransparentButton(boolean transparentButton) {
        isTransparentButton = transparentButton;
        if (isTransparentButton) {
            makeTransparentButton();
        }
    }

    private void makeTransparentButton() {
        // coordinate with selection state (see getTableCellRendererComponent)
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            if (isTransparentButton) {
                button.setBackground(table.getBackground());
            }
            else {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        }

        if (lastRowIcon != null) {
            button.setIcon(row == table.getModel().getRowCount() - 1 ? lastRowIcon : rowIcon);
        }
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        rowIndex = (int) value;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "";      // not important for a button editor really
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Log.log("cell button pressed for delete: %d", rowIndex);
        if (buttonClickListener != null) {
            buttonClickListener.onTableCellButtonClick(rowIndex);
        }
        fireEditingStopped();
    }
}
