package com.mark.main;

import com.mark.Utils;
import com.mark.play.player.EPlayerStateChangeType;
import com.mark.play.player.IMyPlayerStateChangeListener;
import com.mark.play.player.MyPlayerState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StatusBar extends JPanel  implements IMyPlayerStateChangeListener {
    final JTextArea textArea = new JTextArea();
    final JScrollPane textScroll = new JScrollPane (textArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    final int MAX_LINES = 100;
    final ArrayList<String> textLines = new ArrayList<String>();
    private int totalLines = 0;

    public StatusBar() {
        super(new BorderLayout(), true);
        //setBorder(new LineBorder(Color.LIGHT_GRAY));

        textArea.setFont(new Font("helvetica", Font.PLAIN, 12));
        textArea.setForeground(Color.DARK_GRAY);
        textArea.setEditable(false);

        textScroll.setPreferredSize(new Dimension(-1, 38));
        textScroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        add(textScroll);
        setStatusText("Status");
    }

    public void setStatusText(String text) {
        totalLines++;

        textLines.add(0, text);
        if (textLines.size() > MAX_LINES) {
            textLines.remove(MAX_LINES);
        }

        StringBuilder stringBuilder = new StringBuilder();
        final int textLinesSize = textLines.size();
        for (int i=0; i<textLinesSize; i++) {
            stringBuilder.append(String.format(" [%d] %s%s", totalLines - i, textLines.get(i), i < (textLinesSize-1) ? "\n" : ""));
        }

        textArea.setText(stringBuilder.toString());
        textArea.setCaretPosition(0);
    }

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            setStatusText(String.format("%s, Length: %s, Video: %d x %d, %s (%s), Audio: %d channels, %d (sample rate), %s (%s)",
                    playerState.getResource().getPath(),
                    Utils.getTimelineFormatted(playerState.getMediaDuration(), false),
                    playerState.videoWidth, playerState.videoHeight, playerState.videoCodecName, playerState.videoCodecDesc,
                    playerState.audioChannels, playerState.audioBitRate, playerState.audioCodecName, playerState.audioCodecDesc));
        }
        else if (stateChangeType == EPlayerStateChangeType.Error) {
            setStatusText(playerState.getErrorMessage());
        }
    }
}
