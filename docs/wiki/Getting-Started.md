# Getting Started

## Setup

Initialize KGui in your plugin's `onEnable()`:

```kotlin
import club.skidware.kgui.KGui

class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        KGui.setup(this)
    }
}
```

> **Note:** `KGui.setup()` is idempotent — calling it multiple times is safe. It registers the internal `GuiListener` only once. If you skip `setup()`, it will be called automatically when you create your first GUI.

## Creating Your First GUI

```kotlin
val menu = KGui.chest(plugin, "<dark_purple>Main Menu", rows = 3) {
    // Fill border with glass panes
    border(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }

    // Add a clickable item at slot 13 (center)
    slot(13) {
        item(Material.COMPASS) {
            name("<yellow>Server Info")
            lore(
                "<gray>Click to see server info",
                "<dark_gray>Players online: <white>${Bukkit.getOnlinePlayers().size}"
            )
        }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player ->
            player.sendMessage("Server TPS: ${Bukkit.getTPS()[0]}")
        }
    }

    // Dynamic item that shows different content per player
    slot(15) {
        dynamicItem { player ->
            ItemBuilder(Material.PLAYER_HEAD)
                .skullOwner(player)
                .name("<green>${player.name}")
                .lore("<gray>Your profile")
                .build()
        }
    }

    onOpen { player -> player.sendMessage("Menu opened!") }
    onClose { player -> player.sendMessage("Menu closed!") }
}

// Open the GUI for a player
menu.open(player)
```

## GUI Lifecycle

| Method | Description |
|--------|-------------|
| `gui.open(player)` | Opens the GUI for the player |
| `gui.close(player)` | Closes the GUI and triggers `onClose` |
| `gui.update()` | Re-renders the GUI for all viewers |
| `gui.update(player)` | Re-renders the GUI for a specific player |
| `gui.getViewers()` | Returns the set of players currently viewing this GUI |

## Available GUI Types

| Factory Method | Description | Rows |
|---------------|-------------|------|
| `KGui.chest()` | Standard inventory menu | 1-6 |
| `KGui.paginated()` | Multi-page content browser | 2-6 |
| `KGui.scrollable()` | Scrollable content area | 2-6 |
| `KGui.stateful()` | Reactive GUI with state management | 1-6 |
| `KGui.anvil()` | Text input via anvil rename | N/A |
| `KGui.sign()` | Text input via sign editing | N/A |
| `KGui.hopper()` | Small 5-slot menu | N/A |
| `KGui.dispenser()` | Small 9-slot menu | N/A |
| `KGui.merchant()` | Villager trade interface | N/A |

## Slot Numbering

Inventory slots are numbered left-to-right, top-to-bottom:

```
Row 1:  0  1  2  3  4  5  6  7  8
Row 2:  9 10 11 12 13 14 15 16 17
Row 3: 18 19 20 21 22 23 24 25 26
Row 4: 27 28 29 30 31 32 33 34 35
Row 5: 36 37 38 39 40 41 42 43 44
Row 6: 45 46 47 48 49 50 51 52 53
```
