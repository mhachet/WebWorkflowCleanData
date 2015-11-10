package fr.bird.bloom.utils;

import java.io.File;

public class BloomUtils {

    public static void createDirectory(String path) {
        final boolean created = new File(path).mkdirs();
        if (!created) {
            throw new IllegalStateException("Could'nt create directory at path: " + path);
        }
    }
}
