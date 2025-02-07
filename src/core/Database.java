package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // singleton design pattern
    private Connection connection = null;
    private static  Database instance = null;
    private final String DB_URL = "jdbc:mysql://localhost:3306/customermanager";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "stajyer123.";
    private Database(){
        try {
             this.connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static Connection getInstance(){
        try {
            if (instance == null || instance.getConnection().isClosed()){
                instance = new Database();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        instance = new Database();
        return instance.getConnection();
    }
}
