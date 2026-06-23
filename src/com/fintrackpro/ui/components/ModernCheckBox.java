package com.fintrackpro.ui.components;

import com.fintrackpro.util.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom checkbox dengan desain modern:
 * - Rounded square box
 * - Smooth check animation
 * - Custom colors
 */
public class ModernCheckBox extends JCheckBox {

    private float checkProgress = 0f;
    private Timer animationTimer;
    private int boxSize = 18;

    public ModernCheckBox(String text) {
        super(text);
        setOpaque(false);
        setFont(UIConstants.getSmallFont());
        setForeground(UIConstants.TEXT_SECONDARY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setIconTextGap(8);

        // Override icon
        setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                // Digambar di paintComponent
            }

            @Override
            public int getIconWidth() {
                return boxSize;
            }

            @Override
            public int getIconHeight() {
                return boxSize;
            }
        });

        addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            startAnimation(selected);
        });
    }

    private void startAnimation(boolean selecting) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> {
            if (selecting) {
                checkProgress += 0.2f;
                if (checkProgress >= 1f) {
                    checkProgress = 1f;
                    ((Timer) e.getSource()).stop();
                }
            } else {
                checkProgress -= 0.2f;
                if (checkProgress <= 0f) {
                    checkProgress = 0f;
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

        int y = (getHeight() - boxSize) / 2;

        // Background kotak
        if (checkProgress > 0) {
            g2.setColor(UIConstants.PRIMARY_BLUE);
            int alpha = (int) (255 * checkProgress);
            g2.setColor(new Color(
                UIConstants.PRIMARY_BLUE.getRed(),
                UIConstants.PRIMARY_BLUE.getGreen(),
                UIConstants.PRIMARY_BLUE.getBlue(),
                alpha
            ));
            g2.fill(new RoundRectangle2D.Float(0, y, boxSize, boxSize, 4, 4));
        }

        // Border kotak
        g2.setColor(checkProgress > 0 ? UIConstants.PRIMARY_BLUE : UIConstants.BORDER_DEFAULT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(0, y, boxSize - 1, boxSize - 1, 4, 4));

        // Check mark
        if (checkProgress > 0) {
            g2.setColor(UIConstants.TEXT_WHITE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = boxSize / 2;
            int cy = y + boxSize / 2;

            // Animasi checkmark
            int checkLen = (int) (checkProgress * 5);
            // Garis pendek (kiri bawah)
            if (checkLen > 0) {
                int x1 = cx - 4;
                int y1 = cy;
                int x2 = cx - 1;
                int y2 = cy + 3;
                g2.drawLine(x1, y1, x2, y2);
            }
            // Garis panjang (kanan atas)
            if (checkLen > 2) {
                int x1 = cx - 1;
                int y1 = cy + 3;
                int x2 = cx + 5;
                int y2 = cy - 3;
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Teks
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textX = boxSize + getIconTextGap();
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
