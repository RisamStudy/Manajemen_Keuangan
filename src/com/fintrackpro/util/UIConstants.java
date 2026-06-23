package com.fintrackpro.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * Konstanta UI untuk menjaga konsistensi desain di seluruh aplikasi.
 * Warna dan font mengikuti design system FinTrack Pro.
 */
public class UIConstants {

    // === WARNA UTAMA ===
    public static final Color PRIMARY_BLUE = new Color(24, 100, 205);        // #1864CD - Biru utama
    public static final Color PRIMARY_BLUE_HOVER = new Color(20, 85, 180);   // Hover state
    public static final Color PRIMARY_BLUE_PRESSED = new Color(16, 70, 155); // Pressed state
    public static final Color PRIMARY_BLUE_LIGHT = new Color(232, 242, 255); // Background ringan

    // === WARNA LATAR ===
    public static final Color BG_GRADIENT_START = new Color(235, 240, 255);  // Gradient awal
    public static final Color BG_GRADIENT_END = new Color(245, 248, 255);    // Gradient akhir
    public static final Color BG_WHITE = new Color(255, 255, 255);           // Putih
    public static final Color BG_CARD = new Color(255, 255, 255);            // Background kartu

    // === WARNA TEKS ===
    public static final Color TEXT_PRIMARY = new Color(30, 30, 46);          // Teks utama
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);     // Teks sekunder
    public static final Color TEXT_PLACEHOLDER = new Color(156, 163, 175);   // Placeholder
    public static final Color TEXT_LINK = new Color(24, 100, 205);           // Link
    public static final Color TEXT_WHITE = new Color(255, 255, 255);         // Teks putih

    // === WARNA BORDER ===
    public static final Color BORDER_DEFAULT = new Color(209, 213, 219);     // Border default
    public static final Color BORDER_FOCUS = new Color(24, 100, 205);        // Border saat focus
    public static final Color BORDER_CARD = new Color(229, 231, 235);        // Border kartu

    // === WARNA STATUS ===
    public static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    public static final Color ERROR_RED = new Color(239, 68, 68);
    public static final Color WARNING_YELLOW = new Color(245, 158, 11);

    // === FONT SIZES ===
    public static final int FONT_SIZE_TITLE = 24;
    public static final int FONT_SIZE_SUBTITLE = 14;
    public static final int FONT_SIZE_BODY = 14;
    public static final int FONT_SIZE_LABEL = 13;
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_BUTTON = 15;
    public static final int FONT_SIZE_LOGO = 20;

    // === FONT FAMILY ===
    public static final String FONT_FAMILY = "Segoe UI";  // Font default Windows modern

    // === DIMENSI ===
    public static final int INPUT_HEIGHT = 48;
    public static final int BUTTON_HEIGHT = 50;
    public static final int CARD_PADDING = 40;
    public static final int CARD_WIDTH = 420;
    public static final int INPUT_BORDER_RADIUS = 10;
    public static final int BUTTON_BORDER_RADIUS = 10;
    public static final int CARD_BORDER_RADIUS = 16;

    // === SHADOW ===
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);
    public static final int SHADOW_SIZE = 20;

    /**
     * Mendapatkan font dengan family Segoe UI.
     * Fallback ke SansSerif jika tidak tersedia.
     */
    public static Font getFont(int style, int size) {
        return new Font(FONT_FAMILY, style, size);
    }

    public static Font getTitleFont() {
        return getFont(Font.BOLD, FONT_SIZE_TITLE);
    }

    public static Font getSubtitleFont() {
        return getFont(Font.PLAIN, FONT_SIZE_SUBTITLE);
    }

    public static Font getBodyFont() {
        return getFont(Font.PLAIN, FONT_SIZE_BODY);
    }

    public static Font getLabelFont() {
        return getFont(Font.BOLD, FONT_SIZE_LABEL);
    }

    public static Font getSmallFont() {
        return getFont(Font.PLAIN, FONT_SIZE_SMALL);
    }

    public static Font getButtonFont() {
        return getFont(Font.BOLD, FONT_SIZE_BUTTON);
    }

    public static Font getLogoFont() {
        return getFont(Font.BOLD, FONT_SIZE_LOGO);
    }
}
