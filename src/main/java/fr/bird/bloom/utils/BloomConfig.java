package fr.bird.bloom.utils;

import java.util.ResourceBundle;

public class BloomConfig {

    private static String resourcePath;
    private static String directoryPath = null; // TODO find a better name
    private static boolean directoryPathInitialized = false;
    private static String outsideFolder;

    // properties file
    public final static ResourceBundle bundleConf = ResourceBundle.getBundle("bloom");

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
        if (directoryPathInitialized) {
            throw new IllegalStateException("directoryPath has already by initialized once, cannot override it !");
        }
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException("directoryPath cannot be initialized with a null/empty value !");
        }
        directoryPath = value + getProperty("directory.folder.name");
        //directoryPath = getProperty("directory.folder.name");
        directoryPathInitialized = true;
    }

    public static String getProperty(String key) {
        return bundleConf.getString(key);
    }
}
