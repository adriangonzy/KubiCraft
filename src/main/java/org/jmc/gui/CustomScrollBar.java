package org.jmc.gui;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author paul
 */
public class CustomScrollBar extends MetalScrollBarUI {
    private Image imageThumb;
    private Image imageTrack;
    private JButton b;

    CustomScrollBar() {
        this.b = new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
        imageThumb = ImageGenerator.create(32, 32, CustomPalette.BLUE_GRAY);
        imageTrack = ImageGenerator.create(32, 32, CustomPalette.DARK_BLUE.darker());
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.RED);
        g.drawImage(imageThumb, r.x, r.y, r.width, r.height, null);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.drawImage(imageTrack, r.x, r.y, r.width, r.height, null);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return b;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return b;
    }

    private static class ImageGenerator {
        static public Image create(int w, int h, Color c) {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(c);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
            return bi;
        }
    }
}
