package com.fintrackpro.ui;

import com.fintrackpro.ui.components.*;
import com.fintrackpro.util.DatabaseConnection;
import com.fintrackpro.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Form Login FinTrack Pro
 * 
 * Tampilan login modern dengan:
 * - Logo dan branding FinTrack Pro
 * - Form email & password dengan custom components
 * - Checkbox "Ingat perangkat ini"
 * - Link "Lupa Kata Sandi?" dan "Daftar sekarang"
 * - Gradient background
 * - Animasi dan transisi halus
 */
public class LoginForm extends JFrame {

    private RoundedTextField emailField;
    private RoundedPasswordField passwordField;
    private ModernCheckBox rememberCheckBox;
    private RoundedButton loginButton;
    private JLabel statusLabel;
    
    // User Session
    private int loggedInUserId = -1;
    private String loggedInUserName = "";

    public LoginForm() {
        initializeFrame();
        buildUI();
        setLocationRelativeTo(null);
    }

    /**
     * Inisialisasi properti frame utama.
     */
    private void initializeFrame() {
        setTitle("FinTrack Pro - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setResizable(true);

        // Set icon (opsional - akan menggunakan default jika tidak ada)
        try {
            Image icon = createAppIcon();
            setIconImage(icon);
        } catch (Exception e) {
            // Gunakan icon default
        }
    }

    /**
     * Membuat icon aplikasi secara programatik.
     */
    private Image createAppIcon() {
        int size = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background rounded rectangle
        g2.setColor(UIConstants.PRIMARY_BLUE);
        g2.fill(new RoundRectangle2D.Float(0, 0, size, size, 14, 14));

        // Huruf "F" putih
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 38));
        FontMetrics fm = g2.getFontMetrics();
        String text = "F";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);

        g2.dispose();
        return img;
    }

    /**
     * Membangun seluruh UI login.
     */
    private void buildUI() {
        // Panel utama dengan gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // Container vertikal untuk logo + kartu
        JPanel containerPanel = new JPanel();
        containerPanel.setOpaque(false);
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        // === LOGO SECTION ===
        JPanel logoSection = createLogoSection();
        containerPanel.add(logoSection);
        containerPanel.add(Box.createVerticalStrut(30));

        // === CARD LOGIN ===
        JPanel cardPanel = createCardPanel();
        containerPanel.add(cardPanel);
        containerPanel.add(Box.createVerticalStrut(24));

        // === FOOTER (Daftar sekarang) ===
        JPanel footerSection = createFooterSection();
        containerPanel.add(footerSection);
        containerPanel.add(Box.createVerticalStrut(16));

        // === SECURITY BADGE ===
        JPanel securityBadge = createSecurityBadge();
        containerPanel.add(securityBadge);

        // Tambahkan ke main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(containerPanel, gbc);
    }

    /**
     * Membuat section logo FinTrack Pro di atas.
     */
    private JPanel createLogoSection() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Logo icon + teks
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        logoRow.setOpaque(false);

        // Icon kotak biru dengan "F"
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 28;
                g2.setColor(UIConstants.PRIMARY_BLUE);
                g2.fill(new RoundRectangle2D.Float(0, 2, size, size, 6, 6));

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String t = "F";
                int tx = (size - fm.stringWidth(t)) / 2;
                int ty = 2 + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(t, tx, ty);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(28, 32);
            }
        };
        logoRow.add(iconLabel);

        JLabel logoText = new JLabel("FinTrack Pro");
        logoText.setFont(UIConstants.getLogoFont());
        logoText.setForeground(UIConstants.TEXT_PRIMARY);
        logoRow.add(logoText);

        logoPanel.add(logoRow);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sistem Manajemen Kekayaan Presisi");
        subtitleLabel.setFont(UIConstants.getSmallFont());
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(subtitleLabel);

        return logoPanel;
    }

    /**
     * Membuat card/panel login dengan shadow dan rounded corners.
     */
    private JPanel createCardPanel() {
        // Outer panel untuk shadow
        JPanel shadowWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int radius = UIConstants.CARD_BORDER_RADIUS;
                int shadowSize = UIConstants.SHADOW_SIZE;

                // Multi-layer shadow untuk efek realistis
                for (int i = shadowSize; i > 0; i--) {
                    float alpha = (float) (0.6 * Math.pow((double) (shadowSize - i) / shadowSize, 2));
                    g2.setColor(new Color(0, 0, 0, (int) (alpha * 20)));
                    g2.fill(new RoundRectangle2D.Float(
                        shadowSize - i, shadowSize - i + 2,
                        w - 2 * (shadowSize - i), h - 2 * (shadowSize - i),
                        radius + i, radius + i
                    ));
                }

                // Card background putih
                g2.setColor(UIConstants.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(shadowSize, shadowSize, w - 2 * shadowSize, h - 2 * shadowSize, radius, radius));

                // Card border halus
                g2.setColor(UIConstants.BORDER_CARD);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(shadowSize, shadowSize, w - 2 * shadowSize - 1, h - 2 * shadowSize - 1, radius, radius));

                g2.dispose();
            }
        };
        shadowWrapper.setOpaque(false);
        shadowWrapper.setLayout(new BorderLayout());
        shadowWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inner content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        int pad = UIConstants.CARD_PADDING;
        int shadowPad = UIConstants.SHADOW_SIZE;
        contentPanel.setBorder(new EmptyBorder(pad + shadowPad, pad + shadowPad, pad + shadowPad, pad + shadowPad));

        // === HEADING ===
        JLabel headingLabel = new JLabel("Selamat Datang Kembali");
        headingLabel.setFont(UIConstants.getTitleFont());
        headingLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(headingLabel);

        contentPanel.add(Box.createVerticalStrut(6));

        JLabel subHeadingLabel = new JLabel("Silakan masukkan detail akun Anda.");
        subHeadingLabel.setFont(UIConstants.getSubtitleFont());
        subHeadingLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subHeadingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subHeadingLabel);

        contentPanel.add(Box.createVerticalStrut(28));

        // === EMAIL FIELD ===
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UIConstants.getLabelFont());
        emailLabel.setForeground(UIConstants.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(emailLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        Icon emailIcon = createEmailIcon();
        emailField = new RoundedTextField("nama@perusahaan.com", emailIcon);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        contentPanel.add(emailField);

        contentPanel.add(Box.createVerticalStrut(20));

        // === PASSWORD FIELD ===
        JPanel passwordHeaderPanel = new JPanel(new BorderLayout());
        passwordHeaderPanel.setOpaque(false);
        passwordHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordHeaderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel passwordLabel = new JLabel("Kata Sandi");
        passwordLabel.setFont(UIConstants.getLabelFont());
        passwordLabel.setForeground(UIConstants.TEXT_PRIMARY);
        passwordHeaderPanel.add(passwordLabel, BorderLayout.WEST);

        JLabel forgotPasswordLabel = createClickableLabel("Lupa Kata Sandi?", UIConstants.TEXT_LINK);
        passwordHeaderPanel.add(forgotPasswordLabel, BorderLayout.EAST);

        contentPanel.add(passwordHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(8));

        Icon lockIcon = createLockIcon();
        passwordField = new RoundedPasswordField("Masukkan kata sandi", lockIcon);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.INPUT_HEIGHT));
        contentPanel.add(passwordField);

        contentPanel.add(Box.createVerticalStrut(16));

        // === REMEMBER ME CHECKBOX ===
        rememberCheckBox = new ModernCheckBox("Ingat perangkat ini");
        rememberCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(rememberCheckBox);

        contentPanel.add(Box.createVerticalStrut(24));

        // === STATUS LABEL (untuk error message) ===
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIConstants.getSmallFont());
        statusLabel.setForeground(UIConstants.ERROR_RED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);

        contentPanel.add(Box.createVerticalStrut(4));

        // === LOGIN BUTTON ===
        loginButton = new RoundedButton("Masuk");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));
        loginButton.addActionListener(e -> performLogin());
        contentPanel.add(loginButton);

        shadowWrapper.add(contentPanel, BorderLayout.CENTER);

        // Set ukuran kartu
        int cardWidth = UIConstants.CARD_WIDTH + (2 * UIConstants.SHADOW_SIZE);
        shadowWrapper.setPreferredSize(new Dimension(cardWidth, 480));
        shadowWrapper.setMaximumSize(new Dimension(cardWidth, 480));

        // Setup Enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        return shadowWrapper;
    }

    /**
     * Membuat section footer "Belum memiliki akun? Daftar sekarang".
     */
    private JPanel createFooterSection() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noAccountLabel = new JLabel("Belum memiliki akun?");
        noAccountLabel.setFont(UIConstants.getSmallFont());
        noAccountLabel.setForeground(UIConstants.TEXT_SECONDARY);
        footerPanel.add(noAccountLabel);

        JLabel registerLabel = createClickableLabel("Daftar sekarang", UIConstants.TEXT_LINK);
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onRegisterClicked();
            }
        });
        footerPanel.add(registerLabel);

        return footerPanel;
    }

    /**
     * Membuat badge keamanan di bawah.
     */
    private JPanel createSecurityBadge() {
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        badgePanel.setOpaque(false);
        badgePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Shield icon
        JLabel shieldLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Shield shape
                int cx = 8;
                int cy = 8;
                int[] xPoints = {cx, cx - 7, cx - 6, cx, cx + 6, cx + 7};
                int[] yPoints = {cy - 7, cy - 4, cy + 4, cy + 8, cy + 4, cy - 4};
                g2.drawPolygon(xPoints, yPoints, 6);

                // Checkmark di dalam shield
                g2.drawLine(cx - 2, cy + 1, cx, cy + 3);
                g2.drawLine(cx, cy + 3, cx + 3, cy - 2);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(16, 18);
            }
        };
        badgePanel.add(shieldLabel);

        JLabel securityText = new JLabel("Keamanan Enkripsi Tingkat Institusi");
        securityText.setFont(new Font(UIConstants.FONT_FAMILY, Font.BOLD, 11));
        securityText.setForeground(UIConstants.TEXT_SECONDARY);
        badgePanel.add(securityText);

        return badgePanel;
    }

    /**
     * Membuat icon email secara programatik.
     */
    private Icon createEmailIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Envelope body
                g2.drawRoundRect(x, y + 2, 18, 13, 3, 3);

                // Envelope flap (V shape)
                g2.drawLine(x, y + 3, x + 9, y + 10);
                g2.drawLine(x + 18, y + 3, x + 9, y + 10);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 18;
            }
        };
    }

    /**
     * Membuat icon gembok secara programatik.
     */
    private Icon createLockIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Lock body (rounded rect)
                g2.drawRoundRect(x + 2, y + 8, 14, 10, 3, 3);

                // Lock shackle (arc di atas)
                g2.drawArc(x + 4, y + 1, 10, 10, 0, 180);

                // Keyhole dot
                g2.fillOval(x + 8, y + 12, 3, 3);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 20;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        };
    }

    /**
     * Membuat label yang bisa diklik (link style).
     */
    private JLabel createClickableLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIConstants.FONT_FAMILY, Font.BOLD, 12));
        label.setForeground(color);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>" + text + "</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setText(text);
            }
        });

        return label;
    }

    /**
     * Proses login - validasi dan autentikasi ke database.
     */
    private void performLogin() {
        String email = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        // Validasi input
        if (email.isEmpty()) {
            showError("Silakan masukkan alamat email Anda.");
            emailField.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            showError("Format email tidak valid.");
            emailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Silakan masukkan kata sandi Anda.");
            passwordField.requestFocus();
            return;
        }

        // Disable button selama proses login
        loginButton.setEnabled(false);
        loginButton.setText("Memproses...");
        statusLabel.setText(" ");

        // Login ke database (di background thread)
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String errorMessage = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    String query = "SELECT id, nama, email FROM users WHERE email = ? AND password = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, email);
                    stmt.setString(2, password); // Di produksi, gunakan hashing!

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        loggedInUserId = rs.getInt("id");
                        loggedInUserName = rs.getString("nama");
                        return true;
                    } else {
                        errorMessage = "Email atau kata sandi salah.";
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = "Gagal terhubung ke database. Pastikan MySQL aktif.";
                    return false;
                }
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                loginButton.setText("Masuk");

                try {
                    if (get()) {
                        onLoginSuccess();
                    } else {
                        showError(errorMessage);
                    }
                } catch (Exception e) {
                    showError("Terjadi kesalahan yang tidak terduga.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Validasi format email sederhana.
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Menampilkan pesan error dengan animasi fade.
     */
    private void showError(String message) {
        statusLabel.setForeground(UIConstants.ERROR_RED);
        statusLabel.setText("⚠ " + message);

        // Shake animation pada form
        Timer shakeTimer = new Timer(30, null);
        final Point originalLocation = getLocation();
        final int[] shakeCount = {0};

        shakeTimer.addActionListener(e -> {
            if (shakeCount[0] >= 6) {
                setLocation(originalLocation);
                ((Timer) e.getSource()).stop();
            } else {
                int offsetX = (shakeCount[0] % 2 == 0) ? 5 : -5;
                setLocation(originalLocation.x + offsetX, originalLocation.y);
                shakeCount[0]++;
            }
        });
        shakeTimer.start();
    }

    /**
     * Handler ketika login berhasil.
     */
    private void onLoginSuccess() {
        statusLabel.setForeground(UIConstants.SUCCESS_GREEN);
        statusLabel.setText("✓ Login berhasil! Mengalihkan...");

        // Simulasi delay sebelum pindah ke halaman utama
        Timer redirectTimer = new Timer(1000, e -> {
            DashboardForm dashboard = new DashboardForm(loggedInUserId, loggedInUserName);
            dashboard.setVisible(true);
            dispose();
        });
        redirectTimer.setRepeats(false);
        redirectTimer.start();
    }

    /**
     * Handler ketika "Daftar sekarang" diklik.
     */
    private void onRegisterClicked() {
        // TODO: Buka form registrasi
        JOptionPane.showMessageDialog(this,
            "Halaman pendaftaran akan ditampilkan di sini.",
            "Daftar", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback
        }

        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.caretForeground", new Color(24, 100, 205));

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
