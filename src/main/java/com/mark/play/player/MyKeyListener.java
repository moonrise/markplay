package com.mark.play.player;

import com.mark.Prefs;
import com.mark.main.IMain;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter  {
    private IMain main;
    private IMyPlayer myPlayer;
    private EmbeddedMediaPlayer mediaPlayer;

    public MyKeyListener(IMain main, IMyPlayer myPlayer, EmbeddedMediaPlayer mediaPlayer) {
        this.main = main;
        this.myPlayer = myPlayer;
        this.mediaPlayer = mediaPlayer;
    }

    private void processTildaToZero(int keyCode) {
        float rate10th = 0;

        if (keyCode == 192) {
            rate10th = 0;
        }
        else if (keyCode == 48) {
            rate10th = 1;
        }
        else {
            rate10th = (keyCode - 48)/10F;
        }

        //Log.log("KeyCode: %d, rate: %.1f", keyCode, rate10th);
        myPlayer.seek10th(rate10th);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.printf("key code: %d, key char: %c, shift: %s (%s)\n", e.getKeyCode(), e.getKeyChar(), e.isShiftDown(), KeyEvent.getKeyText(e.getKeyCode()));

        int keyCode = e.getKeyCode();
        if (keyCode >= 48 /* 0 */ && keyCode <=57 /* 9 */ || keyCode == 192 /* ` */) {
            processTildaToZero(keyCode);
            return;
        }

        switch (e.getKeyChar()) {
            case ' ':
                mediaPlayer.controls().pause();
                return;
            case 'c':
                main.navigateResourceList(true, false);
                return;
            case 'x':
                main.navigateResourceList(false, false);
                return;
            case 'v':
                main.navigateResourceList(true, true);
                return;
            case 'z':
                main.navigateResourceList(false, true);
                return;
            case 'e':
                myPlayer.seekMarker(true);
                return;
            case 'w':
                myPlayer.seekMarker(false);
                return;
            case 'A':
                myPlayer.skipTime(-Prefs.getSkipTimeLarge());
                return;
            case 'a':
                myPlayer.skipTime(-Prefs.getSkipTimeMed());
                return;
            case 's':
                myPlayer.skipTime(-Prefs.getSkipTimeSmall());
                return;
            case 'S':
                myPlayer.skipTime(-Prefs.getSkipTimeTiny());
                return;
            case 'D':
                myPlayer.skipTime(Prefs.getSkipTimeTiny());
                return;
            case 'd':
                myPlayer.skipTime(Prefs.getSkipTimeSmall());
                return;
            case 'f':
                myPlayer.skipTime(Prefs.getSkipTimeMed());
                return;
            case 'F':
                myPlayer.skipTime(Prefs.getSkipTimeLarge());
                return;
            case 'g':
                myPlayer.nextFrame();
                return;
            case't':
                myPlayer.toggleMarker();
                return;
            case'r':
                myPlayer.toggleMarkerSelection();
                return;
            case'q':
                myPlayer.toggleSelectionPlay();
                return;
            case 'k':
            case 'K':
                myPlayer.onApplicationExitRequest();
                return;
            case'b':
            case'B':
                myPlayer.setMute(!mediaPlayer.audio().isMute());
                return;
            case'm':
                myPlayer.getResource().toggleFavorite();
                return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                myPlayer.skipTime(-Prefs.getSkipTimeMed());
                return;
            case KeyEvent.VK_RIGHT:
                myPlayer.skipTime(Prefs.getSkipTimeMed());
                return;
            case KeyEvent.VK_UP:
                myPlayer.setVolume(Math.min(200, mediaPlayer.audio().volume() + 2)); // 0-200
                return;
            case KeyEvent.VK_DOWN:
                myPlayer.setVolume(mediaPlayer.audio().volume() - 2);    // lower bound guarded already
                return;
            case KeyEvent.VK_ESCAPE:
                mediaPlayer.fullScreen().toggle();
                return;
        }
    }
}
