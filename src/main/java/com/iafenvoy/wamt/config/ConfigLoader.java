package com.iafenvoy.wamt.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iafenvoy.wamt.WhereAreMyTranslations;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T load(Class<T> clazz, String path, T defaultValue) {
        try {
            FileInputStream stream = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(stream);
            return GSON.fromJson(reader, clazz);
        } catch (FileNotFoundException e) {
            WhereAreMyTranslations.LOGGER.error("Failed to read config", e);
            try {
                FileUtils.write(new File(path), GSON.toJson(defaultValue), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                WhereAreMyTranslations.LOGGER.error("Failed to create config", ex);
            }
            return defaultValue;
        }
    }
}
