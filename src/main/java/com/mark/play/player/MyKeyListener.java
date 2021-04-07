package com.mark.play.player;

import com.mark.Prefs;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter  {
    private IMyPlayer myPlayer;
    private EmbeddedMediaPlayer mediaPlayer;

    public MyKeyListener(IMyPlayer myPlayer, EmbeddedMediaPlayer mediaPlayer) {
        this.myPlayer = myPlayer;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.printf("key code: %d, key char: %c, shift: %s (%s)\n", e.getKeyCode(), e.getKeyChar(), e.isShiftDown(), KeyEvent.getKeyText(e.getKeyCode()));

        switch (e.getKeyChar()) {
            case 'e':
                myPlayer.seekMarker(true);
                break;
            case 'w':
                myPlayer.seekMarker(false);
                break;
            case 'A':
                myPlayer.skipTime(-Prefs.getSkipTimeLarge());
                break;
            case 'a':
                myPlayer.skipTime(-Prefs.getSkipTimeMed());
                break;
            case 's':
                myPlayer.skipTime(-Prefs.getSkipTimeSmall());
                break;
            case 'S':
                myPlayer.skipTime(-Prefs.getSkipTimeTiny());
                break;
            case 'D':
                myPlayer.skipTime(Prefs.getSkipTimeTiny());
                break;
            case 'd':
                myPlayer.skipTime(Prefs.getSkipTimeSmall());
                break;
            case 'f':
                myPlayer.skipTime(Prefs.getSkipTimeMed());
                break;
            case 'F':
                myPlayer.skipTime(Prefs.getSkipTimeLarge());
                break;
            case 'g':
                myPlayer.nextFrame();
                break;
            case't':
                myPlayer.addMarker();
                break;
            case'r':
                myPlayer.toggleMarkerSelection();
                break;
            case'q':
                myPlayer.toggleSelectionPlay();
                break;
            case 'k':
            case 'K':
                myPlayer.onApplicationExitRequest();
                break;
            case'm':
            case'M':
                mediaPlayer.audio().setMute(!mediaPlayer.audio().isMute());
                break;
            case KeyEvent.VK_SPACE:
                mediaPlayer.controls().pause();
                break;
            case KeyEvent.VK_LEFT:
                myPlayer.skipTime(-Prefs.getSkipTimeMed());
                break;
            case KeyEvent.VK_RIGHT:
                myPlayer.skipTime(Prefs.getSkipTimeMed());
                break;
            case KeyEvent.VK_UP:
                mediaPlayer.audio().setVolume(Math.min(200, mediaPlayer.audio().volume() + 2)); // 0-200
                break;
            case KeyEvent.VK_DOWN:
                mediaPlayer.audio().setVolume(mediaPlayer.audio().volume() - 2);    // lower bound guarded already
                break;
            case KeyEvent.VK_ESCAPE:
                mediaPlayer.fullScreen().toggle();
                break;
        }
    }
}
