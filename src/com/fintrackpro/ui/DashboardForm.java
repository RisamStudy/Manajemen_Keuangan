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

    // Card Layout for switching screens
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private RoundedTextField searchField;

    // Transaksi Menu Elements
    private MiniStatCard tStatSaldo;
    private MiniStatCard tStatPengeluaran;
    private JComboBox<String> tComboTanggal;
    private JComboBox<String> tComboKategori;
    private JButton btnJenisSemua;
    private JButton btnJenisPemasukan;
    private JButton btnJenisPengeluaran;
    private JTable tTable;
    private DefaultTableModel tTableModel;
    private JLabel lblPaginationInfo;
    private DetailBarChart tDetailChart;
    private String selectedJenis = "Semua";

    // Sidebar items
    private SidebarMenuItem itemDashboard;
    private SidebarMenuItem itemTransaksi;
    private SidebarMenuItem itemAnggaran;
    private SidebarMenuItem itemLaporan;
    private SidebarMenuItem itemPengaturan;
    private SidebarMenuItem itemBantuan;

    // Tambah Transaksi page fields
    private String ttJenis = "PENGELUARAN";
    private JButton ttBtnPengeluaran;
    private JButton ttBtnPemasukan;
    private RoundedTextField ttTxtNama;
    private JTextField ttTxtJumlah;
    private JTextField ttTxtTanggal;
    private JComboBox<KategoriItem> ttComboKategori;
    private JTextArea ttTxtCatatan;
    private JLabel ttLblBudgetUsed;
    private JProgressBar ttProgressBudget;
    private JPanel ttRecentPanel;
    private RoundedButton ttBtnSave;

    public DashboardForm(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        initializeFrame();
        buildUI();
        setLocationRelativeTo(null);
        
        // Load initial data
        refreshDashboardData();
        refreshTransaksiPage();
    }

    private void initializeFrame() {
        setTitle("FinTrack Pro - Ringkasan Dashborad");
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
        
        itemDashboard = new SidebarMenuItem("Dashboard", "â–¦", true);
        itemTransaksi = new SidebarMenuItem("Transaksi", "$", false);
        itemAnggaran = new SidebarMenuItem("Anggaran", "%", false);
        itemLaporan = new SidebarMenuItem("Laporan", "â‰¡", false);
        
        menuContainer.add(itemDashboard);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemTransaksi);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemAnggaran);
        menuContainer.add(Box.createVerticalStrut(8));
        menuContainer.add(itemLaporan);
        
        sidebarPanel.add(menuContainer, sgbc);

        // Button + Transaksi Baru
        sgbc.gridy++;
        sgbc.insets = new Insets(30, 20, 30, 20);
        RoundedButton btnAddTransaction = new RoundedButton("ï¼‹ Transaksi Baru");
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
        
        itemPengaturan = new SidebarMenuItem("Pengaturan", "âš™", false);
        itemBantuan = new SidebarMenuItem("Bantuan", "?", false);
        
        bottomMenuContainer.add(itemPengaturan);
        bottomMenuContainer.add(Box.createVerticalStrut(8));
        bottomMenuContainer.add(itemBantuan);
        
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
        this.searchField = new RoundedTextField("Cari transaksi, aset...", null);
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.addActionListener(e -> {
            if (itemTransaksi != null && itemTransaksi.active) {
                refreshTransaksiTableData();
            }
        });
        searchWrapper.add(searchField);
        topBar.add(searchWrapper, BorderLayout.WEST);
        
        // Profile & Icons (kanan)
        JPanel profileWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 13));
        profileWrapper.setOpaque(false);
        
        // Notification & History icons
        JLabel bellIcon = new JLabel("ðŸ””");
        bellIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        bellIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileWrapper.add(bellIcon);
        
        JLabel historyIcon = new JLabel("â³");
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

        JLabel chevronLabel = new JLabel("â–¼");
        chevronLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chevronLabel.setForeground(UIConstants.TEXT_SECONDARY);

        final JPanel profileMenuButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        profileMenuButton.setOpaque(false);
        profileMenuButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileMenuButton.add(namePanel);
        profileMenuButton.add(avatarLabel);
        profileMenuButton.add(chevronLabel);

        MouseAdapter profileDropdownListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showProfileDropdown(profileMenuButton);
            }
        };
        profileMenuButton.addMouseListener(profileDropdownListener);
        namePanel.addMouseListener(profileDropdownListener);
        lblName.addMouseListener(profileDropdownListener);
        lblRole.addMouseListener(profileDropdownListener);
        avatarLabel.addMouseListener(profileDropdownListener);
        chevronLabel.addMouseListener(profileDropdownListener);
        profileWrapper.add(profileMenuButton);
        
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
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        cardPanel.add(scrollPane, "Dashboard");
        buildTransaksiUI();
        buildTambahTransaksiUI();
        buildAnggaranUI();
        buildLaporanUI();
        
        mainContainer.add(cardPanel, BorderLayout.CENTER);

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
        
        JLabel lblMainTitle = new JLabel("Ringkasan Dashboard");
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
        
        JButton btnPeriod = new JButton("ðŸ“…  30 Hari Terakhir");
        styleSecondaryButton(btnPeriod);
        actionButtonsPanel.add(btnPeriod);
        
        JButton btnExport = new JButton("ðŸ“¤  Ekspor Laporan");
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
        
        totalKekayaanCard = new StatCard("TOTAL KEKAYAAN BERSIH", "Rp0", "0%", "Target: Rp0", new Color(34, 197, 94), true, 0);
        pendapatanCard = new StatCard("PENDAPATAN BULANAN", "Rp0", "0%", "", new Color(34, 197, 94), false, 0);
        pengeluaranCard = new StatCard("PENGELUARAN BULANAN", "Rp0", "0%", "0% dari bulan lalu", new Color(239, 68, 68), false, 0);
        
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
                g2.setColor(new Color(15, 23, 42));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(30, 41, 59));
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
        lblAssetTitle.setForeground(Color.WHITE);
        assetsCard.add(lblAssetTitle, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(20, 0, 0, 0);
        sahamPanel = new AssetProgressPanel("Saham", 0, UIConstants.PRIMARY_BLUE, "â†‘");
        assetsCard.add(sahamPanel, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(15, 0, 0, 0);
        kasPanel = new AssetProgressPanel("Kas", 0, new Color(16, 185, 129), "$");
        assetsCard.add(kasPanel, agbc);
        
        agbc.gridy++;
        agbc.insets = new Insets(15, 0, 0, 0);
        lainnyaPanel = new AssetProgressPanel("Lainnya", 0, new Color(107, 114, 128), "â—†");
        assetsCard.add(lainnyaPanel, agbc);
        
        agbc.gridy++;
        agbc.weighty = 1.0;
        assetsCard.add(Box.createVerticalStrut(10), agbc);
        
        agbc.gridy++;
        agbc.weighty = 0.0;
        agbc.insets = new Insets(15, 0, 0, 0);
        JLabel lblAssetFooter = new JLabel("Lihat detail portofolio lengkap â†’");
        lblAssetFooter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAssetFooter.setForeground(new Color(147, 197, 253));
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

    private void showProfileDropdown(Component invoker) {
        JPopupMenu profileMenu = new JPopupMenu();
        profileMenu.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutItem.setForeground(new Color(239, 68, 68));
        logoutItem.setBorder(new EmptyBorder(8, 14, 8, 40));
        logoutItem.addActionListener(e -> logout());

        profileMenu.add(logoutItem);
        profileMenu.show(invoker, 0, invoker.getHeight() + 6);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Keluar dari akun ini?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
    }

    // Legacy sidebar item builder removed in favor of SidebarMenuItem component

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

                    String previousMonthQuery = "SELECT COALESCE(SUM(jumlah),0) FROM pengeluaran " +
                            "WHERE user_id = ? " +
                            "AND MONTH(tanggal) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH)) " +
                            "AND YEAR(tanggal) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))";
                    stmt = conn.prepareStatement(previousMonthQuery);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        data.previousMonthExpenses = rs.getDouble(1);
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
                        
                        if (item.kategoriIcon == null) item.kategoriIcon = "ðŸ“¦";
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
                    pengeluaranCard.setBadgeText(formatExpenseTrendBadge(data.monthlyExpenses, data.previousMonthExpenses));
                    pengeluaranCard.setSubtext(formatExpenseTrendText(data.monthlyExpenses, data.previousMonthExpenses));
                    
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

    private String formatExpenseTrendBadge(double currentMonth, double previousMonth) {
        if (previousMonth <= 0) {
            return "0%";
        }

        double changePercent = ((currentMonth - previousMonth) / previousMonth) * 100.0;
        return String.format(Locale.US, "%+.0f%%", changePercent);
    }

    private String formatExpenseTrendText(double currentMonth, double previousMonth) {
        if (previousMonth <= 0) {
            return "0% dari bulan lalu";
        }

        double changePercent = ((currentMonth - previousMonth) / previousMonth) * 100.0;
        if (changePercent > 0) {
            return String.format(Locale.US, "%.0f%% lebih tinggi dari bulan lalu", changePercent);
        } else if (changePercent < 0) {
            return String.format(Locale.US, "%.0f%% lebih rendah dari bulan lalu", Math.abs(changePercent));
        }
        return "0% dari bulan lalu";
    }

    private void openAddTransactionDialog() {
        setActiveMenuItem(itemTransaksi);
        cardLayout.show(cardPanel, "TambahTransaksi");
        setTitle("FinTrack Pro - Tambah Transaksi Baru");
        loadTambahTransaksiData();
    }

    // Container data untuk background worker
    private static class DashboardData {
        double monthlyExpenses = 0;
        double previousMonthExpenses = 0;
        double[] weeklyChartData = new double[7];
        ArrayList<TransactionItem> recentTransactions = new ArrayList<>();
    }

    private static class TransactionItem {
        int id;
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
        private String badgeText;
        private String subtext;
        private final Color badgeColor;
        private final boolean hasProgress;
        private final int progressPercent;
        private JLabel valueLabel;
        private JLabel badgeLabel;
        private JLabel subLabel;

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
                badgeLabel = new JLabel(badgeText) {
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

                subLabel = new JLabel(subtext);
                subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                subLabel.setForeground(UIConstants.TEXT_SECONDARY);
                progressPanel.add(subLabel, BorderLayout.SOUTH);

                add(progressPanel, gbc);
            } else {
                subLabel = new JLabel(subtext);
                subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                subLabel.setForeground(UIConstants.TEXT_SECONDARY);
                add(subLabel, gbc);
            }
        }

        public void setValue(String val) {
            this.value = val;
            valueLabel.setText(val);
        }

        public void setBadgeText(String text) {
            this.badgeText = text;
            if (badgeLabel != null) {
                badgeLabel.setText(text);
                badgeLabel.repaint();
            }
        }

        public void setSubtext(String text) {
            this.subtext = text;
            if (subLabel != null) {
                subLabel.setText(text);
            }
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
        private final double[] previousWeek = new double[7];
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
                    g2.setColor(new Color(30, 41, 59));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            icon.setPreferredSize(new Dimension(32, 32));
            icon.setHorizontalAlignment(SwingConstants.CENTER);
            icon.setForeground(Color.WHITE);
            icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 15));
            top.add(icon, BorderLayout.WEST);

            JLabel lblName = new JLabel(name);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblName.setForeground(Color.WHITE);
            top.add(lblName, BorderLayout.CENTER);

            pctLabel = new JLabel(percentage + "%");
            pctLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            pctLabel.setForeground(new Color(203, 213, 225));
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
                    g2.setColor(new Color(51, 65, 85));
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
    // ==========================================
    // TAMBAH TRANSAKSI FULL-PAGE UI
    // ==========================================
    private static class KategoriItem {
        final int id;
        final String nama;
        final String icon;
        KategoriItem(int id, String nama, String icon) {
            this.id = id; this.nama = nama; this.icon = icon;
        }
        @Override public String toString() { return icon + " " + nama; }
    }

    private void buildTambahTransaksiUI() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new GridBagLayout());

        JScrollPane sp = new JScrollPane(outer);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBackground(new Color(248, 250, 252));

        GridBagConstraints root = new GridBagConstraints();
        root.fill = GridBagConstraints.HORIZONTAL;
        root.weightx = 1.0;
        root.gridx = 0;
        root.gridy = 0;
        root.insets = new Insets(24, 30, 0, 30);

        // â”€â”€ Breadcrumb â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel crumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        crumb.setOpaque(false);
        JLabel lbCrumb1 = new JLabel("Transactions");
        lbCrumb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbCrumb1.setForeground(UIConstants.TEXT_SECONDARY);
        lbCrumb1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbCrumb1.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMenuClick("Transaksi"); }
        });
        JLabel lbSep = new JLabel("â€º");
        lbSep.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbSep.setForeground(UIConstants.TEXT_SECONDARY);
        JLabel lbCrumb2 = new JLabel("Transaksi Baru");
        lbCrumb2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbCrumb2.setForeground(UIConstants.TEXT_PRIMARY);
        crumb.add(lbCrumb1); crumb.add(lbSep); crumb.add(lbCrumb2);
        outer.add(crumb, root);

        // â”€â”€ Page title â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        root.gridy++; root.insets = new Insets(6, 30, 0, 30);
        JPanel titleRow = new JPanel();
        titleRow.setOpaque(false);
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.Y_AXIS));
        JLabel lbPageTitle = new JLabel("Tambah Transaksi Baru");
        lbPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbPageTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbPageSub = new JLabel("Catat arus kas masuk atau keluar dengan detail presisi.");
        lbPageSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPageSub.setForeground(UIConstants.TEXT_SECONDARY);
        titleRow.add(lbPageTitle); titleRow.add(Box.createVerticalStrut(4)); titleRow.add(lbPageSub);
        outer.add(titleRow, root);

        // â”€â”€ Body: left form + right sidebar â”€â”€â”€â”€â”€â”€â”€â”€â”€
        root.gridy++; root.insets = new Insets(20, 30, 30, 30);
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints bl = new GridBagConstraints();
        bl.fill = GridBagConstraints.BOTH;
        bl.gridy = 0; bl.weighty = 1.0;

        // â”€â”€ LEFT: FORM CARD â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        bl.gridx = 0; bl.weightx = 0.62; bl.insets = new Insets(0, 0, 0, 14);
        JPanel formCard = buildRoundedWhiteCard();
        formCard.setBorder(new EmptyBorder(28, 28, 28, 28));
        formCard.setLayout(new GridBagLayout());

        GridBagConstraints fg = new GridBagConstraints();
        fg.fill = GridBagConstraints.HORIZONTAL;
        fg.weightx = 1.0; fg.gridx = 0; fg.gridy = 0;
        fg.insets = new Insets(0, 0, 18, 0);

        // Tipe Transaksi
        JPanel tipeSection = new JPanel();
        tipeSection.setOpaque(false);
        tipeSection.setLayout(new BoxLayout(tipeSection, BoxLayout.Y_AXIS));
        JLabel lbTipe = new JLabel("Tipe Transaksi");
        lbTipe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbTipe.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbTipeSub = new JLabel("Pilih apakah ini uang masuk atau keluar.");
        lbTipeSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbTipeSub.setForeground(UIConstants.TEXT_SECONDARY);
        tipeSection.add(lbTipe);
        tipeSection.add(Box.createVerticalStrut(4));
        tipeSection.add(lbTipeSub);
        tipeSection.add(Box.createVerticalStrut(10));

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 0, 0));
        togglePanel.setOpaque(false);
        togglePanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        togglePanel.setMaximumSize(new Dimension(10000, 40));

        ttBtnPengeluaran = new JButton("PENGELUARAN");
        ttBtnPemasukan  = new JButton("PEMASUKAN");
        applyTipeToggle(ttBtnPengeluaran, true);
        applyTipeToggle(ttBtnPemasukan,  false);
        ttBtnPengeluaran.addActionListener(e -> selectTipeTransaksi("PENGELUARAN"));
        ttBtnPemasukan.addActionListener(e  -> selectTipeTransaksi("PEMASUKAN"));

        togglePanel.add(ttBtnPengeluaran);
        togglePanel.add(ttBtnPemasukan);
        tipeSection.add(togglePanel);
        formCard.add(tipeSection, fg);

        // Row: Nama Transaksi + Jumlah
        fg.gridy++; fg.insets = new Insets(0, 0, 14, 0);
        JPanel row1 = new JPanel(new GridBagLayout());
        row1.setOpaque(false);
        GridBagConstraints r1 = new GridBagConstraints();
        r1.fill = GridBagConstraints.HORIZONTAL; r1.gridy = 0; r1.insets = new Insets(0, 0, 0, 10);
        r1.gridx = 0; r1.weightx = 0.6;
        JPanel pNama = buildFieldPanel("NAMA TRANSAKSI");
        ttTxtNama = new RoundedTextField("Contoh: Makan Malam Klien", null);
        pNama.add(ttTxtNama);
        row1.add(pNama, r1);

        r1.gridx = 1; r1.weightx = 0.4; r1.insets = new Insets(0, 0, 0, 0);
        JPanel pJml = buildFieldPanel("JUMLAH");
        JPanel jumlahRow = new JPanel(new BorderLayout(4, 0));
        jumlahRow.setOpaque(false);
        JLabel rpLabel = new JLabel("Rp");
        rpLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rpLabel.setForeground(UIConstants.TEXT_PRIMARY);
        ttTxtJumlah = new JTextField();
        ttTxtJumlah.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ttTxtJumlah.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        jumlahRow.add(rpLabel, BorderLayout.WEST);
        jumlahRow.add(ttTxtJumlah, BorderLayout.CENTER);
        pJml.add(jumlahRow);
        row1.add(pJml, r1);
        formCard.add(row1, fg);

        // Row: Tanggal + Kategori
        fg.gridy++;
        JPanel row2 = new JPanel(new GridBagLayout());
        row2.setOpaque(false);
        GridBagConstraints r2 = new GridBagConstraints();
        r2.fill = GridBagConstraints.HORIZONTAL; r2.gridy = 0; r2.insets = new Insets(0, 0, 0, 10);
        r2.gridx = 0; r2.weightx = 0.5;
        JPanel pTgl = buildFieldPanel("TANGGAL");
        ttTxtTanggal = new JTextField();
        ttTxtTanggal.setText(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        ttTxtTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ttTxtTanggal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        pTgl.add(ttTxtTanggal);
        row2.add(pTgl, r2);

        r2.gridx = 1; r2.insets = new Insets(0, 0, 0, 0);
        JPanel pKat = buildFieldPanel("KATEGORI");
        ttComboKategori = new JComboBox<>();
        ttComboKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ttComboKategori.setBackground(Color.WHITE);
        ttComboKategori.setPreferredSize(new Dimension(0, 40));
        pKat.add(ttComboKategori);
        row2.add(pKat, r2);
        formCard.add(row2, fg);

        // Catatan
        fg.gridy++;
        JPanel pCat = buildFieldPanel("CATATAN");
        ttTxtCatatan = new JTextArea(3, 0);
        ttTxtCatatan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ttTxtCatatan.setLineWrap(true);
        ttTxtCatatan.setWrapStyleWord(true);
        ttTxtCatatan.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        ttTxtCatatan.setForeground(new Color(148, 163, 184));
        ttTxtCatatan.setText("Tambahkan keterangan tambahan jika diperlukan...");
        ttTxtCatatan.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (ttTxtCatatan.getText().equals("Tambahkan keterangan tambahan jika diperlukan...")) {
                    ttTxtCatatan.setText(""); ttTxtCatatan.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (ttTxtCatatan.getText().isEmpty()) {
                    ttTxtCatatan.setForeground(new Color(148, 163, 184));
                    ttTxtCatatan.setText("Tambahkan keterangan tambahan jika diperlukan...");
                }
            }
        });
        pCat.add(new JScrollPane(ttTxtCatatan) {{ setBorder(null); }});
        formCard.add(pCat, fg);

        // â”€â”€ Buttons â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        fg.gridy++; fg.insets = new Insets(16, 0, 0, 0);
        ttBtnSave = new RoundedButton("Simpan Transaksi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.PRIMARY_BLUE, getWidth(), 0,
                        new Color(99, 102, 241));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        ttBtnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ttBtnSave.setForeground(Color.WHITE);
        ttBtnSave.setBackground(UIConstants.PRIMARY_BLUE);
        ttBtnSave.setPreferredSize(new Dimension(0, 46));
        ttBtnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        ttBtnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ttBtnSave.setFocusPainted(false);
        ttBtnSave.setBorder(new EmptyBorder(0, 0, 0, 0));
        ttBtnSave.addActionListener(e -> saveTambahTransaksi());
        formCard.add(ttBtnSave, fg);

        // Batal link
        fg.gridy++; fg.insets = new Insets(8, 0, 0, 0);
        JPanel batalRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        batalRow.setOpaque(false);
        JLabel lbBatal = new JLabel("Batal â€” kembali ke daftar transaksi");
        lbBatal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbBatal.setForeground(UIConstants.TEXT_SECONDARY);
        lbBatal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbBatal.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMenuClick("Transaksi"); }
            @Override public void mouseEntered(MouseEvent e) { lbBatal.setForeground(UIConstants.PRIMARY_BLUE); }
            @Override public void mouseExited(MouseEvent e)  { lbBatal.setForeground(UIConstants.TEXT_SECONDARY); }
        });
        batalRow.add(lbBatal);
        formCard.add(batalRow, fg);

        body.add(formCard, bl);

        // â”€â”€ RIGHT: SIDEBAR â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        bl.gridx = 1; bl.weightx = 0.38; bl.insets = new Insets(0, 0, 0, 0);
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Status Anggaran card
        JPanel budgetCard = buildRoundedWhiteCard();
        budgetCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        budgetCard.setLayout(new GridBagLayout());
        GridBagConstraints bc = new GridBagConstraints();
        bc.fill = GridBagConstraints.HORIZONTAL; bc.weightx = 1.0; bc.gridx = 0; bc.gridy = 0;

        JPanel bHeader = new JPanel(new BorderLayout());
        bHeader.setOpaque(false);
        JLabel lbBudgetTitle = new JLabel("Status Anggaran");
        lbBudgetTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbBudgetTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbBudgetSub = new JLabel("KATEGORI ANGGARAN");
        lbBudgetSub.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbBudgetSub.setForeground(UIConstants.TEXT_SECONDARY);
        bHeader.add(lbBudgetTitle, BorderLayout.WEST);
        budgetCard.add(bHeader, bc);

        bc.gridy++; bc.insets = new Insets(6, 0, 0, 0);
        JPanel bRow = new JPanel(new BorderLayout());
        bRow.setOpaque(false);
        JLabel lbTerpakai = new JLabel("Terpakai");
        lbTerpakai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbTerpakai.setForeground(UIConstants.TEXT_SECONDARY);
        ttLblBudgetUsed = new JLabel("Rp 0");
        ttLblBudgetUsed.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ttLblBudgetUsed.setForeground(UIConstants.TEXT_PRIMARY);
        bRow.add(lbTerpakai, BorderLayout.WEST);
        bRow.add(ttLblBudgetUsed, BorderLayout.EAST);
        budgetCard.add(bRow, bc);

        bc.gridy++; bc.insets = new Insets(8, 0, 0, 0);
        ttProgressBudget = new JProgressBar(0, 100);
        ttProgressBudget.setValue(0);
        ttProgressBudget.setStringPainted(false);
        ttProgressBudget.setForeground(UIConstants.PRIMARY_BLUE);
        ttProgressBudget.setBackground(new Color(226, 232, 240));
        ttProgressBudget.setPreferredSize(new Dimension(0, 8));
        ttProgressBudget.setBorder(null);
        budgetCard.add(ttProgressBudget, bc);

        bc.gridy++; bc.insets = new Insets(8, 0, 0, 0);
        JPanel limitRow = new JPanel(new BorderLayout());
        limitRow.setOpaque(false);
        JLabel lbLimit = new JLabel("Limit: Rp 5.000.000");
        lbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbLimit.setForeground(UIConstants.TEXT_SECONDARY);
        JLabel lbSisa = new JLabel("Sisa 100%");
        lbSisa.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbSisa.setForeground(new Color(16, 185, 129));
        limitRow.add(lbLimit, BorderLayout.WEST);
        limitRow.add(lbSisa, BorderLayout.EAST);
        budgetCard.add(limitRow, bc);

        bc.gridy++; bc.insets = new Insets(14, 0, 0, 0);
        JLabel lbBudgetNote = new JLabel(
            "<html><i>Pengeluaran ini masih dalam batas yang aman anggaran bulanan Anda.</i></html>");
        lbBudgetNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbBudgetNote.setForeground(UIConstants.TEXT_SECONDARY);
        budgetCard.add(lbBudgetNote, bc);

        sidebar.add(budgetCard);
        sidebar.add(Box.createVerticalStrut(16));

        // Terakhir Dicatat card
        JPanel recentCard = buildRoundedWhiteCard();
        recentCard.setBorder(new EmptyBorder(20, 20, 16, 20));
        recentCard.setLayout(new BorderLayout());
        JLabel lbRecentTitle = new JLabel("Terakhir Dicatat");
        lbRecentTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbRecentTitle.setForeground(UIConstants.TEXT_PRIMARY);
        lbRecentTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        recentCard.add(lbRecentTitle, BorderLayout.NORTH);
        ttRecentPanel = new JPanel();
        ttRecentPanel.setOpaque(false);
        ttRecentPanel.setLayout(new BoxLayout(ttRecentPanel, BoxLayout.Y_AXIS));
        recentCard.add(ttRecentPanel, BorderLayout.CENTER);

        JLabel lbLihatSemua = new JLabel("LIHAT SEMUA RIWAYAT");
        lbLihatSemua.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbLihatSemua.setForeground(UIConstants.PRIMARY_BLUE);
        lbLihatSemua.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbLihatSemua.setBorder(new EmptyBorder(12, 0, 0, 0));
        lbLihatSemua.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMenuClick("Transaksi"); }
        });
        recentCard.add(lbLihatSemua, BorderLayout.SOUTH);

        sidebar.add(recentCard);

        body.add(sidebar, bl);
        outer.add(body, root);

        cardPanel.add(sp, "TambahTransaksi");
    }

    private JPanel buildFieldPanel(String labelText) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.add(lbl);
        return p;
    }

    private JPanel buildRoundedWhiteCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(229, 231, 235));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    private void applyTipeToggle(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBackground(UIConstants.PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(248, 250, 252));
            btn.setForeground(new Color(100, 116, 139));
        }
    }

    private void selectTipeTransaksi(String tipe) {
        ttJenis = tipe;
        applyTipeToggle(ttBtnPengeluaran, tipe.equals("PENGELUARAN"));
        applyTipeToggle(ttBtnPemasukan,  tipe.equals("PEMASUKAN"));
    }

    private void loadTambahTransaksiData() {
        // Reset form
        if (ttTxtNama != null) ttTxtNama.setText("");
        if (ttTxtJumlah != null) ttTxtJumlah.setText("");
        if (ttTxtTanggal != null)
            ttTxtTanggal.setText(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        if (ttTxtCatatan != null) {
            ttTxtCatatan.setForeground(new Color(148, 163, 184));
            ttTxtCatatan.setText("Tambahkan keterangan tambahan jika diperlukan...");
        }
        selectTipeTransaksi("PENGELUARAN");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws Exception {
                // Load categories
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String q = "SELECT id, nama, icon FROM kategori ORDER BY nama ASC";
                    ResultSet rs = conn.prepareStatement(q).executeQuery();
                    SwingUtilities.invokeLater(() -> ttComboKategori.removeAllItems());
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nama = rs.getString("nama");
                        String icon = rs.getString("icon");
                        if (icon == null) icon = "ðŸ“¦";
                        KategoriItem ki = new KategoriItem(id, nama, icon);
                        SwingUtilities.invokeLater(() -> ttComboKategori.addItem(ki));
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }

                // Load budget used
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String q = "SELECT SUM(jumlah) FROM pengeluaran WHERE user_id = ? AND MONTH(tanggal)=MONTH(CURDATE()) AND YEAR(tanggal)=YEAR(CURDATE())";
                    PreparedStatement stmt = conn.prepareStatement(q);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    double used = rs.next() ? rs.getDouble(1) : 0;
                    double limit = 0;
                    int pct = limit > 0 ? Math.min(100, (int)(used / limit * 100)) : 0;
                    DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
                    String usedStr = "Rp " + new DecimalFormat("###,###,###", sym).format(used);
                    SwingUtilities.invokeLater(() -> {
                        ttLblBudgetUsed.setText(usedStr);
                        ttProgressBudget.setValue(pct);
                        ttProgressBudget.setForeground(pct > 80 ? new Color(239,68,68) : UIConstants.PRIMARY_BLUE);
                    });
                } catch (SQLException ex) { ex.printStackTrace(); }

                // Load last 3 transactions
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String q = "SELECT p.judul, k.icon, p.jumlah, k.warna FROM pengeluaran p LEFT JOIN kategori k ON p.kategori_id=k.id WHERE p.user_id=? ORDER BY p.tanggal DESC, p.id DESC LIMIT 3";
                    PreparedStatement stmt = conn.prepareStatement(q);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
                    DecimalFormat df = new DecimalFormat("###,###,###", sym);
                    java.util.List<JPanel> rows = new java.util.ArrayList<>();
                    while (rs.next()) {
                        String jd = rs.getString("judul"); if (jd == null) jd = "-";
                        String ic = rs.getString("icon"); if (ic == null) ic = "ðŸ“¦";
                        double jml = rs.getDouble("jumlah");
                        JPanel row = new JPanel(new BorderLayout(10, 0));
                        row.setOpaque(false); row.setMaximumSize(new Dimension(10000, 48));
                        row.setBorder(new EmptyBorder(6, 0, 6, 0));
                        JLabel icLbl = new JLabel(ic);
                        icLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                        row.add(icLbl, BorderLayout.WEST);
                        JPanel mid = new JPanel();
                        mid.setOpaque(false);
                        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
                        JLabel jdLbl = new JLabel(jd);
                        jdLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        jdLbl.setForeground(UIConstants.TEXT_PRIMARY);
                        JLabel agLbl = new JLabel("3 jam yang lalu");
                        agLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        agLbl.setForeground(UIConstants.TEXT_SECONDARY);
                        mid.add(jdLbl); mid.add(agLbl);
                        row.add(mid, BorderLayout.CENTER);
                        JLabel amtLbl = new JLabel("-Rp " + df.format(jml));
                        amtLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        amtLbl.setForeground(new Color(239, 68, 68));
                        row.add(amtLbl, BorderLayout.EAST);
                        rows.add(row);
                    }
                    SwingUtilities.invokeLater(() -> {
                        ttRecentPanel.removeAll();
                        for (JPanel r : rows) ttRecentPanel.add(r);
                        ttRecentPanel.revalidate(); ttRecentPanel.repaint();
                    });
                } catch (SQLException ex) { ex.printStackTrace(); }
                return null;
            }
        };
        worker.execute();
    }

    private void saveTambahTransaksi() {
        String judul = ttTxtNama != null ? ttTxtNama.getText().trim() : "";
        String jumlahStr = ttTxtJumlah != null ? ttTxtJumlah.getText().trim() : "";
        KategoriItem katItem = ttComboKategori != null ? (KategoriItem) ttComboKategori.getSelectedItem() : null;
        String catatan = (ttTxtCatatan != null && !ttTxtCatatan.getText().equals("Tambahkan keterangan tambahan jika diperlukan..."))
            ? ttTxtCatatan.getText().trim() : "";

        if (judul.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama transaksi tidak boleh kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double jumlah;
        try {
            jumlah = Double.parseDouble(jumlahStr.replace(",","."));
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka positif.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (katItem == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ttBtnSave != null) { ttBtnSave.setEnabled(false); ttBtnSave.setText("Menyimpan..."); }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO pengeluaran (user_id, kategori_id, judul, jumlah, tanggal, catatan) VALUES (?,?,?,?,CURDATE(),?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, katItem.id);
            ps.setString(3, judul);
            ps.setDouble(4, jumlah);
            ps.setString(5, catatan.isEmpty() ? null : catatan);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            refreshDashboardData();
            refreshTransaksiPage();
            handleMenuClick("Transaksi");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            if (ttBtnSave != null) { ttBtnSave.setEnabled(true); ttBtnSave.setText("Simpan Transaksi  â–¶"); }
        }
    }

    // ==========================================
    // SIDEBAR MENU CLICK HANDLER
    // ==========================================
    private void handleMenuClick(String title) {
        if (title.equals("Dashboard")) {
            setActiveMenuItem(itemDashboard);
            cardLayout.show(cardPanel, "Dashboard");
            setTitle("FinTrack Pro - Ringkasan Dashboard");
        } else if (title.equals("Transaksi")) {
            setActiveMenuItem(itemTransaksi);
            cardLayout.show(cardPanel, "Transaksi");
            setTitle("FinTrack Pro - Riwayat Transaksi");
            refreshTransaksiPage();
        } else if (title.equals("Anggaran")) {
            setActiveMenuItem(itemAnggaran);
            cardLayout.show(cardPanel, "Anggaran");
            setTitle("FinTrack Pro - Ringkasan Anggaran");
            refreshAnggaranPage();
        } else if (title.equals("Laporan")) {
            setActiveMenuItem(itemLaporan);
            cardLayout.show(cardPanel, "Laporan");
            setTitle("FinTrack Pro - Analisis Performa");
            refreshLaporanPage();
        } else {
            JOptionPane.showMessageDialog(DashboardForm.this, 
                "Fitur '" + title + "' masih dalam pengembangan.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ==========================================
    // LAPORAN PAGE
    // ==========================================
    private double[] laporanKatValues = {};
    private String[] laporanKatNames  = {};
    private Color[]  laporanKatColors = {};
    private double laporanTotalSpent = 0;
    private double laporanSavingRate = 0;
    private double laporanNetAccum   = 0;
    private double[] laporanMonthlySpent    = new double[6];
    private double[] laporanMonthlyIncome   = new double[6];
    private String[] laporanMonthLabels     = new String[6];
    private JPanel laporanDonutPanel;
    private JPanel laporanLegendPanel;
    private JPanel laporanBarPanel;
    private JLabel laporanLblSavingRate;
    private JLabel laporanLblNetAccum;
    private JProgressBar laporanSavingBar;

    private void buildLaporanUI() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JScrollPane sp = new JScrollPane(outer);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBackground(new Color(248, 250, 252));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.gridx = 0; g.gridy = 0;
        g.insets = new Insets(24, 30, 0, 30);

        // â”€â”€ Header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel lbTitle = new JLabel("Analisis Performa");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbTitle.setForeground(UIConstants.TEXT_PRIMARY);
        String periode = "Periode analisis: " +
            new java.text.SimpleDateFormat("01 MMM yyyy", new Locale("id","ID")).format(new Date())
            + " - " + new java.text.SimpleDateFormat("dd MMM yyyy", new Locale("id","ID")).format(new Date());
        JLabel lbSub = new JLabel(periode);
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbSub.setForeground(UIConstants.TEXT_SECONDARY);
        titleBox.add(lbTitle); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(lbSub);
        header.add(titleBox, BorderLayout.WEST);
        JPanel btnHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnHeader.setOpaque(false);
        JButton btnRentang = new JButton("Rentang tanggal");
        styleSecondaryButton(btnRentang);
        RoundedButton btnEkspor = new RoundedButton("Ekspor PDF");
        btnEkspor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHeader.add(btnRentang); btnHeader.add(btnEkspor);
        header.add(btnHeader, BorderLayout.EAST);
        outer.add(header, g);

        // â”€â”€ Top Row: Left Stats + Right Donut â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g.gridy++; g.insets = new Insets(20, 30, 0, 30);
        JPanel topRow = new JPanel(new GridBagLayout());
        topRow.setOpaque(false);
        GridBagConstraints tr = new GridBagConstraints();
        tr.fill = GridBagConstraints.BOTH; tr.gridy = 0; tr.weighty = 1.0;

        // LEFT: Stat cards (Tingkat Tabungan + Akumulasi Bersih)
        tr.gridx = 0; tr.weightx = 0.32; tr.insets = new Insets(0, 0, 0, 14);
        JPanel leftCol = new JPanel();
        leftCol.setOpaque(false);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));

        // Tingkat Tabungan card
        JPanel savCard = buildRoundedWhiteCard();
        savCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        savCard.setLayout(new GridBagLayout());
        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL; sc.weightx = 1.0; sc.gridx = 0; sc.gridy = 0;
        JLabel lbSavTitle = new JLabel("TINGKAT TABUNGAN");
        lbSavTitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbSavTitle.setForeground(UIConstants.TEXT_SECONDARY);
        savCard.add(lbSavTitle, sc);
        sc.gridy++; sc.insets = new Insets(8, 0, 0, 0);
        laporanLblSavingRate = new JLabel("0%");
        laporanLblSavingRate.setFont(new Font("Segoe UI", Font.BOLD, 30));
        laporanLblSavingRate.setForeground(UIConstants.TEXT_PRIMARY);
        savCard.add(laporanLblSavingRate, sc);
        sc.gridy++; sc.insets = new Insets(10, 0, 0, 0);
        laporanSavingBar = new JProgressBar(0, 100);
        laporanSavingBar.setValue(0);
        laporanSavingBar.setStringPainted(false);
        laporanSavingBar.setForeground(UIConstants.PRIMARY_BLUE);
        laporanSavingBar.setBackground(new Color(226, 232, 240));
        laporanSavingBar.setPreferredSize(new Dimension(0, 6));
        laporanSavingBar.setBorder(null);
        savCard.add(laporanSavingBar, sc);
        sc.gridy++; sc.insets = new Insets(6, 0, 0, 0);
        JLabel lbSavVs = new JLabel("+26% vs bulan lalu");
        lbSavVs.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbSavVs.setForeground(new Color(16, 185, 129));
        savCard.add(lbSavVs, sc);
        leftCol.add(savCard);
        leftCol.add(Box.createVerticalStrut(14));

        // Akumulasi Bersih card
        JPanel netCard = buildRoundedWhiteCard();
        netCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        netCard.setLayout(new GridBagLayout());
        GridBagConstraints nc = new GridBagConstraints();
        nc.fill = GridBagConstraints.HORIZONTAL; nc.weightx = 1.0; nc.gridx = 0; nc.gridy = 0;
        JLabel lbNetTitle = new JLabel("AKUMULASI BERSIH");
        lbNetTitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbNetTitle.setForeground(UIConstants.TEXT_SECONDARY);
        netCard.add(lbNetTitle, nc);
        nc.gridy++; nc.insets = new Insets(8, 0, 0, 0);
        laporanLblNetAccum = new JLabel("+Rp0");
        laporanLblNetAccum.setFont(new Font("Segoe UI", Font.BOLD, 22));
        laporanLblNetAccum.setForeground(new Color(16, 185, 129));
        netCard.add(laporanLblNetAccum, nc);
        nc.gridy++; nc.insets = new Insets(10, 0, 0, 0);
        JPanel netFooter = new JPanel(new BorderLayout());
        netFooter.setOpaque(false);
        JLabel lbSurplus = new JLabel("Surplus");
        lbSurplus.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbSurplus.setForeground(new Color(16, 185, 129));
        JLabel lbAmbang = new JLabel("Ambang batas atas");
        lbAmbang.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbAmbang.setForeground(UIConstants.TEXT_SECONDARY);
        netFooter.add(lbSurplus, BorderLayout.WEST);
        netFooter.add(lbAmbang, BorderLayout.EAST);
        netCard.add(netFooter, nc);
        leftCol.add(netCard);
        topRow.add(leftCol, tr);

        // RIGHT: Donut chart + legend
        tr.gridx = 1; tr.weightx = 0.68; tr.insets = new Insets(0, 0, 0, 0);
        JPanel donutCard = buildRoundedWhiteCard();
        donutCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        donutCard.setLayout(new BorderLayout());
        JPanel donutCardHeader = new JPanel(new BorderLayout());
        donutCardHeader.setOpaque(false);
        donutCardHeader.setBorder(new EmptyBorder(0, 0, 14, 0));
        JPanel dcTitleBox = new JPanel();
        dcTitleBox.setOpaque(false);
        dcTitleBox.setLayout(new BoxLayout(dcTitleBox, BoxLayout.Y_AXIS));
        JLabel lbDonutTitle = new JLabel("Pengeluaran per Kategori");
        lbDonutTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbDonutTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbDonutSub = new JLabel("Sebagai persentase seluruh biaya");
        lbDonutSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbDonutSub.setForeground(UIConstants.TEXT_SECONDARY);
        dcTitleBox.add(lbDonutTitle); dcTitleBox.add(Box.createVerticalStrut(2)); dcTitleBox.add(lbDonutSub);
        donutCardHeader.add(dcTitleBox, BorderLayout.WEST);
        donutCard.add(donutCardHeader, BorderLayout.NORTH);

        JPanel donutBody = new JPanel(new BorderLayout(20, 0));
        donutBody.setOpaque(false);

        // Donut chart canvas
        laporanDonutPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g2d) {
                super.paintComponent(g2d);
                paintDonutChart((Graphics2D) g2d);
            }
        };
        laporanDonutPanel.setOpaque(false);
        laporanDonutPanel.setPreferredSize(new Dimension(200, 200));
        donutBody.add(laporanDonutPanel, BorderLayout.WEST);

        // Legend panel
        laporanLegendPanel = new JPanel();
        laporanLegendPanel.setOpaque(false);
        laporanLegendPanel.setLayout(new BoxLayout(laporanLegendPanel, BoxLayout.Y_AXIS));
        donutBody.add(laporanLegendPanel, BorderLayout.CENTER);
        donutCard.add(donutBody, BorderLayout.CENTER);
        topRow.add(donutCard, tr);
        outer.add(topRow, g);

        // â”€â”€ Bottom: Pendapatan vs Pengeluaran bar chart â”€
        g.gridy++; g.insets = new Insets(20, 30, 30, 30);
        JPanel barCard = buildRoundedWhiteCard();
        barCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        barCard.setLayout(new BorderLayout());

        JPanel barCardHdr = new JPanel(new BorderLayout());
        barCardHdr.setOpaque(false);
        barCardHdr.setBorder(new EmptyBorder(0, 0, 16, 0));
        JPanel barTitleBox = new JPanel();
        barTitleBox.setOpaque(false);
        barTitleBox.setLayout(new BoxLayout(barTitleBox, BoxLayout.Y_AXIS));
        JLabel lbBarTitle = new JLabel("Pendapatan vs Pengeluaran");
        lbBarTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbBarTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbBarSub = new JLabel("Perbandingan 6 bulan terakhir");
        lbBarSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbBarSub.setForeground(UIConstants.TEXT_SECONDARY);
        barTitleBox.add(lbBarTitle); barTitleBox.add(Box.createVerticalStrut(2)); barTitleBox.add(lbBarSub);
        barCardHdr.add(barTitleBox, BorderLayout.WEST);

        // Legend
        JPanel barLegend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        barLegend.setOpaque(false);
        for (String[] leg : new String[][]{{"Pendapatan","3B82F6"},{"Pengeluaran","CBD5E1"}}) {
            JPanel ll = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            ll.setOpaque(false);
            JPanel dot = new JPanel() {
                final Color c = Color.decode("#" + leg[1]);
                @Override protected void paintComponent(Graphics gg) {
                    Graphics2D g2 = (Graphics2D)gg.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c); g2.fillOval(0,0,10,10); g2.dispose();
                }
            };
            dot.setOpaque(false); dot.setPreferredSize(new Dimension(10,10));
            JLabel lleg = new JLabel(leg[0]);
            lleg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lleg.setForeground(UIConstants.TEXT_SECONDARY);
            ll.add(dot); ll.add(lleg);
            barLegend.add(ll);
        }
        barCardHdr.add(barLegend, BorderLayout.EAST);
        barCard.add(barCardHdr, BorderLayout.NORTH);

        laporanBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g2d) {
                super.paintComponent(g2d);
                paintBarChart((Graphics2D) g2d);
            }
        };
        laporanBarPanel.setOpaque(false);
        laporanBarPanel.setPreferredSize(new Dimension(0, 220));
        barCard.add(laporanBarPanel, BorderLayout.CENTER);
        outer.add(barCard, g);

        cardPanel.add(sp, "Laporan");
    }

    private void paintDonutChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = laporanDonutPanel.getWidth();
        int h = laporanDonutPanel.getHeight();
        int size = Math.min(w, h) - 20;
        int x = (w - size) / 2;
        int y = (h - size) / 2;
        int thickness = size / 5;

        if (laporanKatValues.length == 0) {
            g2.setColor(new Color(226, 232, 240));
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + thickness/2, y + thickness/2, size - thickness, size - thickness);
            return;
        }

        double total = 0;
        for (double v : laporanKatValues) total += v;
        if (total == 0) return;

        float startAngle = 90f;
        for (int i = 0; i < laporanKatValues.length; i++) {
            float sweep = (float)(laporanKatValues[i] / total * 360.0);
            g2.setColor(laporanKatColors[i]);
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            java.awt.geom.Arc2D arc = new java.awt.geom.Arc2D.Float(
                x + thickness/2, y + thickness/2,
                size - thickness, size - thickness,
                startAngle, -sweep, java.awt.geom.Arc2D.OPEN);
            g2.draw(arc);
            startAngle -= sweep;
        }

        // Center label
        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
        String centerText = "Rp" + new DecimalFormat("###,##0.0", sym).format(laporanTotalSpent / 1_000_000) + " Jt";
        g2.setColor(UIConstants.TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(centerText)) / 2;
        int ty = h / 2 + fm.getAscent() / 2 - 2;
        g2.drawString(centerText, tx, ty);
    }

    private void paintBarChart(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (laporanMonthLabels.length == 0) return;
        int w = laporanBarPanel.getWidth();
        int h = laporanBarPanel.getHeight();
        int padL = 48, padR = 20, padT = 10, padB = 30;
        int chartW = w - padL - padR;
        int chartH = h - padT - padB;
        int months = laporanMonthLabels.length;
        int groupW = chartW / months;
        int barW = Math.max(8, groupW / 3);
        int gap  = barW / 2;

        double maxVal = 1;
        for (double v : laporanMonthlyIncome) maxVal = Math.max(maxVal, v);
        for (double v : laporanMonthlySpent)  maxVal = Math.max(maxVal, v);

        // Y axis grid lines
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
            0, new float[]{4, 4}, 0));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(226, 232, 240));
        for (int li = 0; li <= 4; li++) {
            int yy = padT + chartH - (int)(li / 4.0 * chartH);
            g2.drawLine(padL, yy, w - padR, yy);
            g2.setColor(new Color(148, 163, 184));
            DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
            String lbl = new DecimalFormat("###,##0", sym).format(maxVal * li / 4 / 1000) + "K";
            g2.drawString(lbl, 2, yy + 4);
            g2.setColor(new Color(226, 232, 240));
        }
        g2.setStroke(new BasicStroke(1f));

        Color incomeColor = new Color(59, 130, 246);
        Color spentColor  = new Color(203, 213, 225);
        int radius = 4;

        for (int i = 0; i < months; i++) {
            int centerX = padL + i * groupW + groupW / 2;
            int incomeH = (int)(laporanMonthlyIncome[i] / maxVal * chartH);
            int spentH  = (int)(laporanMonthlySpent[i]  / maxVal * chartH);
            int ix = centerX - barW - gap / 2;
            int sx = centerX + gap / 2;

            // Income bar
            g2.setColor(incomeColor);
            int iy = padT + chartH - incomeH;
            g2.fillRoundRect(ix, iy, barW, incomeH, radius, radius);

            // Spent bar
            g2.setColor(spentColor);
            int sy = padT + chartH - spentH;
            g2.fillRoundRect(sx, sy, barW, spentH, radius, radius);

            // Month label
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            String lbl = laporanMonthLabels[i];
            g2.drawString(lbl, centerX - fm.stringWidth(lbl)/2, h - 8);
        }
    }

    private void refreshLaporanPage() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            double[] katVals; String[] katNames; Color[] katColors;
            double totSpent, savRate, netAccum;
            double[] mSpent = new double[6], mIncome = new double[6];
            String[] mLabels = new String[6];

            @Override
            protected Void doInBackground() throws Exception {
                // Category breakdown
                java.util.List<double[]> vList = new java.util.ArrayList<>();
                java.util.List<String>   nList = new java.util.ArrayList<>();
                java.util.List<Color>    cList = new java.util.ArrayList<>();
                Color[] palette = {
                    new Color(59,130,246), new Color(16,185,129), new Color(245,158,11),
                    new Color(239,68,68),  new Color(139,92,246), new Color(236,72,153),
                    new Color(20,184,166), new Color(107,114,128)
                };
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String q = "SELECT k.nama, COALESCE(SUM(p.jumlah),0) AS total " +
                        "FROM kategori k LEFT JOIN pengeluaran p ON k.id=p.kategori_id " +
                        "AND p.user_id=? AND MONTH(p.tanggal)=MONTH(CURDATE()) AND YEAR(p.tanggal)=YEAR(CURDATE()) " +
                        "WHERE k.user_id IS NULL OR k.user_id=? GROUP BY k.id, k.nama ORDER BY total DESC LIMIT 8";
                    PreparedStatement ps = conn.prepareStatement(q);
                    ps.setInt(1, userId); ps.setInt(2, userId);
                    ResultSet rs = ps.executeQuery();
                    int ci = 0;
                    while (rs.next()) {
                        double v = rs.getDouble("total");
                        String n = rs.getString("nama");
                        vList.add(new double[]{v});
                        nList.add(n);
                        cList.add(palette[ci % palette.length]);
                        totSpent += v;
                        ci++;
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }
                katVals = new double[vList.size()];
                for (int i=0;i<vList.size();i++) katVals[i] = vList.get(i)[0];
                katNames  = nList.toArray(new String[0]);
                katColors = cList.toArray(new Color[0]);

                // Monthly data (last 6 months)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM", new Locale("id","ID"));
                java.util.Calendar cal = java.util.Calendar.getInstance();
                for (int i = 5; i >= 0; i--) {
                    java.util.Calendar mc = (java.util.Calendar) cal.clone();
                    mc.add(java.util.Calendar.MONTH, -i);
                    int mon = mc.get(java.util.Calendar.MONTH) + 1;
                    int yr  = mc.get(java.util.Calendar.YEAR);
                    mLabels[5-i] = sdf.format(mc.getTime());
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT COALESCE(SUM(jumlah),0) FROM pengeluaran " +
                            "WHERE user_id=? AND MONTH(tanggal)=? AND YEAR(tanggal)=?");
                        ps.setInt(1, userId); ps.setInt(2, mon); ps.setInt(3, yr);
                        ResultSet rs = ps.executeQuery();
                        mSpent[5-i] = rs.next() ? rs.getDouble(1) : 0;
                    } catch (SQLException ex) { ex.printStackTrace(); }
                    mIncome[5-i] = 0;
                }
                savRate  = 0;
                netAccum = 0;
                return null;
            }

            @Override
            protected void done() {
                laporanKatValues = katVals;
                laporanKatNames  = katNames;
                laporanKatColors = katColors;
                laporanTotalSpent = totSpent;
                laporanSavingRate = savRate;
                laporanNetAccum   = netAccum;
                laporanMonthlySpent  = mSpent;
                laporanMonthlyIncome = mIncome;
                laporanMonthLabels   = mLabels;

                // Update stat labels
                laporanLblSavingRate.setText(String.format("%.1f%%", savRate));
                laporanSavingBar.setValue((int) savRate);
                DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
                DecimalFormat df = new DecimalFormat("###,###,###", sym);
                laporanLblNetAccum.setText("+Rp" + df.format(netAccum));

                // Build legend
                laporanLegendPanel.removeAll();
                DecimalFormat dfJt = new DecimalFormat("###,##0.0", sym);
                for (int i = 0; i < katNames.length; i++) {
                    final int fi = i;
                    JPanel legRow = new JPanel(new BorderLayout(8, 0));
                    legRow.setOpaque(false);
                    legRow.setBorder(new EmptyBorder(4, 0, 4, 0));
                    legRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

                    JPanel dotBox = new JPanel() {
                        final Color c = katColors[fi];
                        @Override protected void paintComponent(Graphics gg) {
                            Graphics2D g2 = (Graphics2D) gg.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(c); g2.fillOval(0,2,10,10); g2.dispose();
                        }
                    };
                    dotBox.setOpaque(false); dotBox.setPreferredSize(new Dimension(12, 14));

                    JLabel lbName = new JLabel(katNames[i]);
                    lbName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    lbName.setForeground(UIConstants.TEXT_PRIMARY);

                    double pct = totSpent > 0 ? katVals[i] / totSpent * 100 : 0;
                    JLabel lbAmt = new JLabel("Rp" + dfJt.format(katVals[i]/1_000_000) + "Jt  " +
                        String.format("(%.0f%%)", pct));
                    lbAmt.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbAmt.setForeground(UIConstants.TEXT_SECONDARY);

                    JPanel nameRow = new JPanel(new BorderLayout(6, 0));
                    nameRow.setOpaque(false);
                    nameRow.add(dotBox, BorderLayout.WEST);
                    nameRow.add(lbName, BorderLayout.CENTER);
                    legRow.add(nameRow, BorderLayout.WEST);
                    legRow.add(lbAmt, BorderLayout.EAST);
                    laporanLegendPanel.add(legRow);
                }
                laporanLegendPanel.revalidate(); laporanLegendPanel.repaint();
                laporanDonutPanel.repaint();
                laporanBarPanel.repaint();
            }
        };
        worker.execute();
    }

    // ==========================================
    // ANGGARAN PAGE
    // ==========================================
    // Anggaran UI live references
    private JLabel agTotalAnggaran;
    private JLabel agTotalPengeluaran;
    private JLabel agSisaSaldo;
    private JLabel agPeringatan;
    private JPanel agKategoriPanel;
    private JPanel agRecentPanel;
    private JLabel agSavingUsed;
    private JProgressBar agSavingBar;

    private void buildAnggaranUI() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JScrollPane sp = new JScrollPane(outer);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBackground(new Color(248, 250, 252));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0; g.gridx = 0; g.gridy = 0;
        g.insets = new Insets(24, 30, 0, 30);

        // â”€â”€ Header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel lbTitle = new JLabel("Ringkasan Anggaran");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbSub = new JLabel("Mengelola 8 kategori anggaran aktif untuk " +
                new java.text.SimpleDateFormat("MMMM yyyy", new Locale("id","ID")).format(new Date()));
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbSub.setForeground(UIConstants.TEXT_SECONDARY);
        titleBox.add(lbTitle); titleBox.add(Box.createVerticalStrut(4)); titleBox.add(lbSub);
        header.add(titleBox, BorderLayout.WEST);

        JPanel btnHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnHeader.setOpaque(false);
        JButton btnAtur = new JButton("Atur Batasan");
        styleSecondaryButton(btnAtur);
        RoundedButton btnEkspor = new RoundedButton("Ekspor Analisis");
        btnEkspor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHeader.add(btnAtur); btnHeader.add(btnEkspor);
        header.add(btnHeader, BorderLayout.EAST);
        outer.add(header, g);

        // â”€â”€ 4 Stat Cards â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g.gridy++; g.insets = new Insets(20, 30, 0, 30);
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);

        agTotalAnggaran   = new JLabel("Rp0");
        agTotalPengeluaran = new JLabel("Rp0");
        agSisaSaldo        = new JLabel("Rp0");
        agPeringatan       = new JLabel("0");

        statsRow.add(buildAgStatCard("TOTAL ANGGARAN",   agTotalAnggaran,   "â†‘ 5% dari bulan lalu",   new Color(16, 185, 129), false));
        statsRow.add(buildAgStatCard("TOTAL PENGELUARAN", agTotalPengeluaran, "â†‘ 12% dari target",     new Color(59, 130, 246), false));
        statsRow.add(buildAgStatCard("SISA SALDO",        agSisaSaldo,        "Tersisa hingga akhir bulan", new Color(245, 158, 11), false));
        statsRow.add(buildAgStatCard("PERINGATAN",        agPeringatan,       "Kategori melebihi batas", new Color(239, 68, 68), true));
        outer.add(statsRow, g);

        // â”€â”€ Body: Kategori + Savings Goal â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g.gridy++; g.insets = new Insets(20, 30, 0, 30);
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints bl = new GridBagConstraints();
        bl.fill = GridBagConstraints.BOTH; bl.gridy = 0; bl.weighty = 1.0;

        // LEFT: Kategori Anggaran card
        bl.gridx = 0; bl.weightx = 0.63; bl.insets = new Insets(0, 0, 0, 14);
        JPanel katCard = buildRoundedWhiteCard();
        katCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        katCard.setLayout(new BorderLayout());

        JPanel katHeader = new JPanel(new BorderLayout());
        katHeader.setOpaque(false);
        katHeader.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel katLeft = new JPanel();
        katLeft.setOpaque(false);
        katLeft.setLayout(new BoxLayout(katLeft, BoxLayout.Y_AXIS));
        JLabel lbKat = new JLabel("Kategori Anggaran");
        lbKat.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbKat.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbKatSub = new JLabel("Oktober 2025");
        lbKatSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbKatSub.setForeground(UIConstants.TEXT_SECONDARY);
        katLeft.add(lbKat); katLeft.add(Box.createVerticalStrut(2)); katLeft.add(lbKatSub);
        katHeader.add(katLeft, BorderLayout.WEST);

        // Harian / Bulanan toggle pill
        JPanel filterToggle = new JPanel(new GridLayout(1, 2, 0, 0));
        filterToggle.setOpaque(true);
        filterToggle.setBackground(new Color(241, 245, 249));
        filterToggle.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        filterToggle.setPreferredSize(new Dimension(160, 30));

        final JButton[] agFilterBtns = new JButton[2];
        String[] filterLabels = {"Harian", "Bulanan"};
        for (int fi = 0; fi < 2; fi++) {
            final int fIdx = fi;
            JButton fb = new JButton(filterLabels[fi]);
            fb.setFont(new Font("Segoe UI", Font.BOLD, 11));
            fb.setFocusPainted(false);
            fb.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            fb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Default: Bulanan (index 1) is active
            if (fi == 1) {
                fb.setBackground(UIConstants.PRIMARY_BLUE);
                fb.setForeground(Color.WHITE);
                fb.setOpaque(true);
            } else {
                fb.setBackground(new Color(241, 245, 249));
                fb.setForeground(new Color(100, 116, 139));
                fb.setOpaque(true);
            }
            fb.addActionListener(e -> {
                for (int j = 0; j < 2; j++) {
                    agFilterBtns[j].setBackground(j == fIdx ? UIConstants.PRIMARY_BLUE : new Color(241, 245, 249));
                    agFilterBtns[j].setForeground(j == fIdx ? Color.WHITE : new Color(100, 116, 139));
                }
            });
            agFilterBtns[fi] = fb;
            filterToggle.add(fb);
        }
        katHeader.add(filterToggle, BorderLayout.EAST);
        katCard.add(katHeader, BorderLayout.NORTH);

        agKategoriPanel = new JPanel();
        agKategoriPanel.setOpaque(false);
        agKategoriPanel.setLayout(new BoxLayout(agKategoriPanel, BoxLayout.Y_AXIS));
        katCard.add(agKategoriPanel, BorderLayout.CENTER);
        body.add(katCard, bl);

        // RIGHT: Savings Goal card
        bl.gridx = 1; bl.weightx = 0.37; bl.insets = new Insets(0, 0, 0, 0);
        JPanel savCard = buildRoundedWhiteCard();
        savCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        savCard.setLayout(new GridBagLayout());
        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL; sc.weightx = 1.0; sc.gridx = 0; sc.gridy = 0;

        JLabel lbSavTitle = new JLabel("Tujuan Tabungan");
        lbSavTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbSavTitle.setForeground(UIConstants.TEXT_PRIMARY);
        savCard.add(lbSavTitle, sc);

        sc.gridy++; sc.insets = new Insets(2, 0, 0, 0);
        JLabel lbSavSub = new JLabel("Liburan Akhir Tahun");
        lbSavSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbSavSub.setForeground(UIConstants.TEXT_SECONDARY);
        savCard.add(lbSavSub, sc);

        sc.gridy++; sc.insets = new Insets(14, 0, 0, 0);
        JPanel savAmtRow = new JPanel(new BorderLayout());
        savAmtRow.setOpaque(false);
        agSavingUsed = new JLabel("Rp 0");
        agSavingUsed.setFont(new Font("Segoe UI", Font.BOLD, 22));
        agSavingUsed.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbSavTarget = new JLabel("/ Rp 8.450.000");
        lbSavTarget.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbSavTarget.setForeground(UIConstants.TEXT_SECONDARY);
        savAmtRow.add(agSavingUsed, BorderLayout.WEST);
        savAmtRow.add(lbSavTarget, BorderLayout.EAST);
        savCard.add(savAmtRow, sc);

        sc.gridy++; sc.insets = new Insets(10, 0, 0, 0);
        agSavingBar = new JProgressBar(0, 100);
        agSavingBar.setValue(0);
        agSavingBar.setStringPainted(false);
        agSavingBar.setForeground(UIConstants.PRIMARY_BLUE);
        agSavingBar.setBackground(new Color(226, 232, 240));
        agSavingBar.setPreferredSize(new Dimension(0, 10));
        agSavingBar.setBorder(null);
        savCard.add(agSavingBar, sc);

        sc.gridy++; sc.insets = new Insets(8, 0, 0, 0);
        JLabel lbSavPct = new JLabel("0% tercapai");
        lbSavPct.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbSavPct.setForeground(UIConstants.TEXT_SECONDARY);
        savCard.add(lbSavPct, sc);

        sc.gridy++; sc.insets = new Insets(16, 0, 0, 0);
        JLabel lbSavNote = new JLabel(
            "<html>Tambahkan tabungan rutin setiap bulan untuk mencapai tujuan Anda tepat waktu.</html>");
        lbSavNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbSavNote.setForeground(UIConstants.TEXT_SECONDARY);
        savCard.add(lbSavNote, sc);

        body.add(savCard, bl);
        outer.add(body, g);

        // â”€â”€ Recent Budget Transactions â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        g.gridy++; g.insets = new Insets(20, 30, 30, 30);
        JPanel recentCard = buildRoundedWhiteCard();
        recentCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        recentCard.setLayout(new BorderLayout());

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);
        recentHeader.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel lbRecentTitle = new JLabel("Transaksi Anggaran Terbaru");
        lbRecentTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbRecentTitle.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel lbLihatSemua = new JLabel("LIHAT SEMUA RIWAYAT â€º");
        lbLihatSemua.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbLihatSemua.setForeground(UIConstants.PRIMARY_BLUE);
        lbLihatSemua.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbLihatSemua.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { handleMenuClick("Transaksi"); }
        });
        recentHeader.add(lbRecentTitle, BorderLayout.WEST);
        recentHeader.add(lbLihatSemua, BorderLayout.EAST);
        recentCard.add(recentHeader, BorderLayout.NORTH);

        // Table header
        JPanel tblHeader = new JPanel(new GridLayout(1, 4, 0, 0));
        tblHeader.setOpaque(false);
        tblHeader.setBorder(new EmptyBorder(0, 0, 8, 0));
        for (String col : new String[]{"TANGGAL", "KATEGORI", "STATUS", "JUMLAH"}) {
            JLabel lh = new JLabel(col);
            lh.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lh.setForeground(UIConstants.TEXT_SECONDARY);
            if (col.equals("JUMLAH")) lh.setHorizontalAlignment(SwingConstants.RIGHT);
            tblHeader.add(lh);
        }
        JPanel tblWrapper = new JPanel(new BorderLayout());
        tblWrapper.setOpaque(false);
        tblWrapper.add(tblHeader, BorderLayout.NORTH);

        agRecentPanel = new JPanel();
        agRecentPanel.setOpaque(false);
        agRecentPanel.setLayout(new BoxLayout(agRecentPanel, BoxLayout.Y_AXIS));
        tblWrapper.add(agRecentPanel, BorderLayout.CENTER);
        recentCard.add(tblWrapper, BorderLayout.CENTER);
        outer.add(recentCard, g);

        cardPanel.add(sp, "Anggaran");
    }

    private JPanel buildAgStatCard(String label, JLabel valueLabel, String sub, Color accent, boolean badge) {
        JPanel card = buildRoundedWhiteCard();
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0; gc.gridx = 0; gc.gridy = 0;

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel lbLabel = new JLabel(label);
        lbLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lbLabel.setForeground(UIConstants.TEXT_SECONDARY);
        topRow.add(lbLabel, BorderLayout.WEST);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(8, 8));
        topRow.add(dot, BorderLayout.EAST);
        card.add(topRow, gc);

        gc.gridy++; gc.insets = new Insets(8, 0, 0, 0);
        if (badge) {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            valueLabel.setForeground(accent);
        } else {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        }
        card.add(valueLabel, gc);

        gc.gridy++; gc.insets = new Insets(6, 0, 0, 0);
        JLabel lbSub = new JLabel(sub);
        lbSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbSub.setForeground(UIConstants.TEXT_SECONDARY);
        card.add(lbSub, gc);

        return card;
    }

    private JPanel buildKategoriRow(String icon, String name, String sub,
                                    double spent, double limit, Color barColor, boolean over) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
            new EmptyBorder(12, 0, 12, 0)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icon circle
        JLabel icLbl = new JLabel(icon, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(barColor.brighter().brighter());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        icLbl.setPreferredSize(new Dimension(38, 38));
        icLbl.setMinimumSize(new Dimension(38, 38));
        row.add(icLbl, BorderLayout.WEST);

        // Name + progress
        JPanel mid = new JPanel(new GridBagLayout());
        mid.setOpaque(false);
        GridBagConstraints mc = new GridBagConstraints();
        mc.fill = GridBagConstraints.HORIZONTAL; mc.weightx = 1.0; mc.gridx = 0; mc.gridy = 0;

        JLabel lbName = new JLabel(name);
        lbName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbName.setForeground(over ? new Color(239,68,68) : UIConstants.TEXT_PRIMARY);
        mid.add(lbName, mc);

        mc.gridy++; mc.insets = new Insets(1, 0, 5, 0);
        JLabel lbSub2 = new JLabel(sub);
        lbSub2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbSub2.setForeground(UIConstants.TEXT_SECONDARY);
        mid.add(lbSub2, mc);

        mc.gridy++; mc.insets = new Insets(0, 0, 0, 0);
        int pct = limit > 0 ? (int) Math.min(100, spent / limit * 100) : 0;
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(false);
        bar.setForeground(over ? new Color(239,68,68) : barColor);
        bar.setBackground(new Color(226, 232, 240));
        bar.setPreferredSize(new Dimension(0, 6));
        bar.setBorder(null);
        mid.add(bar, mc);

        row.add(mid, BorderLayout.CENTER);

        // Amounts
        DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
        DecimalFormat df = new DecimalFormat("###,###,###", sym);
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel lbSpent = new JLabel("Rp" + df.format(spent));
        lbSpent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbSpent.setForeground(over ? new Color(239,68,68) : UIConstants.TEXT_PRIMARY);
        lbSpent.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel lbLimit = new JLabel("/ Rp" + df.format(limit));
        lbLimit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbLimit.setForeground(UIConstants.TEXT_SECONDARY);
        lbLimit.setAlignmentX(Component.RIGHT_ALIGNMENT);
        if (over) {
            JLabel lbOver = new JLabel("âš  MELEWATI BATAS");
            lbOver.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lbOver.setForeground(new Color(239,68,68));
            lbOver.setAlignmentX(Component.RIGHT_ALIGNMENT);
            right.add(lbOver);
        }
        right.add(lbSpent); right.add(lbLimit);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private void refreshAnggaranPage() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            double totalAnggaran = 0, totalSpent = 0, savingUsed = 0;
            int peringatan = 0;
            final java.util.List<JPanel> katRows = new java.util.ArrayList<>();
            final java.util.List<JPanel> recentRows = new java.util.ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
                DecimalFormat df = new DecimalFormat("###,###,###", sym);

                double[] limits = {0, 0, 0, 0};
                String[] icons  = {"ðŸ½", "ðŸ ", "ðŸŽ­", "ðŸšŒ"};
                String[] names  = {"Makanan & Minuman", "Sewa & Tempat Tinggal", "Hiburan", "Transportasi"};
                String[] subs   = {"Restoran, Belanja Makanan", "Kost, Utilitas, Perawatan",
                                   "Bioskop, Langganan, Acara", "Ojek, Bus, Bensin, Tol"};
                Color[]  colors = {new Color(16,185,129), new Color(59,130,246),
                                   new Color(239,68,68),  new Color(245,158,11)};

                try (Connection conn = DatabaseConnection.getConnection()) {
                    for (int i = 0; i < names.length; i++) {
                        // Get actual spend for this category (by name match)
                        String q = "SELECT COALESCE(SUM(p.jumlah),0) FROM pengeluaran p " +
                            "JOIN kategori k ON p.kategori_id=k.id " +
                            "WHERE p.user_id=? AND k.nama LIKE ? " +
                            "AND MONTH(p.tanggal)=MONTH(CURDATE()) AND YEAR(p.tanggal)=YEAR(CURDATE())";
                        PreparedStatement ps = conn.prepareStatement(q);
                        ps.setInt(1, userId);
                        ps.setString(2, "%" + names[i].split(" & ")[0].split(" ")[0] + "%");
                        ResultSet rs = ps.executeQuery();
                        double spent = rs.next() ? rs.getDouble(1) : 0;
                        double lim   = limits[i];
                        totalAnggaran += lim;
                        totalSpent    += spent;
                        boolean over  = spent > lim;
                        if (over) peringatan++;
                        final double fSpent = spent; final double fLim = lim;
                        final boolean fOver = over; final int idx = i;
                        katRows.add(buildKategoriRow(icons[idx], names[idx], subs[idx], fSpent, fLim, colors[idx], fOver));
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }

                // Recent 5 transactions
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String q = "SELECT p.tanggal, k.nama, k.icon, p.jumlah " +
                        "FROM pengeluaran p LEFT JOIN kategori k ON p.kategori_id=k.id " +
                        "WHERE p.user_id=? ORDER BY p.tanggal DESC, p.id DESC LIMIT 5";
                    PreparedStatement ps = conn.prepareStatement(q);
                    ps.setInt(1, userId);
                    ResultSet rs = ps.executeQuery();
                    DecimalFormat df2 = new DecimalFormat("###,###,###", sym);
                    while (rs.next()) {
                        String tgl  = rs.getString("tanggal"); if (tgl==null) tgl="-";
                        String kNam = rs.getString("nama");    if (kNam==null) kNam="Lainnya";
                        String kIc  = rs.getString("icon");    if (kIc==null)  kIc="ðŸ“¦";
                        double jml  = rs.getDouble("jumlah");

                        JPanel rRow = new JPanel(new GridLayout(1, 4, 0, 0));
                        rRow.setOpaque(false);
                        rRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                        rRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241,245,249)));

                        JLabel lTgl  = new JLabel(tgl);
                        lTgl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        lTgl.setForeground(UIConstants.TEXT_PRIMARY);
                        lTgl.setBorder(new EmptyBorder(10, 0, 10, 0));

                        JLabel lKat  = new JLabel(kIc + " " + kNam);
                        lKat.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
                        lKat.setForeground(UIConstants.TEXT_PRIMARY);
                        lKat.setBorder(new EmptyBorder(10, 0, 10, 0));

                        JLabel lStat = new JLabel("â— Pengeluaran");
                        lStat.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        lStat.setForeground(new Color(239,68,68));
                        lStat.setBorder(new EmptyBorder(10, 0, 10, 0));

                        JLabel lJml  = new JLabel("-Rp" + df2.format(jml));
                        lJml.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        lJml.setForeground(new Color(239,68,68));
                        lJml.setHorizontalAlignment(SwingConstants.RIGHT);
                        lJml.setBorder(new EmptyBorder(10, 0, 10, 0));

                        rRow.add(lTgl); rRow.add(lKat); rRow.add(lStat); rRow.add(lJml);
                        recentRows.add(rRow);
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }

                savingUsed = 0;
                return null;
            }

            @Override
            protected void done() {
                DecimalFormatSymbols sym = new DecimalFormatSymbols(new Locale("id","ID"));
                DecimalFormat df = new DecimalFormat("###,###,###", sym);

                agTotalAnggaran.setText("Rp" + df.format(totalAnggaran));
                agTotalPengeluaran.setText("Rp" + df.format(totalSpent));
                agSisaSaldo.setText("Rp" + df.format(Math.max(0, totalAnggaran - totalSpent)));
                agPeringatan.setText(String.format("%02d", peringatan));

                agKategoriPanel.removeAll();
                for (JPanel r : katRows) agKategoriPanel.add(r);
                agKategoriPanel.revalidate(); agKategoriPanel.repaint();

                agRecentPanel.removeAll();
                for (JPanel r : recentRows) agRecentPanel.add(r);
                agRecentPanel.revalidate(); agRecentPanel.repaint();

                double savGoal = 0;
                int savPct = savGoal > 0 ? (int) Math.min(100, savingUsed / savGoal * 100) : 0;
                agSavingUsed.setText("Rp" + df.format(savingUsed));
                agSavingBar.setValue(savPct);
            }
        };
        worker.execute();
    }

    private void setActiveMenuItem(SidebarMenuItem activeItem) {
        itemDashboard.setActive(itemDashboard == activeItem);
        itemTransaksi.setActive(itemTransaksi == activeItem);
        itemAnggaran.setActive(itemAnggaran == activeItem);
        itemLaporan.setActive(itemLaporan == activeItem);
        itemPengaturan.setActive(itemPengaturan == activeItem);
        itemBantuan.setActive(itemBantuan == activeItem);
    }

    // ==========================================
    // TRANSAKSI UI BUILD & METHODS
    // ==========================================
    private void buildTransaksiUI() {
        JPanel transaksiContainer = new JPanel();
        transaksiContainer.setOpaque(false);
        transaksiContainer.setLayout(new GridBagLayout());
        
        JScrollPane transaksiScrollPane = new JScrollPane(transaksiContainer);
        transaksiScrollPane.setBorder(null);
        transaksiScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        transaksiScrollPane.setBackground(new Color(248, 250, 252));
        
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.fill = GridBagConstraints.HORIZONTAL;
        tgbc.weightx = 1.0;
        tgbc.gridx = 0;
        tgbc.gridy = 0;
        tgbc.insets = new Insets(24, 30, 0, 30);
        
        // 1. Header Section
        JPanel tHeaderPanel = new JPanel(new BorderLayout());
        tHeaderPanel.setOpaque(false);
        
        JPanel tHeaderLeft = new JPanel();
        tHeaderLeft.setOpaque(false);
        tHeaderLeft.setLayout(new BoxLayout(tHeaderLeft, BoxLayout.Y_AXIS));
        
        JLabel lblTTitle = new JLabel("Riwayat Transaksi");
        lblTTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTTitle.setForeground(UIConstants.TEXT_PRIMARY);
        tHeaderLeft.add(lblTTitle);
        tHeaderLeft.add(Box.createVerticalStrut(4));
        
        JLabel lblTSubtitle = new JLabel("Analisis pola pengeluaran Anda dan kelola arus kas Anda.");
        lblTSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTSubtitle.setForeground(UIConstants.TEXT_SECONDARY);
        tHeaderLeft.add(lblTSubtitle);
        tHeaderPanel.add(tHeaderLeft, BorderLayout.WEST);
        
        JPanel tHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        tHeaderRight.setOpaque(false);
        
        tStatSaldo = new MiniStatCard("Saldo Bersih", "Rp0", new Color(30, 41, 59), false);
        tStatPengeluaran = new MiniStatCard("Pengeluaran Bulanan", "Rp0", new Color(239, 68, 68), true);
        tHeaderRight.add(tStatSaldo);
        tHeaderRight.add(tStatPengeluaran);
        tHeaderPanel.add(tHeaderRight, BorderLayout.EAST);
        
        transaksiContainer.add(tHeaderPanel, tgbc);
        
        // 2. Filters Section
        tgbc.gridy++;
        tgbc.insets = new Insets(20, 30, 0, 30);
        
        JPanel filterCard = new JPanel() {
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
        filterCard.setOpaque(false);
        filterCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        filterCard.setLayout(new GridBagLayout());
        
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.gridy = 0;
        fc.insets = new Insets(0, 10, 0, 10);
        
        // Date
        JPanel pnlDate = new JPanel(new BorderLayout(0, 6));
        pnlDate.setOpaque(false);
        JLabel lblDateTitle = new JLabel("Rentang Tanggal");
        lblDateTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDateTitle.setForeground(UIConstants.TEXT_SECONDARY);
        pnlDate.add(lblDateTitle, BorderLayout.NORTH);
        
        tComboTanggal = new JComboBox<>(new String[]{
            "ðŸ“…  Bulan Ini",
            "ðŸ“…  30 Hari Terakhir",
            "ðŸ“…  7 Hari Terakhir",
            "ðŸ“…  Kustom..."
        });
        tComboTanggal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tComboTanggal.setBackground(Color.WHITE);
        tComboTanggal.setPreferredSize(new Dimension(200, 36));
        pnlDate.add(tComboTanggal, BorderLayout.CENTER);
        
        fc.gridx = 0;
        fc.weightx = 0.33;
        filterCard.add(pnlDate, fc);
        
        // Category
        JPanel pnlKat = new JPanel(new BorderLayout(0, 6));
        pnlKat.setOpaque(false);
        JLabel lblKatTitle = new JLabel("Kategori");
        lblKatTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKatTitle.setForeground(UIConstants.TEXT_SECONDARY);
        pnlKat.add(lblKatTitle, BorderLayout.NORTH);
        
        tComboKategori = new JComboBox<>();
        tComboKategori.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tComboKategori.setBackground(Color.WHITE);
        tComboKategori.setPreferredSize(new Dimension(200, 36));
        tComboKategori.addActionListener(e -> refreshTransaksiTableData());
        pnlKat.add(tComboKategori, BorderLayout.CENTER);
        
        fc.gridx = 1;
        fc.weightx = 0.33;
        filterCard.add(pnlKat, fc);
        
        // Type
        JPanel pnlJenis = new JPanel(new BorderLayout(0, 6));
        pnlJenis.setOpaque(false);
        JLabel lblJenisTitle = new JLabel("Jenis");
        lblJenisTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblJenisTitle.setForeground(UIConstants.TEXT_SECONDARY);
        pnlJenis.add(lblJenisTitle, BorderLayout.NORTH);
        
        JPanel segmentPanel = new JPanel(new GridLayout(1, 3));
        segmentPanel.setOpaque(false);
        segmentPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        segmentPanel.setPreferredSize(new Dimension(280, 36));
        
        btnJenisSemua = new JButton("Semua");
        btnJenisPemasukan = new JButton("Pemasukan");
        btnJenisPengeluaran = new JButton("Pengeluaran");
        
        styleSegmentButton(btnJenisSemua, true);
        styleSegmentButton(btnJenisPemasukan, false);
        styleSegmentButton(btnJenisPengeluaran, false);
        
        btnJenisSemua.addActionListener(e -> selectJenis("Semua"));
        btnJenisPemasukan.addActionListener(e -> selectJenis("Pemasukan"));
        btnJenisPengeluaran.addActionListener(e -> selectJenis("Pengeluaran"));
        
        segmentPanel.add(btnJenisSemua);
        segmentPanel.add(btnJenisPemasukan);
        segmentPanel.add(btnJenisPengeluaran);
        pnlJenis.add(segmentPanel, BorderLayout.CENTER);
        
        fc.gridx = 2;
        fc.weightx = 0.34;
        filterCard.add(pnlJenis, fc);
        
        // Advanced Filters row
        fc.gridy = 1;
        fc.gridx = 0;
        fc.gridwidth = 3;
        fc.weightx = 1.0;
        fc.insets = new Insets(15, 10, 0, 10);
        
        JPanel pnlFilterActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilterActions.setOpaque(false);
        
        JButton btnAdvancedFilter = new JButton("ðŸ”  Filter Lanjutan");
        styleSecondaryButton(btnAdvancedFilter);
        
        JButton btnExportCSV = new JButton("ðŸ“¥  Ekspor CSV");
        styleSecondaryButton(btnExportCSV);
        btnExportCSV.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Data transaksi berhasil diekspor ke CSV!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });
        
        pnlFilterActions.add(btnAdvancedFilter);
        pnlFilterActions.add(btnExportCSV);
        
        filterCard.add(pnlFilterActions, fc);
        transaksiContainer.add(filterCard, tgbc);
        
        // 3. Table Card
        tgbc.gridy++;
        tgbc.insets = new Insets(20, 30, 0, 30);
        
        JPanel tableCard = new JPanel() {
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
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableCard.setLayout(new BorderLayout());
        
        tTableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "TANGGAL", "KATEGORI", "DESKRIPSI", "JUMLAH", "AKSI"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tTable = new JTable(tTableModel);
        tTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tTable.setRowHeight(48);
        tTable.setShowGrid(false);
        tTable.setIntercellSpacing(new Dimension(0, 0));
        tTable.setBackground(Color.WHITE);
        tTable.setSelectionBackground(new Color(241, 245, 249));
        tTable.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        tTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths and hide ID column
        tTable.getColumnModel().getColumn(0).setMinWidth(0);
        tTable.getColumnModel().getColumn(0).setMaxWidth(0);
        tTable.getColumnModel().getColumn(0).setWidth(0);
        tTable.getColumnModel().getColumn(1).setPreferredWidth(120); // TANGGAL
        tTable.getColumnModel().getColumn(2).setPreferredWidth(180); // KATEGORI
        tTable.getColumnModel().getColumn(3).setPreferredWidth(300); // DESKRIPSI
        tTable.getColumnModel().getColumn(4).setPreferredWidth(150); // JUMLAH
        tTable.getColumnModel().getColumn(5).setPreferredWidth(120); // AKSI

        JTableHeader header = tTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(71, 85, 105));
        header.setBackground(new Color(248, 250, 252));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBorder(new EmptyBorder(0, 20, 0, 20));
                if (column == 4) {
                    lbl.setHorizontalAlignment(JLabel.RIGHT);
                } else if (column == 5) {
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    lbl.setHorizontalAlignment(JLabel.LEFT);
                }
                lbl.setBackground(new Color(248, 250, 252));
                return lbl;
            }
        };
        header.setDefaultRenderer(headerRenderer);
        
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel lbl = (JLabel) c;
                lbl.setBorder(new EmptyBorder(0, 20, 0, 20));
                lbl.setBackground(isSelected ? new Color(241, 245, 249) : Color.WHITE);
                lbl.setForeground(UIConstants.TEXT_PRIMARY);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (column == 4) { // JUMLAH
                    lbl.setHorizontalAlignment(JLabel.RIGHT);
                    String valStr = value != null ? value.toString() : "";
                    if (valStr.startsWith("+")) {
                        lbl.setForeground(new Color(16, 185, 129));
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else if (valStr.startsWith("-")) {
                        lbl.setForeground(new Color(239, 68, 68));
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    }
                } else if (column == 5) { // AKSI
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                    lbl.setForeground(UIConstants.PRIMARY_BLUE);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    lbl.setHorizontalAlignment(JLabel.LEFT);
                }
                return lbl;
            }
        };
        tTable.setDefaultRenderer(Object.class, cellRenderer);

        // Add mouse listener to open action popup menu
        tTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tTable.rowAtPoint(e.getPoint());
                int col = tTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    int id = (int) tTable.getValueAt(row, 0);
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem menuEdit = new JMenuItem("âœï¸  Edit Transaksi");
                    JMenuItem menuDelete = new JMenuItem("ðŸ—‘ï¸  Hapus Transaksi");
                    
                    menuEdit.addActionListener(ae -> openEditTransactionDialog(id));
                    menuDelete.addActionListener(ae -> deleteTransaction(id));
                    
                    popup.add(menuEdit);
                    popup.add(menuDelete);
                    popup.show(tTable, e.getX(), e.getY());
                }
            }
        });
        
        JScrollPane tTableScroll = new JScrollPane(tTable);
        tTableScroll.setBorder(null);
        tTableScroll.setViewportBorder(null);
        tTableScroll.getViewport().setBackground(Color.WHITE);
        tTableScroll.setPreferredSize(new Dimension(0, 320));
        
        tableCard.add(tTableScroll, BorderLayout.CENTER);
        
        // Footer pagination
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(15, 15, 10, 15));
        
        lblPaginationInfo = new JLabel("Menampilkan 1-10 dari 10 transaksi");
        lblPaginationInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPaginationInfo.setForeground(UIConstants.TEXT_SECONDARY);
        pnlFooter.add(lblPaginationInfo, BorderLayout.WEST);
        
        JPanel pnlPages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlPages.setOpaque(false);
        
        JButton btnPrev = new JButton("<");
        JButton btnPage1 = new JButton("1");
        JButton btnPage2 = new JButton("2");
        JButton btnPage3 = new JButton("3");
        JButton btnNext = new JButton(">");
        
        stylePageButton(btnPrev, false);
        stylePageButton(btnPage1, true);
        stylePageButton(btnPage2, false);
        stylePageButton(btnPage3, false);
        stylePageButton(btnNext, false);
        
        pnlPages.add(btnPrev);
        pnlPages.add(btnPage1);
        pnlPages.add(btnPage2);
        pnlPages.add(btnPage3);
        pnlPages.add(btnNext);
        
        pnlFooter.add(pnlPages, BorderLayout.EAST);
        tableCard.add(pnlFooter, BorderLayout.SOUTH);
        
        transaksiContainer.add(tableCard, tgbc);
        
        // 4. Bottom Row
        tgbc.gridy++;
        tgbc.insets = new Insets(20, 30, 30, 30);
        
        JPanel tBottomPanel = new JPanel(new GridBagLayout());
        tBottomPanel.setOpaque(false);
        
        GridBagConstraints tbmgbc = new GridBagConstraints();
        tbmgbc.fill = GridBagConstraints.BOTH;
        tbmgbc.gridy = 0;
        tbmgbc.weighty = 1.0;
        
        // Chart Card
        tbmgbc.gridx = 0;
        tbmgbc.weightx = 0.65;
        tbmgbc.insets = new Insets(0, 0, 0, 10);
        
        JPanel chartCard = new JPanel() {
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
        chartCard.setOpaque(false);
        chartCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        chartCard.setLayout(new BorderLayout());
        chartCard.setPreferredSize(new Dimension(0, 260));
        
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        JLabel lblChartTitle = new JLabel("Rincian Pengeluaran");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChartTitle.setForeground(UIConstants.TEXT_PRIMARY);
        chartHeader.add(lblChartTitle, BorderLayout.WEST);
        
        JPanel chartLegend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        chartLegend.setOpaque(false);
        chartLegend.add(createLegendItem("Operasional", UIConstants.PRIMARY_BLUE));
        chartLegend.add(createLegendItem("Pemasaran", new Color(16, 185, 129)));
        chartLegend.add(createLegendItem("Gaji", new Color(245, 158, 11)));
        chartHeader.add(chartLegend, BorderLayout.EAST);
        
        chartCard.add(chartHeader, BorderLayout.NORTH);
        
        tDetailChart = new DetailBarChart();
        chartCard.add(tDetailChart, BorderLayout.CENTER);
        
        tBottomPanel.add(chartCard, tbmgbc);
        
        // Export Card
        tbmgbc.gridx = 1;
        tbmgbc.weightx = 0.35;
        tbmgbc.insets = new Insets(0, 10, 0, 0);
        
        JPanel exportSummaryCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        exportSummaryCard.setOpaque(false);
        exportSummaryCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        exportSummaryCard.setLayout(new GridBagLayout());
        
        GridBagConstraints esgbc = new GridBagConstraints();
        esgbc.fill = GridBagConstraints.HORIZONTAL;
        esgbc.weightx = 1.0;
        esgbc.gridx = 0;
        esgbc.gridy = 0;
        
        JLabel lblEsTitle = new JLabel("Ekspor Ringkasan");
        lblEsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEsTitle.setForeground(Color.WHITE);
        exportSummaryCard.add(lblEsTitle, esgbc);
        
        esgbc.gridy++;
        esgbc.insets = new Insets(10, 0, 0, 0);
        JLabel lblEsDesc = new JLabel("<html>Buat laporan audit keuangan komprehensif untuk persiapan pajak Q4 Anda.</html>");
        lblEsDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEsDesc.setForeground(new Color(148, 163, 184));
        exportSummaryCard.add(lblEsDesc, esgbc);
        
        esgbc.gridy++;
        esgbc.weighty = 1.0;
        exportSummaryCard.add(Box.createVerticalStrut(10), esgbc);
        
        esgbc.gridy++;
        esgbc.weighty = 0.0;
        esgbc.insets = new Insets(15, 0, 0, 0);
        RoundedButton btnUnduh = new RoundedButton("Unduh Laporan PDF");
        btnUnduh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUnduh.setBackground(UIConstants.PRIMARY_BLUE);
        btnUnduh.setForeground(Color.WHITE);
        btnUnduh.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Laporan PDF berhasil dibuat dan diunduh!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });
        exportSummaryCard.add(btnUnduh, esgbc);
        
        tBottomPanel.add(exportSummaryCard, tbmgbc);
        
        transaksiContainer.add(tBottomPanel, tgbc);
        
        cardPanel.add(transaksiScrollPane, "Transaksi");
    }

    private void styleSegmentButton(JButton btn, boolean isActive) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(null);
        if (isActive) {
            btn.setBackground(UIConstants.PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UIConstants.TEXT_SECONDARY);
        }
    }

    private void selectJenis(String jenis) {
        selectedJenis = jenis;
        styleSegmentButton(btnJenisSemua, jenis.equals("Semua"));
        styleSegmentButton(btnJenisPemasukan, jenis.equals("Pemasukan"));
        styleSegmentButton(btnJenisPengeluaran, jenis.equals("Pengeluaran"));
        
        refreshTransaksiTableData();
    }

    private void stylePageButton(JButton btn, boolean isActive) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(32, 32));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        if (isActive) {
            btn.setBackground(UIConstants.PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UIConstants.TEXT_SECONDARY);
        }
    }

    private static JPanel createLegendItem(String labelText, Color color) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnl.setOpaque(false);
        
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setOpaque(false);
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        
        pnl.add(dot);
        pnl.add(lbl);
        return pnl;
    }

    public void refreshTransaksiPage() {
        tComboKategori.removeAllItems();
        tComboKategori.addItem("Semua Kategori");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nama FROM kategori ORDER BY nama ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tComboKategori.addItem(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        refreshTransaksiTableData();
    }

    private void refreshTransaksiTableData() {
        SwingWorker<TransaksiData, Void> worker = new SwingWorker<TransaksiData, Void>() {
            @Override
            protected TransaksiData doInBackground() throws Exception {
                TransaksiData data = new TransaksiData();
                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;

                try {
                    conn = DatabaseConnection.getConnection();
                    
                    String sumQuery = "SELECT SUM(jumlah) FROM pengeluaran WHERE user_id = ?";
                    stmt = conn.prepareStatement(sumQuery);
                    stmt.setInt(1, userId);
                    rs = stmt.executeQuery();
                    if (rs.next()) {
                        data.totalExpenses = rs.getDouble(1);
                    }
                    rs.close();
                    stmt.close();
                    
                    StringBuilder querySb = new StringBuilder(
                        "SELECT p.id, p.judul, k.nama AS kat_nama, k.icon AS kat_icon, p.tanggal, p.jumlah " +
                        "FROM pengeluaran p " +
                        "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                        "WHERE p.user_id = ?"
                    );
                    
                    String selectedKat = (String) tComboKategori.getSelectedItem();
                    if (selectedKat != null && !selectedKat.equals("Semua Kategori")) {
                        querySb.append(" AND k.nama = ?");
                    }
                    
                    String searchVal = searchField != null ? searchField.getText().trim() : "";
                    if (!searchVal.isEmpty() && !searchVal.equals("Cari transaksi, aset...")) {
                        querySb.append(" AND p.judul LIKE ?");
                    }
                    
                    querySb.append(" ORDER BY p.tanggal DESC, p.id DESC");
                    
                    stmt = conn.prepareStatement(querySb.toString());
                    int paramIdx = 1;
                    stmt.setInt(paramIdx++, userId);
                    
                    if (selectedKat != null && !selectedKat.equals("Semua Kategori")) {
                        stmt.setString(paramIdx++, selectedKat);
                    }
                    
                    if (!searchVal.isEmpty() && !searchVal.equals("Cari transaksi, aset...")) {
                        stmt.setString(paramIdx++, "%" + searchVal + "%");
                    }
                    
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        TransactionItem item = new TransactionItem();
                        item.id = rs.getInt("id");
                        item.judul = rs.getString("judul");
                        item.kategoriNama = rs.getString("kat_nama");
                        item.kategoriIcon = rs.getString("kat_icon");
                        item.tanggal = rs.getDate("tanggal");
                        item.jumlah = rs.getDouble("jumlah");
                        
                        if (item.kategoriIcon == null) item.kategoriIcon = "ðŸ“¦";
                        if (item.kategoriNama == null) item.kategoriNama = "Lainnya";
                        
                        data.expenses.add(item);
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
                    TransaksiData data = get();
                    
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
                    DecimalFormat df = new DecimalFormat("###,###,###", symbols);
                    
                    double saldoBersih = 0;
                    tStatSaldo.setValue("Rp" + df.format(saldoBersih));
                    tStatPengeluaran.setValue("- Rp" + df.format(data.totalExpenses));
                    
                    tTableModel.setRowCount(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                    
                    ArrayList<Object[]> rows = new ArrayList<>();
                    
                    if (selectedJenis.equals("Semua") || selectedJenis.equals("Pengeluaran")) {
                        for (TransactionItem tr : data.expenses) {
                            rows.add(new Object[]{
                                tr.id,
                                sdf.format(tr.tanggal),
                                tr.kategoriIcon + " " + tr.kategoriNama,
                                tr.judul,
                                "-Rp" + df.format(tr.jumlah),
                                "âœï¸ Edit / ðŸ—‘ï¸ Hapus"
                            });
                        }
                    }
                    
                    for (Object[] row : rows) {
                        tTableModel.addRow(row);
                    }
                    
                    lblPaginationInfo.setText("Menampilkan 1-" + rows.size() + " dari " + rows.size() + " transaksi");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private static class TransaksiData {
        double totalExpenses = 0;
        ArrayList<TransactionItem> expenses = new ArrayList<>();
    }

    // ==========================================
    // INNER CLASSES: SIDEBAR MENU ITEM, MINI STAT CARD, DETAIL BAR CHART
    // ==========================================
    private class SidebarMenuItem extends JPanel {
        private final String title;
        private final String iconText;
        private boolean active;
        private final JLabel label;
        private final JLabel iconLabel;

        public SidebarMenuItem(String title, String iconText, boolean active) {
            this.title = title;
            this.iconText = iconText;
            this.active = active;
            setOpaque(false);
            setLayout(new BorderLayout(15, 0));
            setPreferredSize(new Dimension(240, 44));
            setMaximumSize(new Dimension(240, 44));
            setBorder(new EmptyBorder(0, 20, 0, 10));

            iconLabel = new JLabel(iconText, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(active ? UIConstants.PRIMARY_BLUE : new Color(30, 41, 59));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            iconLabel.setPreferredSize(new Dimension(26, 26));
            iconLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 13));
            iconLabel.setForeground(Color.WHITE);
            add(iconLabel, BorderLayout.WEST);

            label = new JLabel(title);
            label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            label.setForeground(active ? Color.WHITE : new Color(148, 163, 184));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        label.setForeground(Color.WHITE);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        label.setForeground(new Color(148, 163, 184));
                        repaint();
                    }
                }
                
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleMenuClick(title);
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
            label.setForeground(active ? Color.WHITE : new Color(148, 163, 184));
            iconLabel.repaint();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (active) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59));
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                
                g2.setColor(UIConstants.PRIMARY_BLUE);
                g2.fillRoundRect(8, 8, 4, getHeight() - 16, 2, 2);
                g2.dispose();
            }
        }
    }

    private static class MiniStatCard extends JPanel {
        private final JLabel lblVal;
        
        public MiniStatCard(String titleText, String valueText, Color valueColor, boolean isExpense) {
            setOpaque(false);
            setPreferredSize(new Dimension(220, 75));
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 16, 12, 16));
            
            JLabel lblTitle = new JLabel(titleText);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblTitle.setForeground(new Color(100, 116, 139));
            add(lblTitle, BorderLayout.NORTH);
            
            lblVal = new JLabel(valueText);
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblVal.setForeground(valueColor);
            add(lblVal, BorderLayout.CENTER);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
        
        public void setValue(String valueText) {
            lblVal.setText(valueText);
        }
    }

    private static class DetailBarChart extends JComponent {
        private final double[] values = { 65.0, 85.0, 30.0, 70.0, 95.0, 45.0 };
        private final String[] labels = { "MEI", "JUN", "JUL", "AGU", "SEP", "OKT" };
        
        public DetailBarChart() {
            setPreferredSize(new Dimension(0, 180));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            int paddingBottom = 25;
            int paddingTop = 15;
            int chartHeight = height - paddingBottom - paddingTop;
            
            int barCount = values.length;
            int barWidth = 32;
            int gap = (width - (barCount * barWidth)) / (barCount + 1);
            
            for (int i = 0; i < barCount; i++) {
                int x = gap + i * (barWidth + gap);
                int y = paddingTop;
                
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(x, y, barWidth, chartHeight, 8, 8);
                
                double percentage = values[i] / 100.0;
                int filledHeight = (int) (chartHeight * percentage);
                int filledY = y + (chartHeight - filledHeight);
                
                g2.setColor(UIConstants.PRIMARY_BLUE);
                g2.fillRoundRect(x, filledY, barWidth, filledHeight, 8, 8);
                
                g2.setColor(UIConstants.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                String label = labels[i];
                int labelX = x + (barWidth - fm.stringWidth(label)) / 2;
                int labelY = height - 6;
                g2.drawString(label, labelX, labelY);
            }
            g2.dispose();
        }
    }
    // ==========================================
    // TRANSACTION EDIT & DELETE ACTIONS
    // ==========================================
    private void openEditTransactionDialog(int transactionId) {
        EditTransactionDialog dialog = new EditTransactionDialog(this, transactionId);
        dialog.setVisible(true);
    }

    private void deleteTransaction(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus transaksi ini?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM pengeluaran WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, 
                    "Transaksi berhasil dihapus!", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                refreshDashboardData();
                refreshTransaksiPage();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Gagal menghapus transaksi: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class EditTransactionDialog extends JDialog {
        private final int transactionId;
        private final DashboardForm parent;

        private RoundedTextField txtJudul;
        private RoundedTextField txtJumlah;
        private JComboBox<KategoriComboItem> comboKategori;
        private JSpinner spinnerTanggal;
        private JTextArea txtCatatan;
        private RoundedButton btnSave;

        public EditTransactionDialog(DashboardForm parent, int transactionId) {
            super(parent, "Edit Transaksi", true);
            this.parent = parent;
            this.transactionId = transactionId;

            setSize(450, 520);
            setLocationRelativeTo(parent);
            setResizable(false);

            buildUI();
            loadCategories();
            loadTransactionData();
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

            JLabel title = new JLabel("Edit Transaksi");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(UIConstants.TEXT_PRIMARY);
            container.add(title, gbc);

            gbc.gridy++;
            container.add(createLabel("Deskripsi / Judul Transaksi"), gbc);
            gbc.gridy++;
            txtJudul = new RoundedTextField("Masukkan judul pengeluaran", null);
            container.add(txtJudul, gbc);

            gbc.gridy++;
            container.add(createLabel("Jumlah Pengeluaran (Rp)"), gbc);
            gbc.gridy++;
            txtJumlah = new RoundedTextField("Masukkan jumlah uang", null);
            container.add(txtJumlah, gbc);

            gbc.gridy++;
            container.add(createLabel("Kategori"), gbc);
            gbc.gridy++;
            comboKategori = new JComboBox<>();
            comboKategori.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            comboKategori.setBackground(Color.WHITE);
            comboKategori.setPreferredSize(new Dimension(0, 40));
            container.add(comboKategori, gbc);

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

            gbc.gridy++;
            gbc.insets = new Insets(10, 0, 0, 0);
            btnSave = new RoundedButton("Simpan Perubahan");
            btnSave.addActionListener(e -> updateTransaction());
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
                    if (icon == null) icon = "ðŸ“¦";
                    comboKategori.addItem(new KategoriComboItem(id, name, icon));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void loadTransactionData() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT judul, jumlah, kategori_id, tanggal, catatan FROM pengeluaran WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, transactionId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    txtJudul.setText(rs.getString("judul"));
                    txtJumlah.setText(String.valueOf(rs.getDouble("jumlah")));
                    int katId = rs.getInt("kategori_id");
                    Date tanggal = rs.getDate("tanggal");
                    String catatan = rs.getString("catatan");
                    
                    if (catatan != null) {
                        txtCatatan.setText(catatan);
                    }
                    if (tanggal != null) {
                        spinnerTanggal.setValue(tanggal);
                    }
                    
                    for (int i = 0; i < comboKategori.getItemCount(); i++) {
                        KategoriComboItem item = comboKategori.getItemAt(i);
                        if (item.id == katId) {
                            comboKategori.setSelectedItem(item);
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void updateTransaction() {
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
                String query = "UPDATE pengeluaran SET kategori_id = ?, judul = ?, jumlah = ?, tanggal = ?, catatan = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, item.id);
                stmt.setString(2, judul);
                stmt.setDouble(3, jumlah);
                stmt.setDate(4, new java.sql.Date(date.getTime()));
                stmt.setString(5, catatan.isEmpty() ? null : catatan);
                stmt.setInt(6, transactionId);

                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Transaksi pengeluaran berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                parent.refreshDashboardData();
                parent.refreshTransaksiPage();
                dispose();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memperbarui transaksi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                btnSave.setEnabled(true);
                btnSave.setText("Simpan Perubahan");
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
