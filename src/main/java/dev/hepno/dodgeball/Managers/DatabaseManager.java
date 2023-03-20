package dev.hepno.dodgeball.Managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String HOST = "eu02-sql.pebblehost.com";
    private final int PORT = 3306;
    private final String DATABASE = "customer_460243_emailverify";
    private final String USERNAME = "customer_460243_emailverify";
    private final String PASSWORD = "bU@TWJf4uWX0Ue5bYsE2";

    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
    }

    public void disconnect() throws SQLException {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() { return connection; }

    // Getters
    public boolean isConnected() { return connection != null; }

}
