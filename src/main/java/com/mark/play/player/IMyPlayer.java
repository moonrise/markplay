package com.mark.play.player;

public interface IMyPlayer {
    void onApplicationExitRequest();
    void onError(String errorMessage);
    void setMute(boolean mute);
    void setVolume(int volume);
    void setRate(float rate);
    void setTime(long time);
    void skipTime(long delta);
    void nextFrame();
    void addMarker();
    void seekMarker(boolean forward);
    void seek10th(float rate10th);
    void toggleMarkerSelection();
    void toggleSelectionPlay();
}
