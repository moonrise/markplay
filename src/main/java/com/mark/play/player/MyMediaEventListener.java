package com.mark.play.player;

import com.mark.Log;
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

    @Override
    public void mediaDurationChanged(Media media, long newDuration) {
        super.mediaDurationChanged(media, newDuration);
        //Log.log("media duration changed: %d", newDuration);
        // for some media, duration info is not known when media parsed, but becomes available at later time
        this.playerState.mediaDurationChanged(newDuration);
    }
}