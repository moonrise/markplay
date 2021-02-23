package com.mark.play.player;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import javax.swing.*;

public class MyMediaPlayerEventListener extends MediaPlayerEventAdapter {
    private JFrame frame;
    private String mrl;


    public MyMediaPlayerEventListener(JFrame frame, String mrl) {
        this.mrl = mrl;
        this.frame = frame;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        System.out.println("Playing...");
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        System.out.println("Finished.");
        mediaPlayer.submit(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.media().play(mrl);
            }
        });
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(frame, "Failed to play media", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
