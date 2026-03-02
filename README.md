# Command Keybind (Fabric)

Bind commands to keybinds. Press a key, run a command. Simple.

**Fabric 1.21.3 port of [commandKeybind](https://github.com/K0Mi/commandKeybind)** — the original Forge 1.16.4 version.

## What's new in the Fabric version

- **Configurable commands** — no more hardcoded values. Edit `config/commandkeybind.json`.
- **5 command slots** — each independently rebindable in the vanilla Controls menu.
- **Hot-reload** — edit the config file while playing; changes take effect on the next key press.
- **Client-only** — no server-side mod needed.

## Setup

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.3
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Drop the mod jar into `.minecraft/mods/`
4. Launch the game

## Configuration

On first launch, the mod creates `config/commandkeybind.json`:

```json
{
  "slots": {
    "1": "/backpack",
    "2": "",
    "3": "",
    "4": "",
    "5": ""
  }
}
```

- **Slot 1** defaults to `/backpack` (matching the original mod).
- Set any slot to a command (e.g. `/home`, `/spawn`, `/tpa SomePlayer`).
- Plain text (no `/`) is sent as chat.
- Leave a slot empty (`""`) to disable it.

## Default Keybinds

| Slot | Default Key | Change in... |
|------|-------------|--------------|
| 1    | R           | Options → Controls → Command Keybinds |
| 2    | G           | " |
| 3    | H           | " |
| 4    | J           | " |
| 5    | K           | " |

## Building

Requires **JDK 21+**. First time setup — generate the Gradle wrapper:

```bash
# If you have Gradle installed globally:
gradle wrapper --gradle-version 8.10

# Then build:
./gradlew build
```

Or grab the wrapper files from the [Fabric example mod](https://github.com/FabricMC/fabric-example-mod) (`gradle/` directory + `gradlew` + `gradlew.bat`).

Output jar: `build/libs/command-keybind-1.0.0.jar`

## License

MIT
