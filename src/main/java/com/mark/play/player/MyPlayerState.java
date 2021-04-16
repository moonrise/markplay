package com.mark.play.player;

import com.mark.Prefs;
import com.mark.Utils;
import com.mark.resource.EResourceChangeType;
import com.mark.resource.IResourceChangeListener;
import com.mark.Log;
import com.mark.resource.Resource;
import uk.co.caprica.vlcj.media.*;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
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

    public void registerPlayerStateChangeListener(IMyPlayerStateChangeListener listener) {
        this.stateChangeListeners.add(listener);
    }

    private void notifyStateChangeListeners(EPlayerStateChangeType changeType) {
        notifyStateChangeListeners_(changeType, this);
    }

    private void notifyStateChangeListeners_(EPlayerStateChangeType changeType, MyPlayerState playerState) {
        // strange thing can happen without this invokeLater
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (IMyPlayerStateChangeListener listener : playerState.stateChangeListeners) {
                    listener.onPlayerStateChange(playerState, changeType);
                }
            }
        });
    }

    public Resource getResource() {
        return this.resource;
    }

    @Override
    public void onResourceChange(Resource resource, EResourceChangeType type) {
        //Log.log("PlayerState - resource changed: %s", type);
    }

    public int videoWidth;
    public int videoHeight;
    public int videoSampleAspectRatio;
    public int videoSampleAspectRatioBase;
    public String videoCodecName;
    public String videoCodecDesc;

    public int audioBitRate;
    public int audioChannels;
    public int audioRate;
    public String audioCodecName;
    public String audioCodecDesc;

    public void mediaParsed(Media media, MediaParsedStatus newStatus) {
        this.media = media;

        // new media -> reset the playtime
        this.playTime = 0;

        if (newStatus == MediaParsedStatus.DONE) {
            final InfoApi info = media.info();
            resource.setDuration(info.duration());
            //Log.log("media parsed - duration: %d", media.info().duration());

            for (VideoTrackInfo videoTrack : info.videoTracks()) {
                videoWidth = videoTrack.width();
                videoHeight = videoTrack.height();
                videoSampleAspectRatio = videoTrack.sampleAspectRatio();
                videoSampleAspectRatioBase = videoTrack.sampleAspectRatioBase();
                videoCodecName = videoTrack.codecName();
                videoCodecDesc = videoTrack.codecDescription();
                //Log.log("media video track width/height: %d x %d", videoTrack.width(), videoTrack.height());
                //Log.log("media video track aspect ratio: %d / %d", videoTrack.sampleAspectRatio(), videoTrack.sampleAspectRatioBase());
                //Log.log("media video track codec: %s (%s)", videoTrack.codecName(), videoTrack.codecDescription());
                //Log.log("media video track : %s\n", videoTrack);
                break;
            }

            for (AudioTrackInfo audioTrack : info.audioTracks()) {
                audioBitRate = audioTrack.bitRate();
                audioChannels = audioTrack.channels();
                audioRate = audioTrack.rate();
                audioCodecName = audioTrack.codecName();
                audioCodecDesc = audioTrack.codecDescription();
                //Log.log("media audio track codec: %s (%s)", audioTrack.codecName(), audioTrack.codecDescription());
                //Log.log("media audio track bitrate: %d, channels/rate: (%d:%d)", audioTrack.bitRate(), audioTrack.channels(), audioTrack.rate());
                //Log.log("media audio track : %s", audioTrack);
                break;
            }

            this.notifyStateChangeListeners(EPlayerStateChangeType.MediaParsed);
        } else {
            Log.err("media parsed with error: %s", newStatus.toString());
            this.errorMessage = newStatus.toString();
            this.notifyStateChangeListeners(EPlayerStateChangeType.MediaParseFailed);
        }
    }

    public void mediaDurationChanged(long newDuration) {
        // for some media, duration info is not known when media parsed, but becomes available at later time
        resource.setDuration(newDuration);
    }

    public long getMediaDuration() {
        if (resource != null) {
            return resource.duration;
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
        this.errorMessage = String.format("Failed to play media: %s", resource.getPath());
        this.notifyStateChangeListeners(EPlayerStateChangeType.Error);
    }

    @Override
    public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
        //Log.log("time changed: %d", newTime);
        this.playTime = newTime;
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayTime);
    }

    // update required in a paused state as timeChanged event does not get triggered.
    void updatePlayTime() {
        this.playTime = mediaPlayer.status().time();
        //Log.log("time changed (while paused): %d", this.playTime);
        this.notifyStateChangeListeners(EPlayerStateChangeType.PlayTime);
    }

    @Override
    public void positionChanged(MediaPlayer mediaPlayer, float newPosition /* 0-1 ? */) {
        //Log.log("position changed %.1f %%", newPosition * 100);
    }

    @Override
    public void muted(MediaPlayer mediaPlayer, boolean muted) {
        super.muted(mediaPlayer, muted);
        //Log.log("mute %b", muted);
        Prefs.setMute(muted);
    }

    @Override
    public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
        super.volumeChanged(mediaPlayer, volume);
        //Log.log("volume %f", volume);
        Prefs.setVolume((int)(volume * 100));
    }
}
