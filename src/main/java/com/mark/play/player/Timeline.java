package com.mark.play.player;

import com.mark.play.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Timeline extends JPanel implements IMyPlayerStateChangeListener, IResourceChangeListener {
    final int height = 50;
    final int middle = 20;

    Font font = new Font("helvetica", Font.PLAIN, 16);
    boolean graphicsSet = false;
    int timeWidth;

    private MyPlayerState playerState;
    private Resource resource;

    public Timeline(MyPlayerState playerState, Resource resource) {
        super(true);
        this.playerState = playerState;
        this.playerState.registerStateChangeListener(this);

        this.resource = resource;
        this.resource.registerChangeListener(this);

        setBorder(new LineBorder(Color.LIGHT_GRAY));
        setPreferredSize(new Dimension(0, this.height));
    }

    private void onFirstGraphics(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        //Font font = new Font("helvetica", Font.PLAIN, 16);
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

        // top frame
        g2.setStroke(new BasicStroke(1));
        g.setColor(Color.LIGHT_GRAY);
        //g.drawRect(0, 0, getWidth(), this.middle);
        g.drawRect(0, 0, getWidth(), this.middle);

        // play head
        g.setColor(Color.BLUE);
        g.fillRect(timeX(this.getWidth(), playRatio)-2, 1, 4, this.middle/2+1);

        // bottom frame - play time value
        Rectangle timeRect = new Rectangle(this.getWidth()/2 - timeWidth/2, this.middle, timeWidth, this.getHeight());
        g.setColor(Color.BLUE);
        //this.drawRect(g, timeRect);
        this.drawCenteredString(g, Utils.getTimelineFormatted(playTime, true), timeRect, font);

        // markers
        g.setColor(Color.BLACK);
        this.drawMarkerAt(g, 0);
        this.drawMarkerAt(g, 1);
        for (Marker marker : resource.markers) {
            float markerAt = marker.position * 1000 / duration;
            this.drawMarkerAt(g, markerAt);
        }
    }

    private int timeX(int width, double ratio) {
        final int margin = 3;
        return (int)((width - 2*margin) * (float)ratio) + margin;
    }

    private void drawMarkerAt(Graphics g, double ratio) {
        int marker = timeX(getWidth(), ratio);
        g.drawLine(marker, this.middle/2, marker, this.middle-2);
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
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent()/2;
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    @Override
    public void onPlayerStateChange(MyPlayerState playerState, EPlayerStateChangeType stateChangeType) {
        if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.PlayTime) {
            repaint();
        }
    }
}
