package com.ransommonitor.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DbConfig {

    private static DbConfig instance;
    private static Connection connection;
    private static final String CONFIG_FILE = "config.properties";

    private DbConfig() {
        try {
            Class.forName("org.postgresql.Driver");
            Properties properties = new Properties();
            InputStream inputStream = DbConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if(inputStream != null) {
                properties.load(inputStream);
                System.out.println("Loading config file");

                connection = DriverManager.getConnection(
                        properties.getProperty("db.url"),
                        properties.getProperty("db.users"),
                        properties.getProperty("db.password"));
            }
            else {
                System.out.println("Config file not found");
            }
//            ??????????????????????????????????????????????????????????????????
            assert inputStream != null;
            inputStream.close();

        } catch (ClassNotFoundException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DbConfig getInstance() {
        if(instance == null) {
            instance = new DbConfig();
        }
        return instance;
    }

    public static PreparedStatement getPs(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            getInstance();
            return getPs(sql);
        }
    }

}
