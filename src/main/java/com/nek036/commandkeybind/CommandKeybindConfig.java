package com.nek036.commandkeybind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple JSON-based config for command-to-slot mappings.
 *
 * <p>Config file location: {@code .minecraft/config/commandkeybind.json}
 *
 * <p>Example:
 * <pre>{@code
 * {
 *   "slots": {
 *     "1": "/backpack",
 *     "2": "/home",
 *     "3": "/spawn",
 *     "4": "",
 *     "5": ""
 *   }
 * }
 * }</pre>
 *
 * The config is read every time a slot is pressed, so edits take
 * effect immediately without restarting.
 */
public class CommandKeybindConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
        FabricLoader.getInstance().getConfigDir().resolve("commandkeybind.json");

    private final Map<Integer, String> slots = new LinkedHashMap<>();

    private CommandKeybindConfig() {}

    /**
     * Loads or creates the config file and returns a config instance.
     */
    public static CommandKeybindConfig load() {
        CommandKeybindConfig config = new CommandKeybindConfig();

        if (Files.exists(CONFIG_PATH)) {
            config.read();
        } else {
            // Seed with a sensible default (matching the original mod).
            config.slots.put(0, "/backpack");
            for (int i = 1; i < 5; i++) {
                config.slots.put(i, "");
            }
            config.write();
            CommandKeybindClient.LOGGER.info("[CKB] Created default config at {}", CONFIG_PATH);
        }

        return config;
    }

    /**
     * Returns the command for the given slot index (0-based).
     * Re-reads from disk each time so edits are picked up immediately.
     */
    public String getCommand(int slotIndex) {
        read(); // hot-reload on every access
        return slots.getOrDefault(slotIndex, "");
    }

    private void read() {
        try {
            String json = Files.readString(CONFIG_PATH);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject slotsObj = root.getAsJsonObject("slots");
            if (slotsObj == null) return;

            slots.clear();
            for (var entry : slotsObj.entrySet()) {
                try {
                    int idx = Integer.parseInt(entry.getKey()) - 1; // config is 1-indexed
                    slots.put(idx, entry.getValue().getAsString());
                } catch (NumberFormatException e) {
                    CommandKeybindClient.LOGGER.warn("[CKB] Ignoring non-numeric slot key: {}", entry.getKey());
                }
            }
        } catch (IOException e) {
            CommandKeybindClient.LOGGER.error("[CKB] Failed to read config", e);
        }
    }

    private void write() {
        try {
            JsonObject root = new JsonObject();
            JsonObject slotsObj = new JsonObject();
            for (var entry : slots.entrySet()) {
                slotsObj.addProperty(String.valueOf(entry.getKey() + 1), entry.getValue()); // 1-indexed in file
            }
            root.add("slots", slotsObj);
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException e) {
            CommandKeybindClient.LOGGER.error("[CKB] Failed to write config", e);
        }
    }
}
