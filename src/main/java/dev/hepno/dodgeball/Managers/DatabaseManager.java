package dev.hepno.dodgeball.Managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String HOST = "172.18.0.1";
    private final int PORT = 3306;
    private final String DATABASE = "s5_rer";
    private final String USERNAME = "u5_FVX2O15bxQ";
    private final String PASSWORD = "BrVw0m@e^CLYN88!K!yLwK8t";

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
