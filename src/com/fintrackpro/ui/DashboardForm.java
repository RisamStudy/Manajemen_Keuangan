package com.fintrackpro.ui;

import com.fintrackpro.ui.components.RoundedButton;
import com.fintrackpro.ui.components.RoundedTextField;
import com.fintrackpro.util.DatabaseConnection;
import com.fintrackpro.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Dashboard Utama FinTrack Pro
 * Didesain berdasarkan visual modern premium.
 */
public class DashboardForm extends JFrame {

    private final int userId;
    private final String userName;

    // UI Elements
    private JLabel greetingLabel;
    private StatCard totalKekayaanCard;
    private StatCard pendapatanCard;
    private StatCard pengeluaranCard;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    
    // Components
    private ExpenseTrendChart trendChart;
    private AssetProgressPanel sahamPanel;
    private AssetProgressPanel kasPanel;
    private AssetProgressPanel lainnyaPanel;

    public DashboardForm(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        initializeFrame();
        buildUI();
        setLocationRelativeTo(null);
        
        // Load initial data
        refreshDashboardData();
    }

    private void initializeFrame() {
        setTitle("FinTrack Pro - Ringkasan Dasbor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1080, 750));
        setResizable(true);
        
        // App icon
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            // Ignore
        }
    }

    private Image createAppIcon() {
        int size = 64;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UIConstants.PRIMARY_BLUE);
        g2.fill(new RoundRectangle2D.Float(0, 0, size, size, 14, 14));
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

    private void buildUI() {
        // Root Panel
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(new Color(248, 250, 252)); // Slate 50
        setContentPane(rootPanel);

        // ==========================================
        // 1. SIDEBAR (KIRI)
        // ==========================================
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(15, 23, 42)); // Slate 900
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.gridx = 0;
        sgbc.gridy = 0;
        sgbc.insets = new Insets(30, 24, 40, 24);

        // Logo FinTrack Pro
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        
        JLabel logoTitle = new JLabel("FinTrack Pro");
        logoTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoTitle.setForeground(Color.WHITE);
        logoPanel.add(logoTitle);
        
        JLabel logoSubtitle = new JLabel("Manajemen Kekayaan");
        logoSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoSubtitle.setForeground(new Color(148, 163, 184)); // Slate 400
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(logoSubtitle);
        
        sidebarPanel.add(logoPanel, sgbc);

        // Menu Items
        sgbc.insets = new Insets(0, 0, 0, 0);
        sgbc.gridy++;
        
        JPanel menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        
        menuContainer.add(createSidebarMenuItem("Dasbor", "📊", true));
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(createSidebarMenuItem("Transaksi", "💵", false));
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(createSidebarMenuItem("Anggaran", "🪙", false));
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(createSidebarMenuItem("Laporan", "📁", false));
        
        sidebarPanel.add(menuContainer, sgbc);

        // Button + Transaksi Baru
        sgbc.gridy++;
        sgbc.insets = new Insets(30, 20, 30, 20);
        RoundedButton btnAddTransaction = new RoundedButton("＋ Transaksi Baru");
        btnAddTransaction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddTransaction.addActionListener(e -> openAddTransactionDialog());
        sidebarPanel.add(btnAddTransaction, sgbc);

        // Push settings to bottom
        sgbc.gridy++;
        sgbc.weighty = 1.0;
        sidebarPanel.add(Box.createGlue(), sgbc);

        // Bottom Menu
        sgbc.gridy++;
        sgbc.weighty = 0.0;
        sgbc.insets = new Insets(0, 0, 30, 0);
        JPanel bottomMenuContainer = new JPanel();
        bottomMenuContainer.setOpaque(false);
        bottomMenuContainer.setLayout(new BoxLayout(bottomMenuContainer, BoxLayout.Y_AXIS));
        bottomMenuContainer.add(createSidebarMenuItem("Pengaturan", "⚙️", false));
        bottomMenuContainer.add(Box.createVerticalStrut(8));
        bottomMenuContainer.add(createSidebarMenuItem("Bantuan", "❓", false));
        
        sidebarPanel.add(bottomMenuContainer, sgbc);
        
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // ==========================================
        // 2. MAIN CONTAINER (KANAN)
        // ==========================================
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        rootPanel.add(mainContainer, BorderLayout.CENTER);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240))); // Border bottom
        
        // Search bar (kiri)
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 13));
        searchWrapper.setOpaque(false);
        RoundedTextField searchField = new RoundedTextField("Cari transaksi, aset...", null);
        searchField.setPreferredSize(new Dimension(300, 40));
        searchWrapper.add(searchField);
        topBar.add(searchWrapper, BorderLayout.WEST);
        
        // Profile & Icons (kanan)
        JPanel profileWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 13));
        profileWrapper.setOpaque(false);
        
        // Notification & History icons
        JLabel bellIcon = new JLabel("🔔");
        bellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        bellIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileWrapper.add(bellIcon);
        
        JLabel historyIcon = new JLabel("⏳");
        historyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        historyIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileWrapper.add(historyIcon);
        
        // Divider line
        JSeparator topDivider = new JSeparator(JSeparator.VERTICAL);
        topDivider.setPreferredSize(new Dimension(1, 24));
        topDivider.setForeground(new Color(226, 232, 240));
        profileWrapper.add(topDivider);
        
        // Name & Role Label
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        
        JLabel lblName = new JLabel(userName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(UIConstants.TEXT_PRIMARY);
        
        JLabel lblRole = new JLabel("Anggota Premium");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(UIConstants.TEXT_SECONDARY);
        
        namePanel.add(lblName);
        namePanel.add(lblRole);
        profileWrapper.add(namePanel);

        // Avatar
        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Circle gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(37, 99, 235), getWidth(), getHeight(), new Color(29, 78, 216));
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Initial text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String initial = userName.length() > 0 ? userName.substring(0, 1).toUpperCase() : "U";
                if (userName.contains(" ") && userName.indexOf(" ") + 1 < userName.length()) {
                    initial += userName.substring(userName.indexOf(" ") + 1, userName.indexOf(" ") + 2).toUpperCase();
                }
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initial, x, y);
                
                g2.dispose();
            }
        };
        avatarLabel.setPreferredSize(new Dimension(38, 38));
        profileWrapper.add(avatarLabel);
        
        topBar.add(profileWrapper, BorderLayout.EAST);
        mainContainer.add(topBar, BorderLayout.NORTH);

        // SCROLL PANELS CONTROLLER
        JPanel contentContainer = new JPanel();
        contentContainer.setOpaque(false);
        contentContainer.setLayout(new GridBagLayout());
        
        JScrollPane scrollPane = new JScrollPane(contentContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(248, 250, 252));
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.weightx = 1.0;
        cgbc.gridx = 0;
        cgbc.gridy = 0;
        cgbc.insets = new Insets(24, 30, 0, 30);

        // ==========================================
        // 2A. TITLE SECTION
        // ==========================================
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setOpaque(false);
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        
        JLabel lblMainTitle = new JLabel("Ringkasan Dasbor");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMainTitle.setForeground(UIConstants.TEXT_PRIMARY);
        titleTextPanel.add(lblMainTitle);
        
        greetingLabel = new JLabel("Selamat datang kembali, " + userName + ".");
        greetingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        greetingLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleTextPanel.add(Box.createVerticalStrut(4));
        titleTextPanel.add(greetingLabel);
        titlePanel.add(titleTextPanel, BorderLayout.WEST);

        // Buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonsPanel.setOpaque(false);
        
        JButton btnPeriod = new JButton("📅  30 Hari Terakhir");
        styleSecondaryButton(btnPeriod);
        actionButtonsPanel.add(btnPeriod);
        
        JButton btnExport = new JButton("📤  Ekspor Laporan");
        styleSecondaryButton(btnExport);
        actionButtonsPanel.add(btnExport);
        
        titlePanel.add(actionButtonsPanel, BorderLayout.EAST);
        contentContainer.add(titlePanel, cgbc);

        // ==========================================
        // 2B. STAT CARDS ROW
        // ==========================================
        cgbc.gridy++;
        cgbc.insets = new Insets(20, 30, 0, 30);
        
        JPanel statCardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statCardsPanel.setOpaque(false);
        
        totalKekayaanCard = new StatCard("TOTAL KEKAYAAN BERSIH", "Rp0", "+0.0%", "Target: Rp500Jt", new Color(34, 197, 94), true, 0);
        pendapatanCard = new StatCard("PENDAPATAN BULANAN", "Rp0", "↗", "Pembayaran berikutnya dalam 6 hari", new Color(34, 197, 94), false, 0);
        pengeluaranCard = new StatCard("PENGELUARAN BULANAN", "Rp0", "↗", "12% lebih tinggi dari bulan lalu", new Color(239, 68, 68), false, 0);
        
        statCardsPanel.add(totalKekayaanCard);
        statCardsPanel.add(pendapatanCard);
        statCardsPanel.add(pengeluaranCard);
        
        contentContainer.add(statCardsPanel, cgbc);

        // ==========================================
        // 2C. MID SECTION (CHART & ASSETS)
        // ==========================================
        cgbc.gridy++;
        cgbc.insets = new Insets(20, 30, 0, 30);
        
        JPanel midPanel = new JPanel(new GridBagLayout());
        midPanel.setOpaque(false);
        
        GridBagConstraints mgbc = new GridBagConstraints();
        mgbc.fill = GridBagConstraints.BOTH;
        mgbc.gridy = 0;
        mgbc.weighty = 1.0;

        // Chart Card (Kiri - 65% weight)
        mgbc.gridx = 0;
        mgbc.weightx = 0.65;
        mgbc.insets = new Insets(0, 0, 0, 10);
        trendChart = new ExpenseTrendChart();
        midPanel.add(trendChart, mgbc);

        // Assets Card (Kanan - 35% weight)
        mgbc.gridx = 1;
        mgbc.weightx = 0.35;
        mgbc.insets = new Insets(0, 10, 0, 0);
        
        JPanel assetsCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(229, 231, 235));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        assetsCard.setOpaque(false);
        assetsCard.setLayout(new GridBagLayout());
        assetsCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints agbc = new GridBagConstraints();
        agbc.fill = GridBagConstraints.HORIZONTAL;
        agbc.weightx = 1.0;
        agbc.gridx = 0;
        agbc.gridy = 0;
        
        JLabel lblAssetTitle = new JLabel("Alokasi Aset");
        lblAssetTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAssetTitle.setForeground(UIConstants.TEXT_PRIMARY);
        assetsCard.add(lblAssetTitle, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(20, 0, 0, 0);
        sahamPanel = new AssetProgressPanel("Saham", 0, UIConstants.PRIMARY_BLUE, "📈");
        assetsCard.add(sahamPanel, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(15, 0, 0, 0);
        kasPanel = new AssetProgressPanel("Kas", 0, new Color(16, 185, 129), "💵");
        assetsCard.add(kasPanel, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(15, 0, 0, 0);
        lainnyaPanel = new AssetProgressPanel("Lainnya", 0, new Color(107, 114, 128), "💎");
        assetsCard.add(lainnyaPanel, agbc);
        
        agbc.gridy++;
        agbc.weighty = 1.0;
        assetsCard.add(Box.createVerticalStrut(10), agbc);
        
        agbc.gridy++;
        agbc.weighty = 0.0;
        agbc.insets = new Insets(15, 0, 0, 0);
        JLabel lblAssetFooter = new JLabel("Lihat detail portofolio lengkap →");
        lblAssetFooter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAssetFooter.setForeground(UIConstants.PRIMARY_BLUE);
        lblAssetFooter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        assetsCard.add(lblAssetFooter, agbc);
        
        midPanel.add(assetsCard, mgbc);
        
        contentContainer.add(midPanel, cgbc);

        // ==========================================
        // 2D. BOTTOM SECTION (RECENT TRANSACTIONS)
        // ==========================================
        cgbc.gridy++;
        cgbc.insets = new Insets(20, 30, 30, 30);
        
        JPanel bottomCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(229, 231, 235));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottomCard.setOpaque(false);
        bottomCard.setLayout(new BorderLayout());
        bottomCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        
        JLabel lblRecentTitle = new JLabel("Transaksi Terakhir");
        lblRecentTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRecentTitle.setForeground(UIConstants.TEXT_PRIMARY);
        recentHeader.add(lblRecentTitle, BorderLayout.WEST);
        
        JLabel lblSeeAll = new JLabel("Lihat Semua");
        lblSeeAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSeeAll.setForeground(UIConstants.PRIMARY_BLUE);
        lblSeeAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        recentHeader.add(lblSeeAll, BorderLayout.EAST);
        
        bottomCard.add(recentHeader, BorderLayout.NORTH);

        // Table
        String[] columns = {"ENTITAS", "KATEGORI", "TANGGAL", "STATUS", "JUMLAH"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(50);
        transactionTable.setShowGrid(false);
        transactionTable.setIntercellSpacing(new Dimension(0, 0));
        transactionTable.setBackground(Color.WHITE);
        
        // Style Header Table
        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setBackground(new Color(248, 250, 252));
        tableHeader.setForeground(UIConstants.TEXT_SECONDARY);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        
        // Custom Renderers
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.WHITE);
                c.setForeground(UIConstants.TEXT_PRIMARY);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
                
                if (column == 0) {
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                
                if (column == 4) { // JUMLAH
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    String valText = value.toString();
                    if (valText.startsWith("-")) {
                        setForeground(UIConstants.ERROR_RED);
                    } else if (valText.startsWith("+")) {
                        setForeground(new Color(16, 185, 129));
                    }
                }
                
                return c;
            }
        });

        // Add Table within scroll panel (but disable table's own scrollbar)
        JScrollPane tableScroll = new JScrollPane(transactionTable);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(0, 160));
        bottomCard.add(tableScroll, BorderLayout.CENTER);
        
        contentContainer.add(bottomCard, cgbc);
    }

    private JPanel createSidebarMenuItem(String title, String emoji, boolean isActive) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isActive) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(30, 41, 59)); // Slate 800
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                    
                    g2.setColor(UIConstants.PRIMARY_BLUE); // Left indicator
                    g2.fillRoundRect(8, 8, 4, getHeight() - 16, 2, 2);
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(15, 0));
        panel.setPreferredSize(new Dimension(240, 44));
        panel.setMaximumSize(new Dimension(240, 44));
        panel.setBorder(new EmptyBorder(0, 20, 0, 10));

        JLabel label = new JLabel(emoji + "   " + title);
        label.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
        label.setForeground(isActive ? Color.WHITE : new Color(148, 163, 184)); // Slate 400
        panel.add(label, BorderLayout.CENTER);

        if (!isActive) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    label.setForeground(Color.WHITE);
                    panel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    label.setForeground(new Color(148, 163, 184));
                    panel.repaint();
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (title.equals("Transaksi")) {
                        openAddTransactionDialog();
                    } else {
                        JOptionPane.showMessageDialog(DashboardForm.this, 
                            "Fitur '" + title + "' masih dalam pengembangan.", 
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }

        return panel;
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(UIConstants.TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
    }

    // ==========================================
    // DATABASE LOADER & DATA REFRESH
    // ==========================================
    public void refreshDashboardData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                DashboardData data = new DashboardData();
                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;

                try {
                    conn = DatabaseConnection.getConnection();
                    
                    // 1. Get Monthly Expenditures
                    String sumQuery = "SELECT SUM(jumlah) FROM pengeluaran " +
                            "WHERE user_id = ? AND MONTH(tanggal) = MONTH(CURRENT_DATE()) AND YEAR(tanggal) = YEAR(CURRENT_DATE())";
                    stmt = conn.prepareStatement(sumQuery);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        data.monthlyExpenses = rs.getDouble(1);
                    }
                    rs.close();
                    stmt.close();

                    // 2. Get Weekly Expenditures (Mon to Sun)
                    String weeklyQuery = "SELECT DAYOFWEEK(tanggal) as day, SUM(jumlah) as total " +
                            "FROM pengeluaran " +
                            "WHERE user_id = ? AND YEARWEEK(tanggal, 1) = YEARWEEK(CURDATE(), 1) " +
                            "GROUP BY DAYOFWEEK(tanggal)";
                    stmt = conn.prepareStatement(weeklyQuery);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        int day = rs.getInt("day");
                        double total = rs.getDouble("total");
                        
                        // Map MySQL DAYOFWEEK (1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat) 
                        // into index array (0=Mon, 1=Tue, 2=Wed, 3=Thu, 4=Fri, 5=Sat, 6=Sun)
                        int index = -1;
                        if (day >= 2 && day <= 7) {
                            index = day - 2;
                        } else if (day == 1) {
                            index = 6; // Sunday
                        }
                        
                        if (index >= 0 && index < 7) {
                            data.weeklyChartData[index] = total;
                        }
                    }
                    rs.close();
                    stmt.close();

                    // 3. Get Recent Transactions (limit 3)
                    String trQuery = "SELECT p.judul, k.nama AS kat_nama, k.icon AS kat_icon, k.warna AS kat_warna, p.tanggal, p.jumlah " +
                            "FROM pengeluaran p " +
                            "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                            "WHERE p.user_id = ? " +
                            "ORDER BY p.tanggal DESC, p.id DESC " +
                            "LIMIT 3";
                    stmt = conn.prepareStatement(trQuery);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        TransactionItem item = new TransactionItem();
                        item.judul = rs.getString("judul");
                        item.kategoriNama = rs.getString("kat_nama");
                        item.kategoriIcon = rs.getString("kat_icon");
                        item.tanggal = rs.getDate("tanggal");
                        item.jumlah = rs.getDouble("jumlah");
                        
                        if (item.kategoriIcon == null) item.kategoriIcon = "📦";
                        if (item.kategoriNama == null) item.kategoriNama = "Lainnya";
                        
                        data.recentTransactions.add(item);
                    }
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                }
                
                return data;
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    
                    // Formatters
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
                    DecimalFormat df = new DecimalFormat("###,###,###", symbols);
                    
                    // Update Stat Cards
                    pengeluaranCard.setValue("Rp" + df.format(data.monthlyExpenses));
                    
                    // Update Chart
                    trendChart.setData(data.weeklyChartData);
                    
                    // Update Table
                    tableModel.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                    
                    for (TransactionItem tr : data.recentTransactions) {
                        tableModel.addRow(new Object[]{
                            tr.judul,
                            tr.kategoriIcon + " " + tr.kategoriNama,
                            sdf.format(tr.tanggal),
                            "Selesai",
                            "-Rp" + df.format(tr.jumlah)
                        });
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openAddTransactionDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(this, userId);
        dialog.setVisible(true);
    }

    // Container data untuk background worker
    private static class DashboardData {
        double monthlyExpenses = 0;
        double[] weeklyChartData = new double[7];
        ArrayList<TransactionItem> recentTransactions = new ArrayList<>();
    }

    private static class TransactionItem {
        String judul;
        String kategoriNama;
        String kategoriIcon;
        Date tanggal;
        double jumlah;
    }

    // ==========================================
    // INNER CUSTOM COMPONENT CLASSES
    // ==========================================

    // StatCard
    private static class StatCard extends JPanel {
        private final String title;
        private String value;
        private final String badgeText;
        private final String subtext;
        private final Color badgeColor;
        private final boolean hasProgress;
        private final int progressPercent;
        private JLabel valueLabel;

        public StatCard(String title, String value, String badgeText, String subtext, Color badgeColor, boolean hasProgress, int progressPercent) {
            this.title = title;
            this.value = value;
            this.badgeText = badgeText;
            this.subtext = subtext;
            this.badgeColor = badgeColor;
            this.hasProgress = hasProgress;
            this.progressPercent = progressPercent;

            setOpaque(false);
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(16, 20, 16, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;

            // Row 1: Title and badge
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
            headerPanel.add(titleLabel, BorderLayout.WEST);

            if (badgeText != null) {
                JLabel badgeLabel = new JLabel(badgeText) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(badgeColor.getRed(), badgeColor.getGreen(), badgeColor.getBlue(), 35)); // low alpha background
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                badgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
                badgeLabel.setForeground(badgeColor);
                badgeLabel.setBorder(new EmptyBorder(2, 6, 2, 6));
                headerPanel.add(badgeLabel, BorderLayout.EAST);
            }
            add(headerPanel, gbc);

            // Row 2: Value
            gbc.gridy++;
            gbc.insets = new Insets(8, 0, 8, 0);
            valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
            add(valueLabel, gbc);

            // Row 3: Progress or Subtext
            gbc.gridy++;
            gbc.insets = new Insets(0, 0, 0, 0);
            if (hasProgress) {
                JPanel progressPanel = new JPanel(new BorderLayout(0, 6));
                progressPanel.setOpaque(false);

                JPanel track = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        // Track
                        g2.setColor(new Color(226, 232, 240));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                        // Bar
                        g2.setColor(UIConstants.PRIMARY_BLUE);
                        int barW = (int) (getWidth() * (progressPercent / 100.0));
                        g2.fillRoundRect(0, 0, barW, getHeight(), 4, 4);
                        g2.dispose();
                    }
                };
                track.setPreferredSize(new Dimension(0, 5));
                progressPanel.add(track, BorderLayout.CENTER);

                JLabel subLabel = new JLabel(subtext);
                subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                subLabel.setForeground(UIConstants.TEXT_SECONDARY);
                progressPanel.add(subLabel, BorderLayout.SOUTH);

                add(progressPanel, gbc);
            } else {
                JLabel subLabel = new JLabel(subtext);
                subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                subLabel.setForeground(UIConstants.TEXT_SECONDARY);
                add(subLabel, gbc);
            }
        }

        public void setValue(String val) {
            this.value = val;
            valueLabel.setText(val);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw card background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            
            // Draw card border
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Custom painted ExpenseTrendChart
    private static class ExpenseTrendChart extends JPanel {
        private double[] currentWeek = new double[7]; // Mon-Sun
        private final double[] previousWeek = new double[]{120000, 150000, 80000, 200000, 110000, 90000, 170000}; // Mock previous week values
        private final String[] days = {"SEN", "SEL", "RAB", "KAM", "JUM", "SAB", "MING"};

        public ExpenseTrendChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 310));
        }

        public void setData(double[] currentData) {
            if (currentData != null && currentData.length == 7) {
                this.currentWeek = currentData;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 16, 16);
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

            int padLeft = 65;
            int padRight = 30;
            int padTop = 60;
            int padBottom = 40;

            int chartWidth = w - padLeft - padRight;
            int chartHeight = h - padTop - padBottom;

            // Chart Header
            g2.setColor(UIConstants.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString("Tren Pengeluaran", 24, 32);

            // Legend
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int legX = w - 180;
            g2.setColor(UIConstants.PRIMARY_BLUE);
            g2.fillOval(legX, 22, 10, 10);
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.drawString("Minggu Ini", legX + 16, 31);

            g2.setColor(new Color(226, 232, 240));
            g2.fillOval(legX + 85, 22, 10, 10);
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.drawString("Minggu Lalu", legX + 101, 31);

            // Find Max Value
            double maxVal = 100000; 
            for (double v : currentWeek) {
                if (v > maxVal) maxVal = v;
            }
            for (double v : previousWeek) {
                if (v > maxVal) maxVal = v;
            }
            maxVal *= 1.2; // Add breathing room

            // Grid Lines and Y-Labels
            int gridLines = 4;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int i = 0; i <= gridLines; i++) {
                int gy = padTop + chartHeight - (chartHeight * i / gridLines);
                
                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(padLeft, gy, padLeft + chartWidth, gy);

                // Y Label
                double val = (maxVal * i / gridLines);
                String label;
                if (val >= 1000000) {
                    label = String.format(Locale.US, "%.1fJt", val / 1000000.0);
                } else if (val >= 1000) {
                    label = String.format(Locale.US, "%.0fRb", val / 1000.0);
                } else {
                    label = String.format(Locale.US, "%.0f", val);
                }
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.drawString(label, 18, gy + 4);
            }

            // Draw Columns (Bars)
            int colW = chartWidth / 7;
            int barW = 12;

            for (int i = 0; i < 7; i++) {
                int cx = padLeft + (i * colW) + (colW / 2);

                // Label Day
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString(days[i], cx - 12, padTop + chartHeight + 20);

                // Prev Week Bar
                double pVal = previousWeek[i];
                int pBarH = (int) (chartHeight * (pVal / maxVal));
                int pBarX = cx - barW - 2;
                int pBarY = padTop + chartHeight - pBarH;
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(pBarX, pBarY, barW, pBarH, 4, 4);

                // Curr Week Bar
                double cVal = currentWeek[i];
                int cBarH = (int) (chartHeight * (cVal / maxVal));
                int cBarX = cx + 2;
                int cBarY = padTop + chartHeight - cBarH;
                
                if (cVal > 0) {
                    g2.setColor(UIConstants.PRIMARY_BLUE);
                } else {
                    g2.setColor(new Color(241, 245, 249)); // very light gray if 0
                }
                g2.fillRoundRect(cBarX, cBarY, barW, cBarH, 4, 4);
            }

            g2.dispose();
        }
    }

    // AssetProgressPanel
    private static class AssetProgressPanel extends JPanel {
        private final String name;
        private int percentage;
        private final Color progressColor;
        private final String emoji;
        private JLabel pctLabel;
        private JComponent barComp;

        public AssetProgressPanel(String name, int percentage, Color progressColor, String emoji) {
            this.name = name;
            this.percentage = percentage;
            this.progressColor = progressColor;
            this.emoji = emoji;

            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;

            // Row 1: Icon, Name, Value
            JPanel top = new JPanel(new BorderLayout(8, 0));
            top.setOpaque(false);

            JLabel icon = new JLabel(emoji) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(248, 250, 252)); // Slate 50
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            icon.setPreferredSize(new Dimension(32, 32));
            icon.setHorizontalAlignment(SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            top.add(icon, BorderLayout.WEST);

            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblName.setForeground(UIConstants.TEXT_PRIMARY);
            top.add(lblName, BorderLayout.CENTER);

            pctLabel = new JLabel(percentage + "%");
            pctLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            pctLabel.setForeground(UIConstants.TEXT_PRIMARY);
            top.add(pctLabel, BorderLayout.EAST);

            add(top, gbc);

            // Row 2: Progress Bar
            gbc.gridy++;
            gbc.insets = new Insets(8, 0, 0, 0);
            
            barComp = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Track
                    g2.setColor(new Color(241, 245, 249));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                    
                    // Bar
                    int fill = (int) (getWidth() * (percentage / 100.0));
                    g2.setColor(progressColor);
                    g2.fillRoundRect(0, 0, fill, getHeight(), 4, 4);
                    g2.dispose();
                }
            };
            barComp.setPreferredSize(new Dimension(0, 6));
            add(barComp, gbc);
        }

        public void setPercentage(int pct) {
            this.percentage = pct;
            pctLabel.setText(pct + "%");
            barComp.repaint();
        }
    }

    // ==========================================
    // 3. DIALOG TAMBAH TRANSAKSI BARU (MODAL)
    // ==========================================
    private static class AddTransactionDialog extends JDialog {
        private final int userId;
        private final DashboardForm parent;

        private RoundedTextField txtJudul;
        private RoundedTextField txtJumlah;
        private JComboBox<KategoriComboItem> comboKategori;
        private JSpinner spinnerTanggal;
        private JTextArea txtCatatan;
        private RoundedButton btnSave;

        public AddTransactionDialog(DashboardForm parent, int userId) {
            super(parent, "Tambah Transaksi Baru", true);
            this.parent = parent;
            this.userId = userId;

            setSize(450, 520);
            setLocationRelativeTo(parent);
            setResizable(false);

            buildUI();
            loadCategories();
        }

        private void buildUI() {
            JPanel container = new JPanel(new GridBagLayout());
            container.setBackground(Color.WHITE);
            container.setBorder(new EmptyBorder(24, 24, 24, 24));
            setContentPane(container);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 16, 0);

            // Title
            JLabel title = new JLabel("Transaksi Baru");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(UIConstants.TEXT_PRIMARY);
            container.add(title, gbc);

            // 1. Judul
            gbc.gridy++;
            container.add(createLabel("Deskripsi / Judul Transaksi"), gbc);
            gbc.gridy++;
            txtJudul = new RoundedTextField("Masukkan judul pengeluaran", null);
            container.add(txtJudul, gbc);

            // 2. Jumlah
            gbc.gridy++;
            container.add(createLabel("Jumlah Pengeluaran (Rp)"), gbc);
            gbc.gridy++;
            txtJumlah = new RoundedTextField("Masukkan jumlah uang", null);
            container.add(txtJumlah, gbc);

            // 3. Kategori
            gbc.gridy++;
            container.add(createLabel("Kategori"), gbc);
            gbc.gridy++;
            comboKategori = new JComboBox<>();
            comboKategori.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            comboKategori.setBackground(Color.WHITE);
            comboKategori.setPreferredSize(new Dimension(0, 40));
            container.add(comboKategori, gbc);

            // 4. Tanggal
            gbc.gridy++;
            container.add(createLabel("Tanggal"), gbc);
            gbc.gridy++;
            SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
            spinnerTanggal = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerTanggal, "yyyy-MM-dd");
            spinnerTanggal.setEditor(dateEditor);
            spinnerTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            spinnerTanggal.setPreferredSize(new Dimension(0, 40));
            container.add(spinnerTanggal, gbc);

            // 5. Catatan
            gbc.gridy++;
            container.add(createLabel("Catatan (Opsional)"), gbc);
            gbc.gridy++;
            txtCatatan = new JTextArea();
            txtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtCatatan.setLineWrap(true);
            txtCatatan.setWrapStyleWord(true);
            JScrollPane noteScroll = new JScrollPane(txtCatatan);
            noteScroll.setPreferredSize(new Dimension(0, 60));
            container.add(noteScroll, gbc);

            // 6. Action Button
            gbc.gridy++;
            gbc.insets = new Insets(10, 0, 0, 0);
            btnSave = new RoundedButton("Simpan Transaksi");
            btnSave.addActionListener(e -> saveTransaction());
            container.add(btnSave, gbc);
        }

        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(UIConstants.TEXT_SECONDARY);
            lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
            return lbl;
        }

        private void loadCategories() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT id, nama, icon FROM kategori ORDER BY nama ASC";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("nama");
                    String icon = rs.getString("icon");
                    if (icon == null) icon = "📦";
                    comboKategori.addItem(new KategoriComboItem(id, name, icon));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void saveTransaction() {
            String judul = txtJudul.getText().trim();
            String jumlahStr = txtJumlah.getText().trim();
            KategoriComboItem item = (KategoriComboItem) comboKategori.getSelectedItem();
            Date date = (Date) spinnerTanggal.getValue();
            String catatan = txtCatatan.getText().trim();

            if (judul.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Deskripsi/Judul tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                txtJudul.requestFocus();
                return;
            }

            double jumlah;
            try {
                jumlah = Double.parseDouble(jumlahStr);
                if (jumlah <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah pengeluaran harus berupa angka positif.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                txtJumlah.requestFocus();
                return;
            }

            if (item == null) {
                JOptionPane.showMessageDialog(this, "Kategori belum dipilih.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnSave.setEnabled(false);
            btnSave.setText("Menyimpan...");

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO pengeluaran (user_id, kategori_id, judul, jumlah, tanggal, catatan) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setInt(2, item.id);
                stmt.setString(3, judul);
                stmt.setDouble(4, jumlah);
                stmt.setDate(5, new java.sql.Date(date.getTime()));
                stmt.setString(6, catatan.isEmpty() ? null : catatan);

                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Transaksi pengeluaran berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent dashboard
                parent.refreshDashboardData();
                dispose();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi ke database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                btnSave.setEnabled(true);
                btnSave.setText("Simpan Transaksi");
            }
        }

        private static class KategoriComboItem {
            final int id;
            final String name;
            final String icon;

            KategoriComboItem(int id, String name, String icon) {
                this.id = id;
                this.name = name;
                this.icon = icon;
            }

            @Override
            public String toString() {
                return icon + " " + name;
            }
        }
    }
}
