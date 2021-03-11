package com.mark.play.player;

public interface IMyPlayer {
    void onApplicationExitRequest();
    void onAddMarkerRequest();
    void onError(String errorMessage);
}
