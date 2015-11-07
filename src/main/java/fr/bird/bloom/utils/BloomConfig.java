package fr.bird.bloom.utils;

import java.util.ResourceBundle;

public class BloomConfig {

    private static String resourcePath;

    private BloomConfig() {
        // private default constructor to prevent instantiation
    }

    public static String getResourcePath() {
        if (resourcePath == null) {
            resourcePath = BloomConfig.class.getClassLoader().getResource(BloomConfig.getProperty("resource.folder")).getPath();
        }
        return resourcePath;
    }

    // properties file
    public static ResourceBundle bundleConf = ResourceBundle.getBundle("bloom");

    public static String getProperty(String key) {
        return bundleConf.getString(key);
    }
}
