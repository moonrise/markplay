package com.mark.play;

import com.mark.play.player.MyPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

public class Main implements IMain {
    private static Main thisApp = null;

    private final JFrame frame;

    private MyPlayer myPlayer;


    public static void main(String[] args) {
        if (args.length > 0 && args[0].length() > 0) {
            String givenFile = args[0];
            if (new File(givenFile).exists()) {
                System.out.printf("Given file: %s\n", givenFile);
                thisApp = new Main(givenFile);
            }
            else {
                System.err.printf("Given file: '%s' does not exist.\n", givenFile);
            }
        }
        else {
            System.out.println("Provide a video file to play as the first command line argument.");
        }
    }

    public Main(String mrl) {
        frame = new JFrame("Mark and Play");
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

        myPlayer = new MyPlayer(this, contentPane, new Resource(mrl));

        frame.add(contentPane);
        frame.pack();
        frame.setVisible(true);

        //myPlayer.setLogo(Utils.getResourcePath("/icons/crown.png"));
        myPlayer.setMute();
        myPlayer.play(mrl);

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
}