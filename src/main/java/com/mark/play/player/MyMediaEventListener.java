package com.mark.play.player;

import uk.co.caprica.vlcj.media.*;

public class MyMediaEventListener extends MediaEventAdapter {
    public MyMediaEventListener() {
    }

    @Override
    public void mediaParsedChanged(Media media, MediaParsedStatus newStatus) {
        if (newStatus == MediaParsedStatus.DONE) {
            final InfoApi info = media.info();
            System.out.printf("media duration: %d\n", info.duration());

            for (VideoTrackInfo videoTrack : info.videoTracks()) {
                System.out.printf("media video track width/height: %d x %d\n", videoTrack.width(), videoTrack.height());
                System.out.printf("media video track aspect ratio: %d / %d\n", videoTrack.sampleAspectRatio(), videoTrack.sampleAspectRatioBase());
                System.out.printf("media video track codec: %s (%s)\n", videoTrack.codecName(), videoTrack.codecDescription());
                //System.out.printf("media video track : %s\n", videoTrack);
            }

            for (AudioTrackInfo audioTrack : info.audioTracks()) {
                System.out.printf("media audio track codec: %s (%s)\n", audioTrack.codecName(), audioTrack.codecDescription());
                System.out.printf("media audio track bitrate: %d, channels/rate: (%d:%d)\n", audioTrack.bitRate(), audioTrack.channels(), audioTrack.rate());
                //System.out.printf("media audio track : %s\n", audioTrack);
            }
        } else {
            System.out.printf("media parsed with error: %s\n", newStatus.toString());
        }
    }
}
