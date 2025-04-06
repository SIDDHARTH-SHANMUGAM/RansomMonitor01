package com.ransommonitor.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnect {
    private static final Logger logger = Logger.getLogger(DbConnect.class.getName());
    private static HikariDataSource dataSource;
    private static final String CONFIG_FILE = "config.properties";

    static {
        try {
            logger.info("Initializing database connection...");

            Class.forName("org.postgresql.Driver");

            Properties p = new Properties();
            InputStream file = DbConnect.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (file == null) {
                logger.severe("config.properties file not found on classpath.");
            } else {
                logger.info("config.properties file loaded successfully.");
                p.load(file);
                file.close();
            }

            String jdbcUrl = p.getProperty("db.url");
            String dbUser = p.getProperty("db.user");
            String dbPassword = p.getProperty("db.password");

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
            logger.info("Database connection pool initialized successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load database configuration", e);
            throw new RuntimeException("Error loading database configuration", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing database connection pool", e);
            throw new RuntimeException("Error initializing database connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        logger.fine("Fetching database connection from pool.");
        return dataSource.getConnection();
    }
}
