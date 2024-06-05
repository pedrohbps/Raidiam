package com.raidiamproject.automation.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDataBase {
    private static ConnectionDataBase instance;
    private Connection connection;
    private String url = EnvironmentProperties.getValue("dbUrl");
    private String username = EnvironmentProperties.getValue("dbUsername");
    private String password = EnvironmentProperties.getValue("dbPassword");

    private ConnectionDataBase() {
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static synchronized ConnectionDataBase getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new ConnectionDataBase();
        }

        return instance;
    }
}

