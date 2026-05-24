package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    public static Connection getConnection() {
        
        Properties config = new Properties();

          try {
            config.load(new FileInputStream("config.properties"));
          } catch (IOException e) {
            System.err.println("Config file not found:" + e.getMessage());
            return null;
        }

        String url = config.getProperty("db.url");
        String user = config.getProperty("db.user");
        String password = config.getProperty("db.password");

        try {
           

            Connection conn = DriverManager.getConnection(url, user, password); 
            if (conn != null) {
                System.out.println("Sucessfully connected to database.");
                
            }

            return conn; 

        } catch (SQLException e) {
            System.out.println("Connection failed:" + e.getMessage());
            return null;
            
        }
    
    }
}






