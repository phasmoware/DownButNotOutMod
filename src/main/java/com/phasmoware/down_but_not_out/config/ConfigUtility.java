package com.phasmoware.down_but_not_out.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.phasmoware.down_but_not_out.DownButNotOut;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigUtility {

    private static final String CONF_DIR = "DownButNotOut";
    private static final String CONF_FILE = "config.json5";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path GLOBAL_CONF_PATH = FabricLoader.getInstance().getConfigDir();

    private static File requestConfigFile() throws IOException {
        Path directoryPath = GLOBAL_CONF_PATH.resolve(CONF_DIR);
        Files.createDirectories(directoryPath);
        Path filePath = directoryPath.resolve(CONF_FILE);
        File file = directoryPath.resolve(CONF_FILE).toFile();
        if (!file.exists()) {
            Files.createFile(filePath);
            saveConfig(filePath, new DefaultModConfig());
            file = directoryPath.resolve(CONF_FILE).toFile();
        }
        return file;
    }

    public static ModConfig loadConfig() {
        try {
            File file = requestConfigFile();
            return GSON.fromJson(Files.readString(file.toPath()),  ModConfig.class);
        } catch (IOException e) {
            DownButNotOut.LOGGER.error("Config file request failed!", e);
            throw new RuntimeException(e);
        }
    }

    private static void saveConfig(Path filePath, DefaultModConfig config) {
        try {
            Files.writeString(filePath, GSON.toJson(config));
        } catch (IOException e) {
            DownButNotOut.LOGGER.error("Saving default config file failed!", e);
        }
    }
}
