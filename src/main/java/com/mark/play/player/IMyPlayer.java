package com.mark.play.player;

public interface IMyPlayer {
    MyPlayerState getPlayerState();
    void onApplicationExitRequest();
    void onAddMarkerRequest();
    void onError(String errorMessage);
}
