package de.ja.view.table.renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class VerticalLabel extends JLabel {

    //private final String text;

    /*public VerticalLabel(String t) {
        this.text = t;
        setText("  " + t);
    }*/

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2D = (Graphics2D) g;
        int w2 = getWidth();
        int h2 = getHeight() / 2;
        g2D.rotate(- Math.PI / 2);
        super.paintComponent(g);


        /*super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        final AffineTransform transform = g2.getTransform();

        g2.rotate(Math.toRadians(-90));
        g2.drawString(text, -80, g2.getFontMetrics().getAscent());
        g2.setTransform(transform);*/
    }
}
