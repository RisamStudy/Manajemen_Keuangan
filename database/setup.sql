-- ============================================================
-- FinTrack Pro - Database Setup Script
-- Sistem Manajemen Pengeluaran Keuangan
-- ============================================================

-- Buat database
CREATE DATABASE IF NOT EXISTS fintrack_pro
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE fintrack_pro;

-- ============================================================
-- Tabel Users - Menyimpan data pengguna
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    foto_profil VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ============================================================
-- Tabel Kategori - Kategori pengeluaran
-- ============================================================
CREATE TABLE IF NOT EXISTS kategori (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    warna VARCHAR(7) DEFAULT '#1864CD',
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Tabel Pengeluaran - Catatan pengeluaran
-- ============================================================
CREATE TABLE IF NOT EXISTS pengeluaran (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    kategori_id INT,
    judul VARCHAR(200) NOT NULL,
    jumlah DECIMAL(15, 2) NOT NULL,
    tanggal DATE NOT NULL,
    catatan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (kategori_id) REFERENCES kategori(id) ON DELETE SET NULL,
    INDEX idx_user_tanggal (user_id, tanggal)
) ENGINE=InnoDB;

-- ============================================================
-- Data Awal - User demo untuk testing
-- ============================================================
INSERT INTO users (nama, email, password) VALUES
('Admin FinTrack', 'admin@fintrackpro.com', 'admin123'),
('Demo User', 'demo@perusahaan.com', 'demo123');

-- Kategori default
INSERT INTO kategori (nama, icon, warna, user_id) VALUES
('Makanan & Minuman', '🍽️', '#EF4444', 1),
('Transportasi', '🚗', '#F59E0B', 1),
('Belanja', '🛒', '#8B5CF6', 1),
('Tagihan & Utilitas', '💡', '#3B82F6', 1),
('Kesehatan', '🏥', '#10B981', 1),
('Hiburan', '🎬', '#EC4899', 1),
('Pendidikan', '📚', '#6366F1', 1),
('Lainnya', '📦', '#6B7280', 1);

-- Data transaksi sengaja dikosongkan agar angka awal aplikasi bernilai 0.
DELETE FROM pengeluaran
WHERE user_id = 1
  AND (
    (judul = 'Makan siang kantor' AND jumlah = 35000.00)
    OR (judul = 'Bensin motor' AND jumlah = 50000.00)
    OR (judul = 'Listrik bulan ini' AND jumlah = 350000.00)
    OR (judul = 'Belanja bulanan' AND jumlah = 500000.00)
  );

SELECT '✅ Database fintrack_pro berhasil dibuat!' AS status;
