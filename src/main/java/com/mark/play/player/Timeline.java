package com.mark.play.player;

import com.mark.play.Utils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Timeline extends JPanel {
    static {
    }

    private float playHeadTime;

    public Timeline() {
        super(true);
        setBorder(new LineBorder(Color.LIGHT_GRAY));
        setPreferredSize(new Dimension(0, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D) {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        g.fillRect(200, 0, 230, 30);
        g.drawRect(getWidth()/4, 0, getWidth()/2, getHeight());
        g.setFont(new Font("helvetica", Font.PLAIN, 18));
        g.drawString(Utils.getTimelineFormatted(playHeadTime), 0, 20);
    }

    public void onTimelineChange(float newTime) {
        //System.out.printf("timeline changed: %s\n", Utils.getTimelineFormatted(newTime));
        playHeadTime = newTime;
        repaint();
    }
}
