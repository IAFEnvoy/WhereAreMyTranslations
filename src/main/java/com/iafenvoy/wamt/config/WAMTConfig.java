package com.iafenvoy.wamt.config;

import com.iafenvoy.wamt.WhereAreMyTranslations;

public final class WAMTConfig {
    public static final String PATH = "./config/" + WhereAreMyTranslations.MOD_ID + "/config.json";
    public static final WAMTConfig INSTANCE = ConfigLoader.load(WAMTConfig.class, PATH, new WAMTConfig());

    public boolean logMissingKeys = false;
}
