package com.fintrackpro.ui.components;

import com.fintrackpro.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom button dengan desain modern:
 * - Rounded corners
 * - Hover & press animation
 * - Gradient background opsional
 * - Shadow effect
 * - Smooth color transitions
 */
public class RoundedButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private int borderRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private float hoverProgress = 0f;
    private Timer hoverTimer;
    private boolean hasShadow;

    /**
     * Membuat tombol utama (primary) dengan warna biru.
     */
    public RoundedButton(String text) {
        this(text, UIConstants.PRIMARY_BLUE, UIConstants.PRIMARY_BLUE_HOVER,
             UIConstants.PRIMARY_BLUE_PRESSED, UIConstants.TEXT_WHITE, true);
    }

    /**
     * Membuat tombol dengan kustomisasi penuh.
     */
    public RoundedButton(String text, Color normalColor, Color hoverColor,
                         Color pressedColor, Color textColor, boolean hasShadow) {
        super(text);
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
        this.textColor = textColor;
        this.borderRadius = UIConstants.BUTTON_BORDER_RADIUS;
        this.hasShadow = hasShadow;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(UIConstants.getButtonFont());
        setForeground(textColor);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(0, UIConstants.BUTTON_HEIGHT));

        // Mouse listeners untuk hover & press
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                startHoverAnimation(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                startHoverAnimation(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private void startHoverAnimation(boolean hovering) {
        if (hoverTimer != null && hoverTimer.isRunning()) {
            hoverTimer.stop();
        }

        hoverTimer = new Timer(16, e -> {
            if (hovering) {
                hoverProgress += 0.12f;
                if (hoverProgress >= 1f) {
                    hoverProgress = 1f;
                    ((Timer) e.getSource()).stop();
                }
            } else {
                hoverProgress -= 0.12f;
                if (hoverProgress <= 0f) {
                    hoverProgress = 0f;
                    ((Timer) e.getSource()).stop();
                }
            }
            repaint();
        });
        hoverTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        int shadowOffset = hasShadow ? 3 : 0;

        // Shadow
        if (hasShadow) {
            for (int i = 0; i < 4; i++) {
                g2.setColor(new Color(24, 100, 205, 15 - (i * 3)));
                g2.fill(new RoundRectangle2D.Float(i, shadowOffset + i, w - (i * 2), h - shadowOffset - (i * 2), borderRadius, borderRadius));
            }
        }

        // Background color
        Color bgColor;
        if (isPressed) {
            bgColor = pressedColor;
        } else {
            bgColor = interpolateColor(normalColor, hoverColor, hoverProgress);
        }

        // Gambar rounded rectangle
        RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, w - 1, h - shadowOffset - 1, borderRadius, borderRadius);
        g2.setColor(bgColor);
        g2.fill(roundRect);

        // Scale effect saat pressed
        float scale = isPressed ? 0.98f : 1.0f;

        // Teks
        g2.setFont(getFont());
        g2.setColor(textColor);
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textX = (w - textWidth) / 2;
        int textY = (h - shadowOffset - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Tidak perlu border default
    }

    private Color interpolateColor(Color c1, Color c2, float fraction) {
        int r = (int) (c1.getRed() + fraction * (c2.getRed() - c1.getRed()));
        int gr = (int) (c1.getGreen() + fraction * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + fraction * (c2.getBlue() - c1.getBlue()));
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, gr)),
            Math.max(0, Math.min(255, b))
        );
    }
}
