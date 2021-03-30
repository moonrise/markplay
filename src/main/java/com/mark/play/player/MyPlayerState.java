package com.mark.play.player;

import com.mark.resource.EResourceChangeType;
import com.mark.resource.IResourceChangeListener;
import com.mark.Log;
import com.mark.resource.Resource;
import uk.co.caprica.vlcj.media.*;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.ArrayList;

public class MyPlayerState extends MediaPlayerEventAdapter implements IResourceChangeListener {
    private ArrayList<IMyPlayerStateChangeListener> stateChangeListeners = new ArrayList<>();

    private EmbeddedMediaPlayer mediaPlayer;
    private Media media;
    private Resource resource;


    private long playTime = 0;
    private String errorMessage = "";

    public MyPlayerState(EmbeddedMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        this.mediaPlayer.events().addMediaEventListener(new MyMediaEventListener(this));
        this.mediaPlayer.events().addMediaPlayerEventListener(this);
    }

    public void setResource(Resource resource) {
        if (this.resource != null) {
            this.resource.unRegisterChangeListener(this);
        }

        this.resource = resource;

        if (this.resource != null) {
            this.resource.registerChangeListener(this);
        }
    }

    public Media getMedia() {
        return this.media;
    }

    public void registerStateChangeListener(IMyPlayerStateChangeListener listener) {
        this.stateChangeListeners.add(listener);
    }

    private void notifyStateChangeListeners(EPlayerStateChangeType changeType) {
        for (IMyPlayerStateChangeListener listener : this.stateChangeListeners) {
            listener.onPlayerStateChange(this, changeType);
        }
    }

    public Resource getResource() {
        return this.resource;
    }

    @Override
    public void onResourceChange(Resource resource, EResourceChangeType type) {
    }

    public void mediaParsed(Media media, MediaParsedStatus newStatus) {
        this.media = media;

        if (newStatus == MediaParsedStatus.DONE) {
            this.notifyStateChangeListeners(EPlayerStateChangeType.MediaParsed);

            final InfoApi info = media.info();
            Log.log("media duration: %d", info.duration());

            for (VideoTrackInfo videoTrack : info.videoTracks()) {
                Log.log("media video track width/height: %d x %d", videoTrack.width(), videoTrack.height());
                Log.log("media video track aspect ratio: %d / %d", videoTrack.sampleAspectRatio(), videoTrack.sampleAspectRatioBase());
                Log.log("media video track codec: %s (%s)", videoTrack.codecName(), videoTrack.codecDescription());
                //Log.log("media video track : %s\n", videoTrack);
            }

            for (AudioTrackInfo audioTrack : info.audioTracks()) {
                Log.log("media audio track codec: %s (%s)", audioTrack.codecName(), audioTrack.codecDescription());
                Log.log("media audio track bitrate: %d, channels/rate: (%d:%d)", audioTrack.bitRate(), audioTrack.channels(), audioTrack.rate());
                //Log.log("media audio track : %s", audioTrack);
            }
        } else {
            Log.err("media parsed with error: %s", newStatus.toString());
            this.notifyStateChangeListeners(EPlayerStateChangeType.MediaParsedFailed);
            this.errorMessage = newStatus.toString();
        }
    }

    public long getMediaDuration() {
        if (this.media != null) {
            return this.media.info().duration();
        }
        return 1000;        // default 1 second (better than zero?)
    }

    public long getPlayTime() {
        return this.playTime;
    }

    public int getVolume() {
        //Log.log("getVolume %d", this.mediaPlayer.audio().volume());
        return this.mediaPlayer.audio().volume() / 2;       // normalize it to 0-100 (from 0-200)
    }

    public boolean isMute() {
        return this.mediaPlayer.audio().isMute();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayStarted);
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayFinished);
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        this.errorMessage = "Failed to play media";
        this.notifyStateChangeListeners(EPlayerStateChangeType.Error);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        this.playTime = newTime;
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayTime);
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition /* 0-1 ? */) {
        //Log.log("position changed %.1f %%", newPosition * 100);
    }

    @Override
    public void muted(MediaPlayer mediaPlayer, boolean muted) {
        //Log.log("mute %b", muted);
        super.muted(mediaPlayer, muted);
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
        //Log.log("volume %f", volume);
        super.volumeChanged(mediaPlayer, volume);
    }
}
