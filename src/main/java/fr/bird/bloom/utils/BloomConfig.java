package fr.bird.bloom.utils;

import java.util.ResourceBundle;

public class BloomConfig {

    private static String resourcePath;
    private static String directoryPath = null; // TODO find a better name

    private BloomConfig() {
        // private default constructor to prevent instantiation
    }

    public static String getResourcePath() {
        if (resourcePath == null) {
            resourcePath = BloomConfig.class.getClassLoader().getResource(BloomConfig.getProperty("resource.folder")).getPath();
        }
        return resourcePath;
    }

    public static String getDirectoryPath() {
        return directoryPath;
    }

    public static void initializeDirectoryPath(String value) {
        if (directoryPath != null) {
            throw new IllegalStateException("directoryPath has already by initialized once, cannot override it !");
        }
        directoryPath = value;
    }

    // properties file
    public static ResourceBundle bundleConf = ResourceBundle.getBundle("bloom");

    public static String getProperty(String key) {
        return bundleConf.getString(key);
    }
}
