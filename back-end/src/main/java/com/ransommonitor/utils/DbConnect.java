package com.ransommonitor.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnect {
    private static HikariDataSource dataSource;
    private static final String CONFIG_FILE = "config.properties";
    static {
        try {
            System.out.println("Initializing database connection...");

            Class.forName("org.postgresql.Driver");
            Properties p = new Properties();
            InputStream file = DbConnect.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (file == null) {
                System.out.println("Error: config.properties file not found on classpath.");
            } else {
                System.out.println("config.properties file loaded successfully.");
            }

            p.load(file);
            file.close();

            String jdbcUrl = p.getProperty("db.url");
            String dbUser = p.getProperty("db.user");
            String dbPassword = p.getProperty("db.password");

            // Configure HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setDriverClassName("org.postgresql.Driver");

            dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load database configuration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error loading database configuration", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing database connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
