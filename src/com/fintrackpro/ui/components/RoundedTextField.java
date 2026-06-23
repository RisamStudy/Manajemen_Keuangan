package com.fintrackpro.ui.components;

import com.fintrackpro.util.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom text field dengan desain modern:
 * - Rounded corners
 * - Icon di sebelah kiri
 * - Placeholder text
 * - Focus animation
 * - Smooth border color transition
 */
public class RoundedTextField extends JTextField {

    private String placeholder;
    private Icon prefixIcon;
    private Color borderColor;
    private Color focusBorderColor;
    private boolean isFocused = false;
    private int borderRadius;
    private float animationProgress = 0f;
    private Timer animationTimer;

    public RoundedTextField(String placeholder, Icon prefixIcon) {
        this.placeholder = placeholder;
        this.prefixIcon = prefixIcon;
        this.borderColor = UIConstants.BORDER_DEFAULT;
        this.focusBorderColor = UIConstants.BORDER_FOCUS;
        this.borderRadius = UIConstants.INPUT_BORDER_RADIUS;

        setOpaque(false);
        setBorder(new EmptyBorder(8, prefixIcon != null ? 44 : 16, 8, 16));
        setFont(UIConstants.getBodyFont());
        setForeground(UIConstants.TEXT_PRIMARY);
        setCaretColor(UIConstants.PRIMARY_BLUE);
        setPreferredSize(new Dimension(0, UIConstants.INPUT_HEIGHT));

        // Focus listener untuk animasi border
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                startAnimation(true);
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                startAnimation(false);
                repaint();
            }
        });
    }

    private void startAnimation(boolean focusing) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> {
            if (focusing) {
                animationProgress += 0.15f;
                if (animationProgress >= 1f) {
                    animationProgress = 1f;
                    ((Timer) e.getSource()).stop();
                }
            } else {
                animationProgress -= 0.15f;
                if (animationProgress <= 0f) {
                    animationProgress = 0f;
                    ((Timer) e.getSource()).stop();
                }
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(UIConstants.BG_WHITE);
        g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, borderRadius, borderRadius));

        // Border dengan animasi warna
        Color currentBorder = interpolateColor(borderColor, focusBorderColor, animationProgress);
        float borderThickness = 1.0f + (animationProgress * 0.5f);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.setColor(currentBorder);
        g2.draw(new RoundRectangle2D.Float(1, 1, w - 3, h - 3, borderRadius, borderRadius));

        // Prefix icon
        if (prefixIcon != null) {
            int iconY = (h - prefixIcon.getIconHeight()) / 2;
            prefixIcon.paintIcon(this, g2, 14, iconY);
        }

        g2.dispose();
        super.paintComponent(g);

        // Placeholder text
        if (getText().isEmpty() && !isFocused && placeholder != null) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g3.setColor(UIConstants.TEXT_PLACEHOLDER);
            g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            int textX = prefixIcon != null ? 44 : 16;
            g3.drawString(placeholder, textX, textY);
            g3.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Border sudah digambar di paintComponent
    }

    private Color interpolateColor(Color c1, Color c2, float fraction) {
        int r = (int) (c1.getRed() + fraction * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + fraction * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + fraction * (c2.getBlue() - c1.getBlue()));
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
