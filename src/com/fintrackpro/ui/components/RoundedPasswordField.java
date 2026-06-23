package com.fintrackpro.ui.components;

import com.fintrackpro.util.UIConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom password field dengan desain modern:
 * - Rounded corners
 * - Icon gembok di sebelah kiri
 * - Toggle visibility password
 * - Placeholder text
 * - Focus animation
 */
public class RoundedPasswordField extends JPasswordField {

    private String placeholder;
    private Icon prefixIcon;
    private Color borderColor;
    private Color focusBorderColor;
    private boolean isFocused = false;
    private boolean isPasswordVisible = false;
    private int borderRadius;
    private float animationProgress = 0f;
    private Timer animationTimer;
    private Rectangle toggleBounds;

    public RoundedPasswordField(String placeholder, Icon prefixIcon) {
        this.placeholder = placeholder;
        this.prefixIcon = prefixIcon;
        this.borderColor = UIConstants.BORDER_DEFAULT;
        this.focusBorderColor = UIConstants.BORDER_FOCUS;
        this.borderRadius = UIConstants.INPUT_BORDER_RADIUS;

        setOpaque(false);
        setBorder(new EmptyBorder(8, prefixIcon != null ? 44 : 16, 8, 44));
        setFont(UIConstants.getBodyFont());
        setForeground(UIConstants.TEXT_PRIMARY);
        setCaretColor(UIConstants.PRIMARY_BLUE);
        setEchoChar('●');
        setPreferredSize(new Dimension(0, UIConstants.INPUT_HEIGHT));

        // Focus listener untuk animasi
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

        // Click listener untuk toggle visibility
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (toggleBounds != null && toggleBounds.contains(e.getPoint())) {
                    isPasswordVisible = !isPasswordVisible;
                    setEchoChar(isPasswordVisible ? (char) 0 : '●');
                    repaint();
                }
            }
        });

        // Cursor change on hover over toggle button
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (toggleBounds != null && toggleBounds.contains(e.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
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

        // Border dengan animasi
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

        // Toggle visibility icon (eye)
        drawToggleIcon(g2, w, h);

        g2.dispose();
        super.paintComponent(g);

        // Placeholder
        if (getPassword().length == 0 && !isFocused && placeholder != null) {
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

    private void drawToggleIcon(Graphics2D g2, int w, int h) {
        int iconSize = 18;
        int iconX = w - 34;
        int iconY = (h - iconSize) / 2;
        toggleBounds = new Rectangle(iconX - 6, iconY - 6, iconSize + 12, iconSize + 12);

        g2.setColor(UIConstants.TEXT_SECONDARY);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Gambar icon mata
        int centerX = iconX + iconSize / 2;
        int centerY = iconY + iconSize / 2;

        // Bentuk mata (ellipse)
        g2.drawOval(centerX - 8, centerY - 5, 16, 10);
        // Pupil
        g2.fillOval(centerX - 3, centerY - 3, 6, 6);

        if (!isPasswordVisible) {
            // Garis diagonal (mata tertutup)
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(centerX - 9, centerY + 7, centerX + 9, centerY - 7);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Border sudah digambar di paintComponent
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
