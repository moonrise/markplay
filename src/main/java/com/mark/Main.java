package com.mark;

import com.mark.io.LegacyFilerReader;
import com.mark.resource.*;
import com.mark.main.IMain;
import com.mark.main.MainFrame;
import com.mark.main.MainSplitPane;
import com.mark.play.player.MyPlayer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Main implements IMain, IResourceListChangeListener {
    @FunctionalInterface
    public interface Foo {
        ResourceList generateResourceList();
    }

    private static Main thisApp = null;

    private final MainFrame frame;
    private MainSplitPane splitPane;

    private ArrayList<IResourceListChangeListener> resourceListChangeListeners = new ArrayList<>();
    private ResourceList resourceList;

    private JTable table;
    private ResourceTableModel tableModel;


    private MyPlayer myPlayer;


    public static void main(String[] args) {
        thisApp = new Main();

        thisApp.processFile(null);

        if (args.length > 0) {
            thisApp.processCommandLineArguments(args);
        }
    }

    private void processCommandLineArguments(String[] args) {
        File givenFile = new File(args[0]);
        if (givenFile.exists()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    processFile(givenFile.getPath());
                }
            });
            Log.log("Given file: %s", givenFile);
        }
        else {
            displayErrorMessage(String.format("Given file: '%s' does not exist.", givenFile));
        }
    }

    public Main() {
        frame = new MainFrame(this);

        JPanel playerContainer = new JPanel();
        playerContainer.setLayout(new BorderLayout());

        resourceList = new ResourceList(this);
        tableModel = new ResourceTableModel(resourceList);
        table = new JTable(tableModel);
        registerResourceListChangeListener(tableModel);

        TableColumn column = null;
        for (int i = 0; i < 3; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 1) {
                column.setPreferredWidth(100); //third column is bigger
                column.setMinWidth(100); //third column is bigger
            } else {
                column.setPreferredWidth(50);
                column.setMinWidth(50); //third column is bigger
            }
        }

        splitPane = new MainSplitPane(new JScrollPane(table), playerContainer);
        table.setFillsViewportHeight(true);

        myPlayer = new MyPlayer(this, playerContainer, resourceList);

        frame.add(splitPane);
        frame.display();

        this.updateAppHeader();

        //myPlayer.setLogo(Utils.getResourcePath("/icons/crown.png"));
        myPlayer.setMute();
        myPlayer.play();

        // focus request should be done after the frame becomes visible
        myPlayer.setFocus();

        // app event listeners
        registerResourceListChangeListener(this);
    }

    @Override
    public JFrame getAppFrame() {
        return this.frame;
    }

    @Override
    public void displayErrorMessage(String message) {
        Log.err(message);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void exitApplication() {
        myPlayer.release();
        System.exit(0);
    }

    public void updateAppHeader() {
        String header = String.format("%s - %s%s", Utils.AppName,
           resourceList == null ? Utils.NoName : resourceList.getName(),
           resourceList == null ? "" : resourceList.isDirty() ? " *" : "");
        frame.setTitle(header);
    }

    @Override
    public void registerResourceListChangeListener(IResourceListChangeListener listener) {
        this.resourceListChangeListeners.add(listener);
    }

    @Override
    public void onResourceListChange(ResourceList resourceList, EResourceListChangeType type) {
        updateAppHeader();
    }

    @Override
    public boolean flipShowNavigator() {
        return splitPane.flipVisibilityLeftPanel();
    }

    public void notifyResourceListChange(ResourceList resourceList, EResourceListChangeType changeType) {
        for (IResourceListChangeListener listener : this.resourceListChangeListeners) {
            listener.onResourceListChange(resourceList, changeType);
        }
    }

    public boolean processCurrentContent() {
        if (!resourceList.isDirty()) {
            return true;
        }

        return JOptionPane.YES_OPTION ==
               JOptionPane.showConfirmDialog(getAppFrame(), "Current content modified. Do you want to lose the change and continue?", "Confirm", JOptionPane.YES_NO_OPTION);
    }

    public void processFile(String filePath) {
        if (filePath == null) {                                         // new file request
            loadResourceList(() -> new ResourceList(this));
        }
        else if (ResourceList.isFileExtensionMatch(filePath)) {
            loadResourceList(() -> new ResourceList(this, filePath));
        }
        else if (LegacyFilerReader.isFileExtensionMatch(filePath)) {    // old xml files
            loadResourceList(() -> new LegacyFilerReader().read(this, new File(filePath)));
        }
        else {                                                          // assume media files for all else
            resourceList.addResource(new Resource(filePath));
        }
    }

    private void loadResourceList(Foo resourceListGenerator) {
        if (processCurrentContent()) {
            resourceList = resourceListGenerator.generateResourceList();
            notifyResourceListChange(resourceList, EResourceListChangeType.ResourceListLoaded);
            tableModel.setResourceList(resourceList);
        }
    }
}