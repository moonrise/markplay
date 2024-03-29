package com.mark.play.player;

import com.mark.Prefs;
import com.mark.main.IMain;
import com.mark.Log;
import com.mark.resource.Resource;
import com.mark.Utils;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.*;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MyPlayer implements com.mark.play.player.IMyPlayer, IMyPlayerStateChangeListener {
    private IMain main;
    private Resource resource;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent2;

    private CallbackMediaPlayerComponent mediaPlayerComponent;
    private EmbeddedMediaPlayer mediaPlayer;
    private Component videoSurface;

    private Timeline timeline;
    private MyPlayerState playerState;

    private MyKeyListener keyListener;


    public MyPlayer(IMain main, JPanel container) {
        this.main = main;

        buildPlayer();
        setVolume(Prefs.getVolume());
        setMute(Prefs.isMute());

        container.add(mediaPlayerComponent, BorderLayout.CENTER);
        container.add(buildControlPanel(), BorderLayout.SOUTH);
    }

    private void buildPlayer() {
        final String[] EMBEDDED_MEDIA_PLAYER_ARGS = {
                "--no-metadata-network-access",         // added to the default
                "--video-title=vlcj video output",
                "--no-snapshot-preview",
                "--quiet",
                "--intf=dummy"
        };

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(EMBEDDED_MEDIA_PLAYER_ARGS);

        AdaptiveFullScreenStrategy adaptiveFullScreenStrategy = new AdaptiveFullScreenStrategy(main.getAppFrame()) {
            @Override
            protected void onBeforeEnterFullScreen() {
                Log.log("Entering full screen...");
                //controlsPane.setVisible(false);
            }

            @Override
            protected void onAfterExitFullScreen() {
                Log.log("Exiting full screen...");
                //controlsPane.setVisible(true);
            }
        };

        mediaPlayerComponent = new CallbackMediaPlayerComponent(mediaPlayerFactory, adaptiveFullScreenStrategy, null, true, null, null, null, null);
        //mediaPlayerComponent = new EmbeddedMediaPlayerComponent(mediaPlayerFactory, null, adaptiveFullScreenStrategy, null, null);
        mediaPlayer = mediaPlayerComponent.mediaPlayer();
        videoSurface = mediaPlayerComponent.videoSurfaceComponent();

        this.playerState = new MyPlayerState(mediaPlayer);
        this.registerPlayerStateChangeListener(this);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Log.log("Player: mouse clicked");
                setFocus();
            }
        };

        MouseWheelListener mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            }
        };

        this.keyListener = new MyKeyListener(main, this, mediaPlayer);
        videoSurface.addKeyListener(keyListener);
        videoSurface.addMouseListener(mouseListener);
        videoSurface.addMouseWheelListener(mouseWheelListener);
    }

    public void processPlayerKeys(KeyEvent e) {
        this.keyListener.keyPressed(e);
    }

    public void registerPlayerStateChangeListener(IMyPlayerStateChangeListener listener) {
        this.playerState.registerPlayerStateChangeListener(listener);
    }

    private JPanel buildControlPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());


        timeline = new Timeline(this.playerState, main);
        container.add(timeline, BorderLayout.NORTH);
        container.add(buildButtonPanel(), BorderLayout.SOUTH);

        return container;
    }

    private JPanel buildButtonPanel() {
        final JButton pauseButton;
        final JButton rewindButton;
        final JButton skipButton;
        final JButton clearButton;

        JPanel controlPanel = new JPanel();

        pauseButton = new JButton("Play/Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().pause();
            }
        });
        controlPanel.add(pauseButton);

        rewindButton = new JButton("Rewind");
        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().skipTime(-10000);
            }
        });
        controlPanel.add(rewindButton);

        skipButton = new JButton("Skip");
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().skipTime(10000);
            }
        });
        controlPanel.add(skipButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMedia();
            }
        });
        controlPanel.add(clearButton);

        return controlPanel;
    }

    public void release() {
        mediaPlayerComponent.release();
    }

    public void setVolume(int volume) {     // percentage of 0-200?
        mediaPlayer.audio().setVolume(volume);
        updateOnPause();
    }

    public void setMute(boolean mute) {
        mediaPlayer.audio().setMute(mute);
        updateOnPause();
    }

    public void setRate(float rate) {
        mediaPlayer.controls().setRate(rate);
        updateOnPause();
    }

    public void startResource(Resource resource) {
        if (resource == null) {
            clearMedia();
            return;
        }

        File file = new File((resource.getPath()));
        if (!file.exists()) {
            clearMedia();
            onError(String.format("File '%s' does not exist.", resource.getPath()));
            return;
        }

        this.resource = resource;
        //let's not hash files on start playing as it can delay (having a lockup effect) for large files, especially
        //when a file is loaded in an app launch. Users can choose to hash files with the menu explicitly.
        //this.resource.initFileSizeAndHash();
        playerState.setResource(resource);
        startMedia(resource.getPath());
    }

    public void clearMedia() {
        if (mediaPlayer.media().isValid()) {
            mediaPlayer.controls().stop();

            // TODO: unload the media  (not quite; the current media still visible) - for now load a blank image
            String mrl = new File(Utils.getResourcePath("/icons/black.png")).getPath();
            mediaPlayer.media().prepare(mrl);
            mediaPlayer.media().parsing().parse();
            mediaPlayer.media().startPaused(mrl);
        }
    }

    public void startMedia(String mrl) {
        mediaPlayer.media().prepare(mrl);
        mediaPlayer.media().parsing().parse();

        if (Prefs.isPlayOnLoad()) {
            mediaPlayer.media().play(mrl);
            // mediaPlayer.media().start(mrl); // not sure how play is different from start
        }
        else {
            mediaPlayer.media().startPaused(mrl);
        }

        // TODO: should the player keyboard actions be serviced from the whole frame as well?
        // give the focus to player for keyboard events to be received, but then the table loses the focus
        if (Prefs.isFocusOnPlayer()) {
            setFocus();
        }
    }

    public void setFocus() {
        //videoSurface.requestFocus();          // key event listener does not seem to work (at least in MacOS)
        videoSurface.requestFocusInWindow();    // this variation seems to work better (at least in MacOS)
    }

    public void updateMarquee() {
        String marguee = String.format("%s V%d%s",
                Utils.getTimelineFormatted(playerState.getPlayTime(), false),
                playerState.getVolume(),
                playerState.isMute() ? "M":"");
        setMarquee(marguee);
    }

    private void setMarquee(String text) {
        Marquee marquee = Marquee.marquee()
                .text(text)
                .size(40)
                .colour(Color.WHITE)
                //.timeout(3000)
                .position(MarqueePosition.BOTTOM_RIGHT)
                .opacity(0.8f)
                .enable();

        mediaPlayer.submit(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.marquee().set(marquee);
            }
        });
    }

    public void setLogo(String imageFilePath) {
        Logo logo = Logo.logo()
                .file(imageFilePath)
                .position(LogoPosition.TOP_RIGHT)
                .opacity(0.5f)
                .duration(2000)
                .enable();
        mediaPlayer.logo().set(logo);
    }

    @Override
    public void onApplicationExitRequest() {
        this.main.exitApplication();
    }

    @Override
    public void onError(String errorMessage) {
        Log.log(errorMessage);
        main.displayStatusMessage(errorMessage);
        //main.displayErrorMessage(errorMessage);
    }

    @Override
    public void setTime(long time) {
        mediaPlayer.controls().setTime(trimTime(time));
        updateOnPause();
    }

    @Override
    public void skipTime(long delta) {
        mediaPlayer.controls().skipTime(delta);
        /*
        long target = playerState.getPlayTime() + delta;
        //Log.log("skip delta: %d, target: %d", delta, target);
        setTime(target);
        */
    }

    @Override
    public void nextFrame() {
        mediaPlayer.controls().nextFrame();
        updateOnPause();
    }

    @Override
    public void toggleMarker() {
        long currentTime = playerState.getPlayTime();
        this.resource.toggleMarker(currentTime);
        updateOnPause();
        //Log.log("toggle marker at %d", currentTime);
    }

    @Override
    public void seekMarker(boolean forward) {
        if (!forward && Prefs.isPlaySelectedMarkers()) {
            long nextSelected = resource.getSelectedMarkerTime(playerState.getPlayTime(), true);
            if (nextSelected >= 0) {
                setTime(nextSelected);
                return;
            }
        }

        setTime(resource.getAdjacentMarkerTime(playerState.getPlayTime(), playerState.getMediaDuration(), forward, isPaused()));
    }

    @Override
    public void seek10th(float rate10th) {
        setTime((long)(playerState.getMediaDuration()*rate10th));
    }

    @Override
    public void toggleMarkerSelection() {
        resource.toggleMarkerSelection(playerState.getPlayTime());
        updateOnPause();
    }

    @Override
    public void toggleSelectionPlay() {
        Prefs.setPlaySelectedMarkers(!Prefs.isPlaySelectedMarkers());
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.PlayTime) {
            updateMarquee();
            if (Prefs.isPlaySelectedMarkers()) {
                long nextSelected = resource.getSelectedMarkerTime(playerState.getPlayTime(), false);
                if (nextSelected >= 0) {
                    setTime(nextSelected);
                }
            }
        }
        else if (stateChangeType == EPlayerStateChangeType.Volume) {
            updateMarquee();
        }
        else if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            updateMarquee();
        }
        else if (stateChangeType == EPlayerStateChangeType.PlayFinished) {
            // repeat?
            mediaPlayer.submit(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.media().play(resource.getPath());
                }
            });
        }
    }

    private boolean isPaused() {
        return  mediaPlayer.status().state() == State.PAUSED;
    }

    private boolean isPlaying() {
        return  mediaPlayer.status().state() == State.PLAYING;
    }

    // trigger update if paused as the time change event does not come in from the underlying library
    private void updateOnPause() {
        if (isPaused()) {
            playerState.updatePlayTime();   // playtime update will do (an arbitrary choice as it is in a paused state)
        }
    }

    private long trimTime(long time) {
        if (time < 0) {
            return 0;
        }

        if (time > playerState.getMediaDuration()) {
            return playerState.getMediaDuration();
        }

        return time;
    }
}