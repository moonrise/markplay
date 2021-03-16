package com.mark.play.player;

public enum EPlayerStateChangeType {
    MediaLoaded,
    MediaUnloaded,
    MediaParsed,
    MediaParsedFailed,
    PlayStarted,
    PlayPaused,
    PlayResumed,
    PlayStopped,
    PlayFinished,
    PlayTime,
    Volume,
    Error,
}
