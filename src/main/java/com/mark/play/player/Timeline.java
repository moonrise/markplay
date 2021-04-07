package com.mark.play.player;

import com.mark.Log;
import com.mark.Prefs;
import com.mark.Utils;
import com.mark.main.IMain;
import com.mark.resource.EResourceChangeType;
import com.mark.resource.IResourceChangeListener;
import com.mark.resource.Marker;
import com.mark.resource.Resource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class Timeline extends JPanel implements IMyPlayerStateChangeListener, IResourceChangeListener {
    final IMain main;
    final int middle = 30;      // mid point between timeline and the bottom bar
    final int height = 50;      // bottom bar height

    private Font font = new Font("helvetica", Font.PLAIN, 12);
    private boolean graphicsSet = false;
    private int timeWidth;

    private MyPlayerState playerState;

    public Timeline(MyPlayerState playerState, IMain main) {
        super(true);
        this.playerState = playerState;
        this.playerState.registerPlayerStateChangeListener(this);
        this.main = main;

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

        Resource resource = playerState.getResource();
        if (resource != null) {
            g.setColor(Color.BLACK);
            String timeText = String.format("%s [%d]", Utils.getTimelineFormatted(playTime, true), resource.getMarkerSpanIndex(playTime));
            this.drawCenteredString(g, timeText, timeRect, font);

            // markers
            ArrayList<Marker> markers = resource.markers;
            for (int i=0; i<markers.size(); i++) {
                Marker marker = markers.get(i);
                float markerAt = marker.time / (float)duration;

                g.setColor(Color.BLACK);
                this.drawMarkerAt(g, markerAt);

                if (marker.select) {
                    float markerNext = i < markers.size()-1 ? markers.get(i+1).time / (float)duration : duration;
                    this.drawMarkerSelectAt(g, markerAt, markerNext);
                }
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

    private void drawMarkerSelectAt(Graphics g, double ratio1, double ratio2) {
        g.setColor(Prefs.isPlaySelectedMarkers() ? Color.ORANGE : Color.LIGHT_GRAY);
        int marker1 = timeX(getWidth(), ratio1);
        int marker2 = timeX(getWidth(), ratio2);
        g.fillRect(marker1+1, this.middle-8, marker2-marker1, 8);
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
            //Log.log("time line: media loaded event handler");
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.MediaParsed) {
            //Log.log("time line: media parsed event handler");
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.PlayTime) {
            //Log.log("time line: play time change event handler");
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.PlaySelected) {
            //Log.log("time line: play selected change event handler");
            repaint();
        }
        else if (stateChangeType == EPlayerStateChangeType.MediaUnloaded) {
            //Log.log("time line: media unloaded event handler");
            playerState.getResource().unRegisterChangeListener(this);
            repaint();
        }
    }
}
