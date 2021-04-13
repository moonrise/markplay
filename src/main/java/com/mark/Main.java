package com.mark;

import com.mark.io.LegacyFilerReader;
import com.mark.main.ResourceListTable;
import com.mark.resource.*;
import com.mark.main.IMain;
import com.mark.main.MainFrame;
import com.mark.main.MainSplitPane;
import com.mark.play.player.MyPlayer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Main implements IMain, IResourceListChangeListener, ListSelectionListener {
    @FunctionalInterface
    public interface Foo {
        ResourceList generateResourceList();
    }

    private static Main thisApp = null;

    private final MainFrame frame;
    private MainSplitPane splitPane;

    private ArrayList<IResourceListChangeListener> resourceListChangeListeners = new ArrayList<>();
    private ResourceList resourceList;

    private ResourceListTable table;

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
            //Log.log("Given file: %s", givenFile);
        } else {
            displayErrorMessage(String.format("Given file: '%s' does not exist.", givenFile));
        }
    }

    public Main() {
        frame = new MainFrame(this);

        JPanel playerContainer = new JPanel();
        playerContainer.setLayout(new BorderLayout());

        resourceList = new ResourceList(this);

        table = new ResourceListTable(this, resourceList);
        splitPane = new MainSplitPane(new JScrollPane(table), playerContainer);

        myPlayer = new MyPlayer(this, playerContainer);
        myPlayer.registerPlayerStateChangeListener(getAppFrame().getStatusBar());

        frame.add(splitPane);
        frame.display();

        this.updateAppHeader();

        // focus request should be done after the frame becomes visible
        myPlayer.setFocus();

        // app event listeners
        registerResourceListChangeListener(this);
    }

    public ResourceList getResourceList() {
        return resourceList;
    }

    @Override
    public MainFrame getAppFrame() {
        return this.frame;
    }

    @Override
    public void displayInfoMessage(String message) {
        Log.log(message);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
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
        if (processCurrentContent()) {
            //myPlayer.release() causes JVM error on exit
            //myPlayer.release();
            System.exit(0);
        }
    }

    public void updateAppHeader() {
        String resourceListInfo = String.format("%s%s",
                resourceList == null ? Utils.NoName : resourceList.getName(),
                resourceList == null ? "" : resourceList.isDirty() ? " *" : "");

        String currentIndex = "";
        String currentResource = "";
        if (resourceList != null && resourceList.size() >= 0) {
            currentIndex = String.format(" (%d/%d)", resourceList.getCurrentIndex() + 1, resourceList.size());

            Resource resource = resourceList.getCurrent();
            if (resource != null) {
                currentResource = String.format("  [%s%s; Markers:%d]", resource.checked ? "*** " : "", resource.getName(), resource.markers.size()-1);
            }
        }

        frame.setTitle(String.format("%s - {%s} %s%s%s", Utils.AppName, resourceList.getRoot(), resourceListInfo, currentIndex, currentResource));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            // the selection events tend to be not precise, not accurate and redundant; so takes this as a clue only.
            resourceList.setCurrentIndex(table.getCurrentModelIndex());
        }
    }

    @Override
    public void registerResourceListChangeListener(IResourceListChangeListener listener) {
        this.resourceListChangeListeners.add(listener);
    }

    public void notifyResourceListChange(ResourceList resourceList, ResourceListUpdate update) {
        for (IResourceListChangeListener listener : this.resourceListChangeListeners) {
            listener.onResourceListChange(resourceList, update);
        }
    }

    @Override
    public void onResourceListChange(ResourceList resourceList, ResourceListUpdate update) {
        if (update.type == EResourceListChangeType.Loaded) {
            // TODO: table init everytime new resource list [AbstractTableModel:fireTableStructureChanged()?]
            table.init();

            table.getSelectionModel().addListSelectionListener(this);
            myPlayer.startResource(resourceList.getCurrent());
            updateTableRowSelection(resourceList.getCurrentIndex());
        } else if (update.type == EResourceListChangeType.Unloaded) {
            table.getSelectionModel().removeListSelectionListener(this);
            myPlayer.startResource(null);
        } else if (update.type == EResourceListChangeType.IndexChanged) {
            //myPlayer.setLogo(Utils.getResourcePath("/icons/crown.png"));
            myPlayer.startResource(resourceList.getCurrent());
            updateTableRowSelection(resourceList.getCurrentIndex());
        }

        updateAppHeader();
    }

    private void updateTableRowSelection(int newIndex) {
        int modelIndex = table.getCurrentModelIndex();
        if (newIndex != modelIndex) {
            table.setRowSelectionInterval(newIndex, newIndex);
        }
    }

    @Override
    public boolean flipShowNavigator() {
        return splitPane.flipVisibilityLeftPanel();
    }

    public boolean processCurrentContent() {
        String promptMessage = "Current content modified. Do you want to lose the change and continue?";
        if (Prefs.isModifiedConfirmOnClose() && resourceList.isDirty() &&
                JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(getAppFrame(), promptMessage, "Confirm", JOptionPane.YES_NO_OPTION)) {
            return false;
        }

        notifyResourceListChange(resourceList, ResourceListUpdate.Unloaded);
        return true;
    }

    public void processFile(String filePath) {
        if (filePath == null) {                                         // new file request
            loadResourceList(() -> new ResourceList(this));
        } else if (ResourceList.isFileExtensionMatch(filePath)) {
            loadResourceList(() -> new ResourceList(this, filePath));
            Prefs.setRecentFile(filePath);
        } else if (LegacyFilerReader.isFileExtensionMatch(filePath)) {    // old xml files
            loadResourceList(() -> new LegacyFilerReader().read(this, new File(filePath)));
            Prefs.setRecentFile(filePath);
        } else {                                                          // assume media files for all else
            addResourceFile(filePath);
        }
    }

    private void addResourceFile(String filePath) {
        if (!resourceList.validateRoot(filePath)) {
            displayErrorMessage(String.format("Cannot add '%s' because it is not compatible with the root context '%s'.", filePath, resourceList.getRoot()));
            return;
        }

        resourceList.addResource(new Resource(filePath, resourceList));
        selectRowTable(resourceList.size() - 1);
    }

    @Override
    public void saveCurrentResourceList(boolean saveAs) {
        if (resourceList == null) {
            return;     // sanity check
        }

        String filePath = resourceList.getFilePath();
        if (saveAs || filePath == null) {
            // TODO: In MacOS, replace prompt from FileDialog generates a warning "<NSSavePanel: 0x7fc036cff7d0> found it necessary to prepare implicitly; please prepare panels using NSSavePanel rather than NSApplication or NSWindow."
            FileDialog dialog = new FileDialog(getAppFrame(), "Provide new file path", FileDialog.SAVE);
            dialog.setFile("new" + ResourceList.FileExtension);
            dialog.setVisible(true);

            filePath = dialog.getFile() != null ? dialog.getDirectory() + dialog.getFile() : null;
            Prefs.setRecentFile(filePath);
            //Log.log("Save to file: %s in directory: %s", dialog.getFile(), dialog.getDirectory());
        }

        if (filePath == null) {
            return;     // SaveAs canceled by user
        }

        resourceList.saveAs(filePath);
    }

    @Override
    public void navigateResourceList(boolean forward, boolean favorite) {
        resourceList.navigate(forward, favorite);
    }

    private void loadResourceList(Foo resourceListGenerator) {
        if (processCurrentContent()) {
            resourceList = resourceListGenerator.generateResourceList();
            table.updateResourceList(resourceList);
            notifyResourceListChange(resourceList, ResourceListUpdate.Loaded);
        }
    }

    private void selectRowTable(int modelIndex) {
        if (resourceList.size() > modelIndex) {
            resourceList.setCurrentIndex(modelIndex);
        }
    }
}