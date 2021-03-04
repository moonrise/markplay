package com.mark.play.player;

import com.mark.play.IMain;
import com.mark.play.Utils;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.Logo;
import uk.co.caprica.vlcj.player.base.LogoPosition;
import uk.co.caprica.vlcj.player.base.Marquee;
import uk.co.caprica.vlcj.player.base.MarqueePosition;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyPlayer implements IMyPlayer, IMyPlayerStateChangeListener {
    private IMain main;
    private String mrl;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent2;

    private CallbackMediaPlayerComponent mediaPlayerComponent;
    private EmbeddedMediaPlayer mediaPlayer;
    private Component videoSurface;

    private Timeline timeline;

    private MyPlayerState playerState = new MyPlayerState();


    public MyPlayer(IMain main, JPanel container, String mrl) {
        this.main = main;
        this.mrl = mrl;

        buildPlayer();

        this.playerState.registerStateChangeListener(this);

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
                System.out.println("Entering full screen...");
                //controlsPane.setVisible(false);
            }

            @Override
            protected void onAfterExitFullScreen() {
                System.out.println("Exiting full screen...");
                //controlsPane.setVisible(true);
            }
        };

        mediaPlayerComponent = new CallbackMediaPlayerComponent(mediaPlayerFactory, adaptiveFullScreenStrategy, null, true, null, null, null, null);
        //mediaPlayerComponent = new EmbeddedMediaPlayerComponent(mediaPlayerFactory, null, adaptiveFullScreenStrategy, null, null);
        mediaPlayer = mediaPlayerComponent.mediaPlayer();
        videoSurface = mediaPlayerComponent.videoSurfaceComponent();

        mediaPlayer.events().addMediaPlayerEventListener(this.playerState);
        mediaPlayer.events().addMediaEventListener(new MyMediaEventListener());

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mediaPlayer.controls().pause();
            }
        };

        MouseWheelListener mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            }
        };

        videoSurface.addKeyListener(new MyKeyListener(this.main, mediaPlayer));
        videoSurface.addMouseListener(mouseListener);
        videoSurface.addMouseWheelListener(mouseWheelListener);
    }

    private JPanel buildControlPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());


        timeline = new Timeline(this.playerState);
        container.add(timeline, BorderLayout.NORTH);
        container.add(buildButtonPanel(), BorderLayout.SOUTH);

        return container;
    }

    private JPanel buildButtonPanel() {
        final JButton pauseButton;
        final JButton rewindButton;
        final JButton skipButton;

        JPanel controlPanel = new JPanel();

        pauseButton = new JButton("P");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().pause();
            }
        });
        controlPanel.add(pauseButton);

        rewindButton = new JButton("R");
        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().skipTime(-10000);
            }
        });
        controlPanel.add(rewindButton);

        skipButton = new JButton("S");
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayer.controls().skipTime(10000);
            }
        });
        controlPanel.add(skipButton);

        return controlPanel;
    }

    public void release() {
        mediaPlayerComponent.release();
    }

    public void setVolume(int volume) {     // percentage of 0-200?
        mediaPlayer.audio().setVolume(volume);
    }

    public void setMute() {
        mediaPlayer.audio().setMute(true);
    }

    public void setRate(float rate) {
        mediaPlayer.controls().setRate(rate);
    }

    public void play(String mrl) {
        mediaPlayer.media().prepare(mrl);
        mediaPlayer.media().parsing().parse();
        mediaPlayer.media().play(mrl);
    }

    public void setFocus() {
        videoSurface.requestFocus();
        //videoSurface().requestFocusInWindow();    // this may work better under certain cases?
    }

    public void updateMarquee(String newTime) {
        setMarquee(newTime);
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
        mediaPlayer.marquee().set(marquee);
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
    public void onError(String errorMessage) {
        main.displayErrorMessage(errorMessage);
    }

    /*
    @Override
    public void onPlayStarted() {
        //System.out.println("timeline starting...");
    }

    @Override
    public void onPlayFinished() {
        //System.out.println("timeline finished.");

        // repeat?
        mediaPlayer.submit(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.media().play(mrl);
            }
        });
    }

    @Override
    public void onTimelineChange(float newTime) {
        timeline.onTimelineChange(newTime);
        playerState.setCurrentPlayTime(newTime);
    }
     */

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.PlayTime) {
            updateMarquee(Utils.getTimelineFormatted(playerState.getCurrentPlayTime(), 0));
        }
    }
}