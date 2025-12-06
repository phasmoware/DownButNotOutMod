package com.phasmoware.down_but_not_out.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.phasmoware.down_but_not_out.util.Constants;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigUtility {

    private static final String CONF_DIR = "DownButNotOut";
    private static final String CONF_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path GLOBAL_CONF_PATH = FabricLoader.getInstance().getConfigDir();
    public static final String SYNTAX_ERROR_MSG = "Error: Config file has syntax errors! Please make sure it is valid json or delete it to revert to defaults!";
    public static final String FILE_REQUEST_FAILED_MSG = "Error: Config file request failed! Please check config filepath permissions!";
    public static final String SAVE_CONFIG_FAILED_MSG = "Error: Writing to config file failed! Please check permissions!";

    private static File requestConfigFile() throws IOException {
        Path directoryPath = GLOBAL_CONF_PATH.resolve(CONF_DIR);
        Files.createDirectories(directoryPath);
        Path filePath = directoryPath.resolve(CONF_FILE);
        File file = directoryPath.resolve(CONF_FILE).toFile();
        if (!file.exists()) {
            Files.createFile(filePath);
            saveConfig(filePath, new ModConfig());
            file = directoryPath.resolve(CONF_FILE).toFile();
        }
        return file;
    }

    public static ModConfig loadConfig() {
        try {
            File file = requestConfigFile();
            return GSON.fromJson(Files.readString(file.toPath()), ModConfig.class);
        } catch (JsonSyntaxException je) {
            Constants.LOGGER.error(SYNTAX_ERROR_MSG, je);
            throw new RuntimeException(je);
        } catch (IOException e) {
            Constants.LOGGER.error(FILE_REQUEST_FAILED_MSG, e);
            throw new RuntimeException(e);
        }
    }

    private static void saveConfig(Path filePath, ModConfig config) {
        try {
            Files.writeString(filePath, GSON.toJson(config));
        } catch (IOException e) {
            Constants.LOGGER.error(SAVE_CONFIG_FAILED_MSG, e);
            throw new RuntimeException(e);
        }
    }
}
