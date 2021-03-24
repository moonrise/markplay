package com.mark;

import com.mark.play.IMain;
import com.mark.play.Log;
import com.mark.play.Utils;
import com.mark.play.actions.AppMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements ComponentListener {
    private IMain main;

    public MainFrame(IMain main) throws HeadlessException {
        super(Utils.AppName);
        this.main = main;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setJMenuBar(new AppMenuBar(main));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                main.exitApplication();
            }
        });

        addComponentListener(this);
    }

    public void display() {
        setBounds();
        // pack();      // DO NOT pack since we're setting the bounds above
        setVisible(true);
    }

    public void setBounds() {
        // set the location and size from the preference store
        Rectangle geo = Prefs.getMainFrameGeometry();
        //Log.log("geo: %d, %d, %d, %d", geo.x, geo.y, geo.width, geo.height);
        setBounds(geo.x, geo.y, geo.width, geo.height);
    }

    private void saveCurrentGeometry() {
        //Log.log("Main frame geometry changed, so saved to pref: at %d,%d; %d x %d", getX(), getY(), getWidth(), getHeight());
        Prefs.setMainFrameGeometry(new Rectangle(getX(), getY(), getWidth(), getHeight()));
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        saveCurrentGeometry();
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {
        saveCurrentGeometry();
    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {
    }
}
