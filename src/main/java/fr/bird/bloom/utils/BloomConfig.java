package fr.bird.bloom.utils;

import java.util.ResourceBundle;

public class BloomConfig {

	private BloomConfig(){
		// private default constructor to prevent instantiation
	}

	// properties file
	public static ResourceBundle bundleConf = ResourceBundle.getBundle("bloom");

	public static String getProperty(String key){
		return bundleConf.getString(key);
	}
}
