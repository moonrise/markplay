package com.mark.play.player;

import com.mark.play.IMain;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyPlayer {
    private IMain iMain;
    private String mrl;
    private EmbeddedMediaPlayerComponent mediaPlayerComponent2;
    private static CallbackMediaPlayerComponent mediaPlayerComponent;


    public MyPlayer(IMain iMain, JPanel container, String mrl) {
        this.iMain = iMain;
        this.mrl = mrl;

        buildPlayer();

        container.add(mediaPlayerComponent, BorderLayout.CENTER);
        container.add(buildControlPanel(), BorderLayout.SOUTH);
    }

    public CallbackMediaPlayerComponent getPlayerComponent() {
        return this.mediaPlayerComponent;
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

        AdaptiveFullScreenStrategy adaptiveFullScreenStrategy = new AdaptiveFullScreenStrategy(iMain.getAppFrame()) {
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

        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MyMediaPlayerEventListener(iMain.getAppFrame(), this.mrl));
        mediaPlayerComponent.mediaPlayer().events().addMediaEventListener(new MyMediaEventListener());

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }
        };

        MouseWheelListener mouseWheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            }
        };

        Component videoSurface = mediaPlayerComponent.videoSurfaceComponent();
        videoSurface.addKeyListener(new MyKeyListener(this.iMain, mediaPlayerComponent.mediaPlayer()));
        videoSurface.addMouseListener(mouseListener);
        videoSurface.addMouseWheelListener(mouseWheelListener);
    }

    private JPanel buildControlPanel() {
        final JButton pauseButton;
        final JButton rewindButton;
        final JButton skipButton;

        JPanel controlPanel = new JPanel();

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }
        });
        controlPanel.add(pauseButton);

        rewindButton = new JButton("Rewind");
        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().skipTime(-10000);
            }
        });
        controlPanel.add(rewindButton);

        skipButton = new JButton("Skip");
        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().skipTime(10000);
            }
        });
        controlPanel.add(skipButton);

        return controlPanel;
    }
}
