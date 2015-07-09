package com.twitter.config.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import org.apache.maven.shared.utils.StringUtils;

/**
 * 
 * @author agupta13
 * 
 */
public class ConfigLoaderUtils {

    /**
     * get resource file abolute path
     * 
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    public static String getResourceFilePath(String fileName) throws FileNotFoundException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("Invalid resource file name=" + fileName);
        }
        URL fileUrl = ConfigLoaderUtils.class.getClassLoader().getResource(fileName);
        if (fileUrl == null) {
            throw new FileNotFoundException("Resource file name=" + fileName + " not found");
        }
        File resourceFile = new File(fileUrl.getPath());
        if (!resourceFile.exists()) {
            throw new FileNotFoundException("Resource file name=" + fileName + " not found");
        }
        return resourceFile.getAbsolutePath();
    }
}
