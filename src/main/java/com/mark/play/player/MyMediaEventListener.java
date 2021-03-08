package com.mark.play.player;

import uk.co.caprica.vlcj.media.*;

public class MyMediaEventListener extends MediaEventAdapter {
    MyPlayerState playerState;

    public MyMediaEventListener(MyPlayerState playerState) {
        this.playerState = playerState;
    }

    @Override
    public void mediaParsedChanged(Media media, MediaParsedStatus newStatus) {
        this.playerState.mediaParsed(media, newStatus);
    }
}
