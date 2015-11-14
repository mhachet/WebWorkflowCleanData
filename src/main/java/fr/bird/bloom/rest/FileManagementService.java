package fr.bird.bloom.rest;

import fr.bird.bloom.utils.BloomConfig;
import fr.bird.bloom.utils.BloomException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileManagementService {

    public static File storeInputFile(String fileUrl, String uuid) {
        try {
            final File destination = new File(BloomConfig.getDirectoryPath() + "temp/input_" + uuid + ".csv");
            FileUtils.copyURLToFile(new URL(fileUrl), destination);
            return destination;
        } catch (IOException e) {
            if (e instanceof MalformedURLException) {
                throw new BloomException(e.getMessage(), e);
            }
            throw new BloomException(e.getMessage(), e);
        }
    }

}
