# FinTrack Pro - Sistem Manajemen Pengeluaran Keuangan

Aplikasi desktop untuk mengelola pengeluaran keuangan pribadi/bisnis, dibangun dengan Java Swing dan MySQL.

## 📋 Persyaratan Sistem

- **Java JDK** 8 atau lebih baru
- **Apache NetBeans** IDE
- **MySQL Server** 5.7+ atau MariaDB 10.3+
- **MySQL Connector/J** (JDBC Driver)

## 🚀 Cara Setup

### 1. Setup Database

Jalankan script SQL di MySQL:

```sql
source d:/kumpulanCodingan/Manajemen_Pengeluaran/database/setup.sql
```

Atau buka file `database/setup.sql` di MySQL Workbench / phpMyAdmin dan execute.

### 2. Konfigurasi Database

Edit file `src/com/fintrackpro/util/DatabaseConnection.java` jika diperlukan:

```java
private static final String URL = "jdbc:mysql://localhost:3306/fintrack_pro";
private static final String USER = "root";
private static final String PASSWORD = "";  // Sesuaikan password MySQL Anda
```

### 3. Tambahkan MySQL Connector

1. Download [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
2. Di NetBeans: **Project Properties > Libraries > Add JAR/Folder**
3. Pilih file `mysql-connector-j-x.x.x.jar`

### 4. Buka Project di NetBeans

1. Buka NetBeans
2. **File > Open Project** → pilih folder `Manajemen_Pengeluaran`
3. Atau buat project baru **Java Application** dan salin folder `src` ke dalamnya

### 5. Jalankan Aplikasi

Klik kanan `Main.java` → **Run File**

## 🔑 Akun Demo

| Email | Password |
|-------|----------|
| admin@fintrackpro.com | admin123 |
| demo@perusahaan.com | demo123 |

## 📁 Struktur Project

```
Manajemen_Pengeluaran/
├── database/
│   └── setup.sql                           # Script setup database
├── src/
│   └── com/fintrackpro/
│       ├── Main.java                       # Entry point aplikasi
│       ├── ui/
│       │   ├── LoginForm.java              # Form login
│       │   └── components/
│       │       ├── GradientPanel.java      # Panel gradient background
│       │       ├── ModernCheckBox.java     # Custom checkbox
│       │       ├── RoundedButton.java      # Custom button
│       │       ├── RoundedPasswordField.java # Custom password field
│       │       └── RoundedTextField.java   # Custom text field
│       └── util/
│           ├── DatabaseConnection.java     # Koneksi MySQL
│           └── UIConstants.java            # Design system constants
└── README.md
```

## 🎨 Fitur Tampilan Login

- ✅ Gradient background biru-putih
- ✅ Card dengan shadow effect
- ✅ Custom rounded text fields dengan icon
- ✅ Toggle visibility password
- ✅ Animasi hover & focus pada komponen
- ✅ Shake animation saat error
- ✅ Validasi email
- ✅ Loading state pada tombol login
- ✅ Responsive layout
