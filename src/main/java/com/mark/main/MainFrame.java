package com.mark.main;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import com.mark.play.actions.AppMenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements ComponentListener, DropTargetListener, KeyListener {
    private final IMain main;
    private final ToolBar toolBar;
    private final StatusBar statusBar;

    public MainFrame(IMain main) throws HeadlessException {
        super(Utils.AppName);
        this.main = main;
        this.toolBar = new ToolBar(main);
        this.statusBar = new StatusBar();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setJMenuBar(new AppMenuBar(main));

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                main.exitApplication();
            }
        });

        addComponentListener(this);
        addKeyListener(this);

        new DropTarget(this, DnDConstants.ACTION_NONE, this);
    }

    public Point getOffsetLocation(int offsetX, int offsetY) {
        Point offsetLocation = getLocation();
        offsetLocation.translate(150, 50);
        return offsetLocation;
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
        Rectangle pref = Prefs.getMainFrameGeometry();
        Rectangle visible = computeVisibleBounds(pref);
        //Log.log("preference: %d, %d, %d, %d", pref.x, pref.y, pref.width, pref.height);
        //Log.log("visible: %d, %d, %d, %d", visible.x, visible.y, visible.width, visible.height);
        setBounds(visible);
    }

    private Rectangle computeVisibleBounds(Rectangle preferred) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Rectangle main = new Rectangle(0, 0, 600, 400);
        for (GraphicsDevice gd : screens) {
            Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
            //Log.log("Device/Monitor ID: %s, Bounds: %s", gd.getIDstring(), screenBounds);
            if (screenBounds.contains(preferred)) {
                return preferred;
            }
            if (screenBounds.x == 0 && screenBounds.y == 0) {
                main = screenBounds;
            }
        }

        return new Rectangle(0, 0, Math.min(main.width, preferred.width), Math.min(main.height, preferred.height));
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

    @Override
    public void dragEnter(DropTargetDragEvent event) {
        //Log.log("drag enter");
        event.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
        //Log.log("drag over");
        event.acceptDrag(DnDConstants.ACTION_COPY);
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
        //Log.log("drop action changed");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        //Log.log("drag exit");
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        //Log.log("dropped: " + event.getSource());

        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable transferable = event.getTransferable();
        DataFlavor[] flavors = event.getTransferable().getTransferDataFlavors();

        for (DataFlavor flavor : flavors) {
            try {
                if (flavor.isFlavorJavaFileListType()) {
                    List<File> files = (List) transferable.getTransferData(flavor);
                    ArrayList<String> paths = new ArrayList<>();
                    for (File file : files) {
                        //Log.log("Drop file: %s", file.getPath());
                        paths.add(file.getPath());
                    }

                    main.processPaths(paths.toArray(String[]::new), event.getDropAction() == DnDConstants.ACTION_COPY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                main.displayErrorMessage(e.toString());
            }
        }

        // Inform that the drop is complete
        event.dropComplete(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO: not sure if we ever get here...
        main.processPlayerKeys(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }
}