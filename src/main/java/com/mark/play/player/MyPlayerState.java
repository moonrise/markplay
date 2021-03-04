package com.mark.play.player;

import com.mark.play.Utils;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.util.ArrayList;

public class MyPlayerState extends MediaPlayerEventAdapter {
    private ArrayList<IMyPlayerStateChangeListener> stateChangeListeners = new ArrayList<>();

    private float currentPlayTime = 0;
    private String errorMessage = "";

    public void registerStateChangeListener(IMyPlayerStateChangeListener listener) {
        this.stateChangeListeners.add(listener);
    }

    private void notifyStateChangeListeners(EPlayerStateChangeType changeType) {
        for (IMyPlayerStateChangeListener listener : this.stateChangeListeners) {
            listener.onPlayerStateChange(this, changeType);
        }
    }

    public float getCurrentPlayTime() {
        return this.currentPlayTime;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayStarted);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        this.currentPlayTime = newTime;
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayTime);
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
}
