package com.mark.play;

import com.mark.io.ResourceList;
import com.mark.play.player.MyPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class Main implements IMain {
    private static final String AppName = "MarkPlay";
    private static Main thisApp = null;

    private final JFrame frame;
    private ResourceList resourceList = new ResourceList();

    private MyPlayer myPlayer;


    public static void main(String[] args) {
        String givenFile = null;
        if (args.length > 0 && args[0].length() > 0) {
            if (new File(args[0]).exists()) {
                givenFile = args[0];
                Log.log("Given file: %s", givenFile);
            }
            else {
                System.err.printf("Given file: '%s' does not exist.\n", args[0]);
            }
        }

        thisApp = new Main(givenFile);
    }

    public Main(String filePath) {
        if (filePath != null) {
            if (ResourceList.isFileExtensionMatch(filePath)) {
                this.resourceList = new ResourceList(filePath);
            }
            else {
                this.resourceList.addResource(new Resource(filePath));
            }
        }

        frame = new JFrame(AppName);
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
        String header = String.format("%s (%s%s)", AppName,
           resourceList == null ? Utils.NoName : resourceList.getName(),
           resourceList == null ? "" : resourceList.isDirty() ? " *" : "");
        frame.setTitle(header);
    }
}