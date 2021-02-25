package com.mark.play.player;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;


public class MyMediaPlayerEventListener extends MediaPlayerEventAdapter {
    private IMyPlayer myPlayer;


    public MyMediaPlayerEventListener(IMyPlayer myPlayer) {
        this.myPlayer = myPlayer;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        myPlayer.onPlayStarted();
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        myPlayer.onTimelineChange(newTime);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        myPlayer.onPlayFinished();
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        myPlayer.onError("Failed to play media");
    }
}
