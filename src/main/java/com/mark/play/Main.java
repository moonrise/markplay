package com.mark.play;

import com.mark.io.IAppDataChangeListener;
import com.mark.io.ResourceList;
import com.mark.play.actions.AppMenuBar;
import com.mark.play.player.MyPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

public class Main implements IMain {
    private static Main thisApp = null;

    private final JFrame frame;
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
        frame = new JFrame(Utils.AppName);
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        myPlayer = new MyPlayer(this, contentPane, resourceList);

        frame.setJMenuBar(new AppMenuBar(this));
        frame.add(contentPane);
        frame.pack();
        frame.setVisible(true);

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
        String header = String.format("%s (%s%s)", Utils.AppName,
           resourceList == null ? Utils.NoName : resourceList.getName(),
           resourceList == null ? "" : resourceList.isDirty() ? " *" : "");
        frame.setTitle(header);
    }

    @Override
    public void registerAppDataChangeListener(IAppDataChangeListener listener) {
        this.appDataChangeListeners.add(listener);
    }

//    private void notifyAppDataChangeListeners(EResourceChangeType changeType) {
//        for (IAppDataChangeListener listener : this.appDataChangeListeners) {
//            listener.onResourceListLoaded(this, changeType);
//        }
//    }

    private void processFile(String filePath) {
        if (ResourceList.isFileExtensionMatch(filePath)) {
            this.resourceList = new ResourceList(filePath);
        }
        else {
            this.resourceList.addResource(new Resource(filePath));
        }
    }
}