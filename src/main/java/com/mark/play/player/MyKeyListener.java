package com.mark.play.player;

import com.mark.play.IMain;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter  {
    private IMain iMain;
    private EmbeddedMediaPlayer mediaPlayer;

    public MyKeyListener(IMain iMain, EmbeddedMediaPlayer mediaPlayer) {
        this.iMain = iMain;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.printf(".. key0code: %d, key char: %c, shift: %s (%s)\n", e.getKeyCode(), e.getKeyChar(), e.isShiftDown(), KeyEvent.getKeyText(e.getKeyCode()));

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                System.out.println("VK_LEFT...");
                mediaPlayer.controls().skipTime(-5000);
                break;
            case KeyEvent.VK_RIGHT:
                System.out.println("VK_RIGHT...");
                mediaPlayer.controls().skipTime(5000);
                break;
        }

        switch (e.getKeyChar()) {
            case '4':
                mediaPlayer.controls().setTime(4000);
                break;
            case '5':
                mediaPlayer.controls().setPosition(0.5F);
                break;
            case '6':
                mediaPlayer.controls().setPosition(0.6F);
                break;
            case 'k':
            case 'K':
            case 'q':
            case 'Q':
            case 'x':
            case 'X':
                iMain.exitApplication();
                break;
            case 'f':
            case 'F':
                mediaPlayer.controls().nextFrame();
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
