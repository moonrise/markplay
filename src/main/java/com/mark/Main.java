package com.mark;

import com.mark.io.IAppDataChangeListener;
import com.mark.io.ResourceList;
import com.mark.main.IMain;
import com.mark.main.MainFrame;
import com.mark.main.MainSplitPane;
import com.mark.resource.Resource;
import com.mark.play.player.MyPlayer;
import com.mark.resource.ResourceTableModel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Main implements IMain {
    private static Main thisApp = null;

    private final MainFrame frame;
    private MainSplitPane splitPane;
    private JTable table;
    private ResourceTableModel tableModel = new ResourceTableModel(new ResourceList());

    private ArrayList<IAppDataChangeListener> appDataChangeListeners = new ArrayList<>();

    private ResourceList resourceList = new ResourceList();

    private MyPlayer myPlayer;


    public static void main(String[] args) {
        thisApp = new Main();

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

        table = new JTable(tableModel);

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
        String header = String.format("%s -%s%s", Utils.AppName,
           resourceList == null ? Utils.NoName : resourceList.getName(),
           resourceList == null ? "" : resourceList.isDirty() ? " *" : "");
        frame.setTitle(header);
    }

    @Override
    public void registerAppDataChangeListener(IAppDataChangeListener listener) {
        this.appDataChangeListeners.add(listener);
    }

    @Override
    public boolean flipShowNavigator() {
        return splitPane.flipVisibilityLeftPanel();
    }

    //    private void notifyAppDataChangeListeners(EResourceChangeType changeType) {
//        for (IAppDataChangeListener listener : this.appDataChangeListeners) {
//            listener.onResourceListLoaded(this, changeType);
//        }
//    }

    private void processFile(String filePath) {
        if (ResourceList.isFileExtensionMatch(filePath)) {
            resourceList = new ResourceList(filePath);
        }
        else {
            resourceList.addResource(new Resource(filePath));
        }

        tableModel.setResourceList(resourceList);
        tableModel.fireTableStructureChanged();
    }
}