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

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                mediaPlayer.controls().skipTime(-Prefs.getSkipTimeMed());
                break;
            case KeyEvent.VK_RIGHT:
                mediaPlayer.controls().skipTime(Prefs.getSkipTimeMed());
                break;
            case KeyEvent.VK_UP:
                mediaPlayer.audio().setVolume(Math.min(200, mediaPlayer.audio().volume() + 2)); // 0-200
                break;
            case KeyEvent.VK_DOWN:
                mediaPlayer.audio().setVolume(mediaPlayer.audio().volume() - 2);    // lower bound guarded already
                break;
            case't':
            case'T':
                myPlayer.onAddMarkerRequest();
                break;
            case'm':
            case'M':
                mediaPlayer.audio().setMute(!mediaPlayer.audio().isMute());
                break;
        }

        switch (e.getKeyChar()) {
            case 'A':
                mediaPlayer.controls().skipTime(-Prefs.getSkipTimeLarge());
                break;
            case 'a':
                mediaPlayer.controls().skipTime(-Prefs.getSkipTimeMed());
                break;
            case 's':
                mediaPlayer.controls().skipTime(-Prefs.getSkipTimeSmall());
                break;
            case 'S':
                mediaPlayer.controls().skipTime(-Prefs.getSkipTimeTiny());
                break;
            case 'D':
                mediaPlayer.controls().skipTime(Prefs.getSkipTimeTiny());
                break;
            case 'd':
                mediaPlayer.controls().skipTime(Prefs.getSkipTimeSmall());
                break;
            case 'f':
                mediaPlayer.controls().skipTime(Prefs.getSkipTimeMed());
                break;
            case 'F':
                mediaPlayer.controls().skipTime(Prefs.getSkipTimeLarge());
                break;
            case 'g':
                mediaPlayer.controls().nextFrame();
                break;
            case 'k':
            case 'K':
                myPlayer.onApplicationExitRequest();
                break;
            case KeyEvent.VK_SPACE:
                mediaPlayer.controls().pause();
                break;
            case KeyEvent.VK_ESCAPE:
                mediaPlayer.fullScreen().toggle();
                break;
        }
    }
}
