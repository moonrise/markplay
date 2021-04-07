package com.mark.play.player;

public interface IMyPlayer {
    void onApplicationExitRequest();
    void onError(String errorMessage);
    void setTime(long time);
    void skipTime(long delta);
    void nextFrame();
    void addMarker();
    void seekMarker(boolean forward);
    void toggleMarkerSelection();
    void toggleSelectionPlay();
}
