package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection conn;

    private DBConnection() {
        try {
            
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            
            String dbUrl = "jdbc:ucanaccess://ParkWiseDB.accdb";
            
            this.conn = DriverManager.getConnection(dbUrl);
            System.out.println(">> Database connection established.");
            
        } catch (Exception e) {
            System.err.println(">> Error connecting to DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        } else {
            try {
                if (instance.getConnection() == null || instance.getConnection().isClosed()) {
                    instance = new DBConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return this.conn;
    }
}