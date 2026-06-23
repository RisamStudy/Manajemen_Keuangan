package com.fintrackpro;

import com.fintrackpro.ui.LoginForm;
import javax.swing.*;

/**
 * Main entry point aplikasi FinTrack Pro.
 * Sistem Manajemen Pengeluaran Keuangan.
 * 
 * @author FinTrack Pro Team
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        // Set Look and Feel ke system native untuk tampilan lebih baik
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback ke default L&F
        }

        // Kustomisasi UIManager defaults untuk konsistensi
        UIManager.put("OptionPane.messageFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        UIManager.put("TextField.caretForeground", new java.awt.Color(24, 100, 205));

        // Jalankan di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
