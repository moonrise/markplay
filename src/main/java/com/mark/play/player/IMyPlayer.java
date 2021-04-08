package com.mark.play.player;

import com.mark.resource.Resource;

public interface IMyPlayer {
    Resource getResource();
    void onApplicationExitRequest();
    void onError(String errorMessage);
    void setMute(boolean mute);
    void setVolume(int volume);
    void setRate(float rate);
    void setTime(long time);
    void skipTime(long delta);
    void nextFrame();
    void toggleMarker();
    void seekMarker(boolean forward);
    void seek10th(float rate10th);
    void toggleMarkerSelection();
    void toggleSelectionPlay();
}
