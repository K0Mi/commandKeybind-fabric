package com.nek036.commandkeybind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command Keybind — Fabric edition.
 *
 * Registers up to 5 configurable keybind slots. Each slot can be mapped
 * to an arbitrary chat command (or plain chat message) via a JSON config
 * file at {@code config/commandkeybind.json}.
 *
 * <p>Improvements over the original Forge 1.16.4 version:
 * <ul>
 *   <li>Commands are loaded from a config file, not hardcoded.</li>
 *   <li>Multiple slots (5 by default) — rebindable in vanilla Controls menu.</li>
 *   <li>Hot-reload: edit the config, press a slot key, and the new command fires immediately.</li>
 *   <li>Client-only — no server-side component needed.</li>
 * </ul>
 */
public class CommandKeybindClient implements ClientModInitializer {

    public static final String MOD_ID = "commandkeybind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Number of command slots available. */
    private static final int SLOT_COUNT = 5;

    /** Default keys for slots 1-5 (R, G, H, J, K). Rebindable in Controls. */
    private static final int[] DEFAULT_KEYS = {
        GLFW.GLFW_KEY_R,
        GLFW.GLFW_KEY_G,
        GLFW.GLFW_KEY_H,
        GLFW.GLFW_KEY_J,
        GLFW.GLFW_KEY_K
    };

    private final KeyBinding[] keyBindings = new KeyBinding[SLOT_COUNT];
    private CommandKeybindConfig config;

    @Override
    public void onInitializeClient() {
        config = CommandKeybindConfig.load();

        // Register keybindings for each slot.
        for (int i = 0; i < SLOT_COUNT; i++) {
            keyBindings[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + MOD_ID + ".slot" + (i + 1),
                InputUtil.Type.KEYSYM,
                DEFAULT_KEYS[i],
                "category." + MOD_ID + ".keys"
            ));
        }

        // Listen for key presses every client tick.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (client.currentScreen != null) return; // don't fire while GUI is open

            for (int i = 0; i < SLOT_COUNT; i++) {
                while (keyBindings[i].wasPressed()) {
                    String command = config.getCommand(i);
                    if (command == null || command.isBlank()) {
                        LOGGER.warn("[CKB] Slot {} has no command configured — edit config/commandkeybind.json", i + 1);
                        continue;
                    }
                    executeCommand(command);
                }
            }
        });

        LOGGER.info("[CKB] Command Keybind loaded — {} slots registered", SLOT_COUNT);
    }

    /**
     * Sends the command/message as the local player.
     * If the string starts with '/', it's sent as a command.
     * Otherwise it's sent as plain chat.
     */
    private void executeCommand(String command) {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null) return;

        LOGGER.info("[CKB] Executing: {}", command);

        if (command.startsWith("/")) {
            // sendCommand expects the command WITHOUT the leading slash.
            client.player.networkHandler.sendCommand(command.substring(1));
        } else {
            client.player.networkHandler.sendChatMessage(command);
        }
    }
}
