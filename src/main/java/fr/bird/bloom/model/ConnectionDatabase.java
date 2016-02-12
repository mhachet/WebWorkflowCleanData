/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import fr.bird.bloom.utils.BloomConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * src.model
 * <p>
 * ConnectionDatabase.java
 */
public class ConnectionDatabase {
    private static Connection connexion;

    /**
     * src.model
     * ConnectionDatabase
     */
    private ConnectionDatabase() {
        // private default constructor to prevent instantiation
    }

    /**
     * Create an instance to connect on the database if not exist
     *
     * @return Connection
     */
    public static Connection getConnection() {
        try {
            if (connexion == null || connexion.isClosed()) {
                try {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error during loading : driver couldn't be found in the classpath ! <br/>"
                                + e.getMessage());
                    }
                    String url = BloomConfig.getProperty("db.url");
                    String user = BloomConfig.getProperty("db.user");
                    String password = BloomConfig.getProperty("db.password");

                    connexion = DriverManager.getConnection(url, user, password);

                } catch (SQLException e) {
                    System.err.println("CONNECTION ERROR : " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connexion;
    }


}
