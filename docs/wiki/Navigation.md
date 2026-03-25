# Navigation

Stack-based GUI navigation history — enables back/forward traversal between menus.

## Basic Usage

```kotlin
// Navigate forward (pushes current GUI onto history stack)
KGui.navigate(player, shopGui)

// Go back to previous GUI
KGui.back(player) // Returns true if successful, false if no history

// Clear navigation history
KGui.clearHistory(player)
```

## Multi-Level Navigation Example

```kotlin
// Main Menu → Category → Item Details → Back → Back → Main Menu

// In Main Menu:
slot(11) {
    item(Material.DIAMOND_SWORD) { name("<gold>Weapons") }
    onClick { player ->
        val categoryGui = buildCategoryGui(plugin, "weapons")
        KGui.navigate(player, categoryGui)
    }
}

// In Category GUI:
slot(13) {
    item(Material.DIAMOND_SWORD) { name("<gold>Excalibur") }
    onClick { player ->
        val detailGui = buildDetailGui(plugin, "excalibur")
        KGui.navigate(player, detailGui)
    }
}

// In any GUI — back button:
slot(49) {
    item(Material.ARROW) { name("<gray>← Back") }
    onClick { player ->
        if (!KGui.back(player)) {
            player.closeInventory() // No history — close
        }
    }
}
```

## Navigator API

| Method | Description |
|--------|-------------|
| `KGui.navigate(player, gui)` | Open a new GUI, pushing the current one onto the history stack |
| `KGui.back(player): Boolean` | Go back to the previous GUI. Returns `false` if no history |
| `KGui.clearHistory(player)` | Clear the player's navigation stack |

### Direct Navigator Access

```kotlin
import club.skidware.kgui.navigation.Navigator

Navigator.navigate(player, gui)
Navigator.back(player)
Navigator.clear(player)
Navigator.hasHistory(player)  // Check if there's history
Navigator.getDepth(player)    // Get stack depth
```

> **Note:** Navigation history is cleared when the server restarts. It's stored in memory per-player using UUIDs.
