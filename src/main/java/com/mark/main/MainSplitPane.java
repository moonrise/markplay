package com.mark.main;

import com.mark.Prefs;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainSplitPane extends JSplitPane implements PropertyChangeListener {
    private JScrollPane leftPanel;
    private int dividerSize;

    public MainSplitPane(JScrollPane leftPanel, JPanel rightPanel) {
        super(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

        this.leftPanel = leftPanel;
        this.dividerSize = getDividerSize();
        setOneTouchExpandable(true);

        showLeftPanel(Prefs.isNavigatorVisible());
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        //Log.log("MainSplitPane property changed; new divider location: %d", pce.getNewValue());
        Prefs.setDividerX((int) (pce.getNewValue()));
    }

    public boolean isLeftPanelVisible() {
        return getDividerSize() > 0;
    }

    public boolean flipVisibilityLeftPanel() {
        return showLeftPanel(!isLeftPanelVisible());
    }

    private boolean showLeftPanel(boolean show) {
        if (show) {
            setLeftComponent(leftPanel);
            setDividerSize(dividerSize);
            setDividerLocation(Prefs.getDividerX());
            addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
        } else {
            removePropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
            setLeftComponent(null);
            setDividerSize(0);
        }

        Prefs.setNavigatorVisible(show);
        return show;
    }
}
