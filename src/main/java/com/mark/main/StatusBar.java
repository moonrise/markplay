package com.mark.main;

import com.mark.Utils;
import com.mark.play.player.EPlayerStateChangeType;
import com.mark.play.player.IMyPlayerStateChangeListener;
import com.mark.play.player.MyPlayerState;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel  implements IMyPlayerStateChangeListener {
    final JLabel status = new JLabel();

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT), true);

        status.setFont(new Font("helvetica", Font.PLAIN, 12));
        status.setForeground(Color.DARK_GRAY);
        add(status);
        setStatusText("Hello there");
    }

    public void setStatusText(String text) {
        status.setText(text);
    }

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            setStatusText(String.format("%s, Length: %s, Video: %d x %d, %s (%s), Audio: %d channels, %d (sample rate), %s (%s)",
                    playerState.getResource().path,
                    Utils.getTimelineFormatted(playerState.getMediaDuration(), false),
                    playerState.videoWidth, playerState.videoHeight, playerState.videoCodecName, playerState.videoCodecDesc,
                    playerState.audioChannels, playerState.audioBitRate, playerState.audioCodecName, playerState.audioCodecDesc));
        }
        else if (stateChangeType == EPlayerStateChangeType.Error) {
            setStatusText(playerState.getErrorMessage());
        }
    }
}
