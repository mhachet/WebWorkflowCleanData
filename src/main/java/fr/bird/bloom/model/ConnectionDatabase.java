/**
 * @author mhachet
 */
package fr.bird.bloom.model;

import fr.bird.bloom.utils.BloomConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * 
 * src.model
 * 
 * ConnectionDatabase.java
 */
public class ConnectionDatabase {
	private static String url = "";
	private static String user = "";
	private static String password = "";
	private static Connection connexion;
	
	/**
	 * 
	 * src.model
	 * ConnectionDatabase
	 */
	public ConnectionDatabase(){

	}

	/**
	 * Create an instance to connect on the database if not exist
	 *  
	 * @return Connection
	 */
	public static Connection getInstance(){
		if(connexion == null){
			try {
				//System.out.println(getUrl() + "  " + getUser() + "  " + getPassword());
				try {
					Class.forName( "com.mysql.jdbc.Driver" );
				} catch ( ClassNotFoundException e ) {
					System.err.println("Erreur lors du chargement : le driver n'a pas été trouvé dans le classpath ! <br/>"
							+ e.getMessage() );
				}

				String url = BloomConfig.getProperty("db.url");
				String user = BloomConfig.getProperty("db.user");
				String password = BloomConfig.getProperty("db.password");
				connexion = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				System.err.println("ERREUR DE CONNEXION : " + e.getMessage());
			}
		}
		//executeSQLcommand(choiceStatement, sqlCommand);
		
		return connexion;	
	}


	public static String getUser() {
		return user;
	}

	public static void setUser(String newUser) {
		user = newUser;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String newPassword) {
		password = newPassword;
	}

	public static String getUrl() {
		return url;
	}

	public static void setUrl(String url) {
		ConnectionDatabase.url = url;
	}
	

}
