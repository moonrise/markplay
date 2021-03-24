package com.mark.main;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import com.mark.play.actions.AppMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements ComponentListener {
    public MainFrame(IMain main) throws HeadlessException {
        super(Utils.AppName);

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
        /*
        [TODO] See the TODO comment in componentMoved method in this class.
        if (getScreenCount() > 1) {
            dumpGraphicsDevices();
            String savedDeviceId = Prefs.getDeviceId();
            String currentDeviceId = getGraphicsDevice().getIDstring();
            if (!savedDeviceId.equals(currentDeviceId)) {
                // move it to the previously known device if possible
                GraphicsDevice prevGd = findGraphicsDevice(savedDeviceId);
                Log.log("move the window to device %s from device %s", savedDeviceId, currentDeviceId);
            }
        }
        */

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

    private void dumpGraphicsDevices() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        for (GraphicsDevice gd : screens) {
            Log.log("Device/Monitor ID: %s, Bounds: %s", gd.getIDstring(), gd.getDefaultConfiguration().getBounds());
        }
    }

    private int getScreenCount() {      // i.e. monitor count
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }

    private GraphicsDevice findGraphicsDevice(String id) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        for (GraphicsDevice gd : screens) {
            if (gd.getIDstring().equals(id)) {
                return gd;
            }
        }

        return null;
    }

    private GraphicsDevice getGraphicsDevice() {
        return getGraphicsConfiguration().getDevice();
    }

    private Rectangle getDeviceBounds() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        //Log.log("Device/Monitor ID: %s, %s", gc.getDevice().getIDstring(), gc.getBounds().toString());
        return gc.getBounds();
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

        // [TODO] Strange thing is that the device location is restored in multi monitor environments, all of sudden.
        // The work was being done to get it to work, but not sure why it started showing the main frame in the last
        // monitor location. STRANGE.... will revisit if it stops working.
        // save deviceID for the environment where multiple monitors are available
        // Log.log("Device/Monitor ID: %s, Bounds: %s", getGraphicsDevice().getIDstring(), getDeviceBounds());
        // Prefs.setDeviceId(getGraphicsDevice().getIDstring());
    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {
    }
}