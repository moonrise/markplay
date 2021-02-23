package com.mark.play;

import com.mark.play.player.MyPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

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
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        myPlayer = new MyPlayer(this, contentPane, mrl);

        frame.add(contentPane);
        frame.setVisible(true);

        myPlayer.getPlayerComponent().mediaPlayer().media().prepare(mrl);
        myPlayer.getPlayerComponent().mediaPlayer().media().parsing().parse();
        myPlayer.getPlayerComponent().mediaPlayer().media().play(mrl);

        // focus request should be done after the frame becomes visible
        myPlayer.getPlayerComponent().videoSurfaceComponent().requestFocus();
        //myPlayer.getPlayerComponent().videoSurfaceComponent().requestFocusInWindow();

        /*
        Logo logo = Logo.logo()
                .file(png)
                .position(LogoPosition.TOP_RIGHT)
                .opacity(0.1f)
                .duration(2000)
                .enable();
         */
        //mediaPlayerComponent.mediaPlayer().logo().set(logo);

        /*
        Marquee marquee = Marquee.marquee()
                .text(mrl)
                .size(40)
                .colour(Color.WHITE)
                .timeout(3000)
                .position(MarqueePosition.BOTTOM_RIGHT)
                .opacity(0.8f)
                .enable();
        mediaPlayerComponent.mediaPlayer().marquee().set(marquee);
        */
    }

    @Override
    public JFrame getAppFrame() {
        return this.frame;
    }

    @Override
    public void exitApplication() {
//        mediaPlayerComponent.release();
        System.exit(0);
    }
}