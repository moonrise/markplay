package com.mark.play.player;

public interface IMyPlayer {
    void onApplicationExitRequest();
    void onAddMarkerRequest();
    void onError(String errorMessage);
    void setTime(long time);
    void skipTime(long delta);
    void nextFrame();
    void seekMarker(boolean forward);
}
