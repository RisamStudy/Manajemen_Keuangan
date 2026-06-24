package com.fintrackpro.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class untuk mengelola koneksi database MySQL.
 * Pastikan MySQL sudah terinstall dan database sudah dibuat.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/fintrack_pro";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // Sesuaikan dengan password MySQL Anda

    /**
     * Mendapatkan koneksi ke database MySQL.
     * @return Connection object
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan mysql-connector-java ada di classpath.", e);
        }
    }

    /**
     * Menutup koneksi database.
     */
    public static void closeConnection() {
        // No-op karena koneksi ditutup secara mandiri oleh pemanggil lewat try-with-resources
    }
}
