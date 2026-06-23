package com.fintrackpro.ui.components;

import com.fintrackpro.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel background dengan gradient biru-putih untuk halaman login.
 * Menggunakan gradient halus dari biru muda ke putih.
 */
public class GradientPanel extends JPanel {

    private Color gradientStart;
    private Color gradientEnd;

    public GradientPanel() {
        this(UIConstants.BG_GRADIENT_START, UIConstants.BG_GRADIENT_END);
    }

    public GradientPanel(Color start, Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Gradient dari atas ke bawah
        GradientPaint gradient = new GradientPaint(
            0, 0, gradientStart,
            w, h, gradientEnd
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // Subtle radial overlay untuk efek kedalaman
        RadialGradientPaint radial = new RadialGradientPaint(
            new Point(w / 2, h / 3),
            (float) Math.max(w, h) * 0.6f,
            new float[]{0f, 1f},
            new Color[]{
                new Color(255, 255, 255, 30),
                new Color(255, 255, 255, 0)
            }
        );
        g2.setPaint(radial);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
        super.paintComponent(g);
    }
}
