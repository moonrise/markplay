package com.mark.play.player;

import com.mark.Utils;
import com.mark.play.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Timeline extends JPanel implements IMyPlayerStateChangeListener, IResourceChangeListener {
    final int height = 50;
    final int middle = 30;

    private Font font = new Font("helvetica", Font.PLAIN, 12);
    private boolean graphicsSet = false;
    private int timeWidth;

    private MyPlayerState playerState;

    public Timeline(MyPlayerState playerState) {
        super(true);
        this.playerState = playerState;
        this.playerState.registerStateChangeListener(this);

        setBorder(new LineBorder(Color.LIGHT_GRAY));
        setPreferredSize(new Dimension(0, this.height));
    }

    private void onFirstGraphics(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        FontMetrics metrics = g.getFontMetrics(this.font);
        this.timeWidth = metrics.stringWidth("H:MM:SS.S");
    }

    @Override
    public void onResourceChange(Resource resource, EResourceChangeType type) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;      // always the case, I assume

        if (!this.graphicsSet) {
            this.onFirstGraphics(g);
            this.graphicsSet = true;
        }

        long duration = this.playerState.getMediaDuration();
        long playTime = this.playerState.getPlayTime();
        float playRatio = playTime/(float)duration;
        float zoomScale = 10.0F;

        // top frame
        g2.setStroke(new BasicStroke(1));
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, getWidth(), this.middle/2);
        g.drawRect(0, this.middle/2, getWidth(), this.middle/2);

        // zoom segment bar
        float segmentDuration = duration/zoomScale;
        float segmentWest = playTime - segmentDuration/2;
        float segmentEast = playTime + segmentDuration/2;
        if (segmentWest < 0) {
            segmentEast -= segmentWest;
            segmentWest = 0;
        }
        else if (segmentEast > duration) {
            segmentWest += (segmentEast - duration);
            segmentEast = duration;
        }

        int segmentWestX = timeX(this.getWidth(), segmentWest/duration);
        //int segmentEastX = timeX(this.getWidth(), segmentEast/duration);
        g.setColor(Color.GRAY);
        this.drawMarkerAt(g, 0);
        this.drawMarkerAt(g, 1);
        g.fillRect(segmentWestX, this.middle/2, (int)(this.getWidth()/zoomScale), 3);

        // play head
        g.setColor(Color.BLUE);
        g.fillRect(timeX(this.getWidth(), playRatio)-1, 1, 4, this.middle/2+6);

        // bottom frame - play time value
        Rectangle timeRect = new Rectangle(this.getWidth()/2 - timeWidth/2, this.middle, timeWidth, this.getHeight()-this.middle);
        g.setColor(Color.LIGHT_GRAY);
        this.drawRect(g, timeRect);
        g.setColor(Color.BLACK);
        this.drawCenteredString(g, Utils.getTimelineFormatted(playTime, true), timeRect, font);

        // markers
        Resource resource = this.playerState.getResource();
        if (resource != null) {
            g.setColor(Color.BLACK);
            for (Marker marker : resource.markers) {
                float markerAt = marker.position * 1000 / duration;
                this.drawMarkerAt(g, markerAt);
            }
        }
    }

    // compute X coordinate of the timeline
    private int timeX(int width, double ratio) {
        final int margin = 3;
        return (int)((width - 2*margin) * (float)ratio) + margin;
    }

    private void drawMarkerAt(Graphics g, double ratio) {
        int marker = timeX(getWidth(), ratio);
        g.drawLine(marker, this.middle/2+1, marker, this.middle-1);
    }

    private void drawRect(Graphics g, Rectangle rect) {
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     * https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.MediaLoaded) {
            playerState.getResource().registerChangeListener(this);
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.PlayTime) {
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.MediaUnloaded) {
            playerState.getResource().unRegisterChangeListener(this);
            repaint();
        }
    }
}
