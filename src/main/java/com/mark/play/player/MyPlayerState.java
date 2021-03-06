package com.mark.play.player;

import com.mark.play.Log;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.ArrayList;

public class MyPlayerState extends MediaPlayerEventAdapter {
    private ArrayList<IMyPlayerStateChangeListener> stateChangeListeners = new ArrayList<>();

    private EmbeddedMediaPlayer mediaPlayer;
    private float playTime = 0;
    private String errorMessage = "";

    public MyPlayerState(EmbeddedMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void registerStateChangeListener(IMyPlayerStateChangeListener listener) {
        this.stateChangeListeners.add(listener);
    }

    private void notifyStateChangeListeners(EPlayerStateChangeType changeType) {
        for (IMyPlayerStateChangeListener listener : this.stateChangeListeners) {
            listener.onPlayerStateChange(this, changeType);
        }
    }

    public float getPlayTime() {
        return this.playTime;
    }

    public int getVolume() {
        Log.log("getVolume %d", this.mediaPlayer.audio().volume());
        return this.mediaPlayer.audio().volume() / 2;       // normalize it to 0-100 (from 0-200)
    }

    public boolean isMute() {
        return this.mediaPlayer.audio().isMute();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayStarted);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayFinished);
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        this.errorMessage = "Failed to play media";
        this.notifyStateChangeListeners(EPlayerStateChangeType.Error);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        this.playTime = newTime;
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayTime);
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition /* 0-1 ? */) {
        //Log.log("position changed %.1f %%", newPosition * 100);
    }

    @Override
    public void muted(MediaPlayer mediaPlayer, boolean muted) {
        //Log.log("mute %b", muted);
        super.muted(mediaPlayer, muted);
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
        Log.log("volume %f", volume);
        super.volumeChanged(mediaPlayer, volume);
    }
}
