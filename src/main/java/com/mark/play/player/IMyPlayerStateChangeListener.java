package com.mark.play.player;

public interface IMyPlayerStateChangeListener {
    void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType);
}
