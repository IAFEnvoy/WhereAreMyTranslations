package com.iafenvoy.wamt;

import com.iafenvoy.wamt.util.CopyOnWriteHashMap;
import com.iafenvoy.wamt.util.MapComparator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public final class KeyRecorder {
    public static final List<String> MOD_IDS = Platform.gatherAllModIds();
    private static final int SAVE_DELAY = 1000;
    //Map<MOD_ID, Map<LANGUAGE, List<KEY>>>
    private static final Map<String, Map<String, Set<String>>> MISSING_KEYS = new CopyOnWriteHashMap<>();
    private static long LAST_MODIFY_TIME = 0;
    private static boolean MODIFIED = false;

    public static void reload() {
        MISSING_KEYS.clear();
    }

    public static void recordAllMissingKeys(String key) {
        saveKey("en_us", key);
    }

    public static void recordUnchanged(Map<String, String> previous, Map<String, String> current, String language) {
        MapComparator.findUnchangedKeys(previous, current).forEach(key -> saveKey(language, key));
    }

    private static void saveKey(String language, String key) {
        MISSING_KEYS.computeIfAbsent(resolveModIds(key), s -> new CopyOnWriteHashMap<>()).computeIfAbsent(language, s -> new CopyOnWriteArraySet<>()).add(key);
        WhereAreMyTranslations.LOGGER.debug("Missing translate key: {}", key);
        LAST_MODIFY_TIME = System.currentTimeMillis();
        MODIFIED = true;
    }

    private static String resolveModIds(String key) {
        return MOD_IDS.stream().filter(key::contains).findAny().orElse(".");
    }

    private static String getSavePath(String modId, String language) {
        return String.format("./config/%s/%s/%s.json", WhereAreMyTranslations.MOD_ID, modId, language);
    }

    private static void runSaveTask() {
        if (MODIFIED && System.currentTimeMillis() - LAST_MODIFY_TIME >= SAVE_DELAY) {
            WhereAreMyTranslations.LOGGER.debug("Start saving file");
            MODIFIED = false;
            for (Map.Entry<String, Map<String, Set<String>>> modId : MISSING_KEYS.entrySet())
                for (Map.Entry<String, Set<String>> language : modId.getValue().entrySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    boolean first = true;
                    for (String key : language.getValue()) {
                        if (!first) sb.append(",");
                        sb.append("\n    \"").append(key).append("\":\"\"");
                        first = false;
                    }
                    sb.append("\n}");
                    String path = getSavePath(modId.getKey(), language.getKey());
                    try {
                        FileUtils.write(new File(path), sb.toString(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        WhereAreMyTranslations.LOGGER.error("Failed to save file {}", path, e);
                    }
                }
            WhereAreMyTranslations.LOGGER.debug("Complete saving file");
        }
    }

    static {
        new Thread(() -> {
            while (true)
                try {
                    runSaveTask();
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (Exception e) {
                    WhereAreMyTranslations.LOGGER.error("Error occurred when saving missing language keys.", e);
                }
        }, "Missing Language Keys Saver").start();
    }
}
