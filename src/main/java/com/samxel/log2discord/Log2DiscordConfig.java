package com.samxel.log2discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Log2DiscordConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/log2discord.json");

    public String webhookUrl = "";

    public static Log2DiscordConfig load() {
        if (!CONFIG_FILE.exists()) {
            Log2DiscordConfig c = new Log2DiscordConfig();
            c.save();
            return c;
        }
        try (FileReader r = new FileReader(CONFIG_FILE)) {
            return GSON.fromJson(r, Log2DiscordConfig.class);
        } catch (IOException e) {
            Log2Discord.LOGGER.error("Failed to load config", e);
            return new Log2DiscordConfig();
        }
    }

    public void save() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, w);
            }
        } catch (IOException e) {
            Log2Discord.LOGGER.error("Failed to save config", e);
        }
    }
}