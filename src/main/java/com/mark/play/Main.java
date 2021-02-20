package com.mark.play;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.*;
import uk.co.caprica.vlcj.player.base.*;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.MediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

public class Main {
    private static Main thisApp = null;

    private final JFrame frame;

    private EmbeddedMediaPlayerComponent mediaPlayerComponent2;
    private CallbackMediaPlayerComponent mediaPlayerComponent;


    private final JButton pauseButton;

    private final JButton rewindButton;

    private final JButton skipButton;

    public static void main(String[] args) {
        if (args.length > 0 || args[0].length() > 0) {
            thisApp = new Main(args[0]);
        }
        else {
            System.out.println("Provide a file to play as the first command line argument.");
        }
    }

    public Main(String mrl) {

        //final String png = "c:\\mp\\crown.png";
        //final String mrl = "c:\\mp\\cloud.wmv";

        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        final String[] EMBEDDED_MEDIA_PLAYER_ARGS = {
                "--no-metadata-network-access",         // added to the default
                "--video-title=vlcj video output",
                "--no-snapshot-preview",
                "--quiet",
                "--intf=dummy"
        };

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(EMBEDDED_MEDIA_PLAYER_ARGS);

        AdaptiveFullScreenStrategy adaptiveFullScreenStrategy = new AdaptiveFullScreenStrategy(frame) {
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

        mediaPlayerComponent = new CallbackMediaPlayerComponent();
        mediaPlayerComponent2 = new EmbeddedMediaPlayerComponent(mediaPlayerFactory, null, adaptiveFullScreenStrategy, null, null) {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                System.out.println("Playing...");
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                System.out.println("Finished.");
                mediaPlayer.submit(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.media().play(mrl);
                    }
                });
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, "Failed to play media", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
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

            @Override
            public void mouseClicked(MouseEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.printf("key code: %d, key char: %c, shift: %s (%s)\n", e.getKeyCode(), e.getKeyChar(), e.isShiftDown(), KeyEvent.getKeyText(e.getKeyCode()));

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        System.out.println("VK_LEFT...");
                        mediaPlayerComponent.mediaPlayer().controls().skipTime(-5000);
                        break;
                    case KeyEvent.VK_RIGHT:
                        System.out.println("VK_RIGHT...");
                        mediaPlayerComponent.mediaPlayer().controls().skipTime(5000);
                        break;
                }

                switch (e.getKeyChar()) {
                    case '4':
                        mediaPlayerComponent.mediaPlayer().controls().setTime(4000);
                        break;
                    case '5':
                        mediaPlayerComponent.mediaPlayer().controls().setPosition(0.5F);
                        break;
                    case '6':
                        mediaPlayerComponent.mediaPlayer().controls().setPosition(0.6F);
                        break;
                    case 'k':
                    case 'K':
                    case 'q':
                    case 'Q':
                    case 'x':
                    case 'X':
                        exit();
                        break;
                    case 'f':
                    case 'F':
                        mediaPlayerComponent.mediaPlayer().controls().nextFrame();
                        break;
                    case KeyEvent.VK_SPACE:
                        mediaPlayerComponent.mediaPlayer().controls().pause();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
                        break;
                }
            }
        };

        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);

        JPanel controlsPane = new JPanel();
        pauseButton = new

                JButton("Pause");
        controlsPane.add(pauseButton);
        rewindButton = new

                JButton("Rewind");
        controlsPane.add(rewindButton);
        skipButton = new

                JButton("Skip");
        controlsPane.add(skipButton);
        contentPane.add(controlsPane, BorderLayout.SOUTH);

        pauseButton.addActionListener(new

                                              ActionListener() {
                                                  @Override
                                                  public void actionPerformed(ActionEvent e) {
                                                      mediaPlayerComponent.mediaPlayer().controls().pause();
                                                  }
                                              });

        rewindButton.addActionListener(new

                                               ActionListener() {
                                                   @Override
                                                   public void actionPerformed(ActionEvent e) {
                                                       mediaPlayerComponent.mediaPlayer().controls().skipTime(-10000);
                                                   }
                                               });

        skipButton.addActionListener(new

                                             ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {
                                                     mediaPlayerComponent.mediaPlayer().controls().skipTime(10000);
                                                 }
                                             });

        frame.setContentPane(contentPane);

        frame.setVisible(true);

        /*
        Logo logo = Logo.logo()
                .file(png)
                .position(LogoPosition.TOP_RIGHT)
                .opacity(0.1f)
                .duration(2000)
                .enable();
         */
        //mediaPlayerComponent.mediaPlayer().logo().set(logo);

        Marquee marquee = Marquee.marquee()
                .text(mrl)
                .size(40)
                .colour(Color.WHITE)
                .timeout(3000)
                .position(MarqueePosition.BOTTOM_RIGHT)
                .opacity(0.8f)
                .enable();
        mediaPlayerComponent.mediaPlayer().

                marquee().

                set(marquee);

        mediaPlayerComponent.mediaPlayer().

                media().

                prepare(mrl);
        mediaPlayerComponent.mediaPlayer().

                media().

                parsing().

                parse();

        mediaPlayerComponent.mediaPlayer().

                media().

                play(mrl);

        mediaPlayerComponent.videoSurfaceComponent().

                requestFocus();
        // mediaPlayerComponent.videoSurfaceComponent().requestFocusInWindow(); // alternate; this may work better
    }

    void exit() {
        mediaPlayerComponent.release();
        System.exit(0);
    }
}