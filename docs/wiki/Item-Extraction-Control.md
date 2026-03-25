# Item Extraction Control

Control whether players can take items from your GUI inventories.

## The Problem

By default in Bukkit, players can take items from any inventory. KGui prevents this by default, but gives you fine-grained control over which items can be extracted.

## Three Levels of Control

### 1. GUI Level: `interactable`

Controls the default extraction behavior for the entire GUI.

```kotlin
// Default: nothing can be taken
KGui.chest(plugin, "Locked Menu", rows = 3) {
    // interactable defaults to false
    slot(13) { item(Material.DIAMOND) { name("<red>Can't take this") } }
}

// Everything can be taken
KGui.chest(plugin, "Open Menu", rows = 3) {
    interactable(true)
    slot(13) { item(Material.DIAMOND) { name("<green>Take me!") } }
}
```

### 2. Slot Level: `takeable`

Overrides the GUI-level setting for a specific slot.

```kotlin
KGui.chest(plugin, "Mixed Menu", rows = 3) {
    // GUI is locked (default)

    slot(11) {
        item(Material.DIAMOND) { name("<red>Locked") }
        // takeable not set → follows GUI default (false) → can't take
    }

    slot(13) {
        item(Material.GOLDEN_APPLE) { name("<green>Take me!") }
        takeable(true) // Override: CAN take despite GUI being locked
    }

    slot(15) {
        item(Material.BARRIER) { name("<red>Never") }
        takeable(false) // Explicitly locked (redundant here, but explicit)
    }
}
```

### 3. Mixed: Open GUI with Locked Slots

```kotlin
KGui.chest(plugin, "Reward Chest", rows = 3) {
    interactable(true) // Players can take items by default

    // Border items — can't take these
    border(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }
    // Border slots automatically get takeable = null → follows GUI (true)
    // But we want to lock them:

    slot(13) {
        item(Material.DIAMOND) { name("<green>Your reward!") }
        takeable(true) // Can take
    }

    // Lock all border slots
    slots(listOf(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26,9,17)) { _ ->
        item(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }
        takeable(false) // Can't take border items
    }
}
```

## Resolution Logic

```
Is slot.takeable explicitly set?
├── Yes, true  → Player CAN take the item
├── Yes, false → Player CANNOT take the item
└── No (null)  → Check gui.interactable
    ├── true   → Player CAN take the item
    └── false  → Player CANNOT take the item (DEFAULT)
```

| `gui.interactable` | `slot.takeable` | Result |
|---------------------|-----------------|--------|
| `false` (default) | `null` (default) | **Cannot take** |
| `false` | `true` | **Can take** |
| `false` | `false` | Cannot take |
| `true` | `null` | Can take |
| `true` | `true` | Can take |
| `true` | `false` | **Cannot take** |

## Drag Prevention

KGui also prevents item dragging across GUI slots. If any involved slot is non-takeable, the entire drag operation is cancelled.
