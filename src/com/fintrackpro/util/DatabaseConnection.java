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

    private static Connection connection;

    /**
     * Mendapatkan koneksi ke database MySQL.
     * @return Connection object
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan mysql-connector-java ada di classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Menutup koneksi database.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
