package com.example.heroicorganizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApiKeyProvider {
    private static final String LOCAL_PROPERTIES_FILE = "local.properties";

    public static String getApiKey() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("local.properties")) {
            properties.load(fis);
            return properties.getProperty("comicvine.api_key");
        } catch (IOException e) {
            e.printStackTrace();
            return null; // If key retrieval fails, it could cause issues
        }
    }

}
