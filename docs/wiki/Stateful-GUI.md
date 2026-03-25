# Stateful GUI

A reactive GUI that automatically re-renders when its state changes. Uses Kotlin property delegates for seamless state management.

## Basic Usage

```kotlin
val gui = KGui.stateful(plugin, "<gold>Click Counter", rows = 3) {
    var clicks by state("clicks", 0)
    var multiplier by state("multiplier", 1)

    border(Material.ORANGE_STAINED_GLASS_PANE) { name(" ") }

    render { player ->
        setSlot(GuiSlot(
            index = 13,
            item = ItemBuilder(Material.DIAMOND)
                .name("<aqua>Clicks: <white>$clicks")
                .amount(maxOf(1, minOf(64, clicks)))
                .build(),
            onClick = { _, _ ->
                clicks += multiplier
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            }
        ))

        setSlot(GuiSlot(
            index = 11,
            item = ItemBuilder(Material.RED_WOOL)
                .name("<red>Reset")
                .lore("<gray>Set counter to 0")
                .build(),
            onClick = { _, _ -> clicks = 0 }
        ))

        setSlot(GuiSlot(
            index = 15,
            item = ItemBuilder(Material.GOLD_INGOT)
                .name("<gold>Multiplier: x$multiplier")
                .lore("<gray>Click to cycle: 1 → 5 → 10 → 1")
                .build(),
            onClick = { _, _ ->
                multiplier = when (multiplier) {
                    1 -> 5; 5 -> 10; else -> 1
                }
            }
        ))
    }
}
gui.open(player)
```

## How State Works

1. **`state(key, initial)`** creates a Kotlin property delegate backed by a concurrent map
2. When the delegated property is **set** (e.g., `clicks += 1`), the GUI automatically:
   - Updates the state value
   - Notifies all state change listeners
   - Calls `update()` to re-render for all viewers
3. The **`render { player -> }`** block runs on every update, rebuilding slots from current state

## State Methods

### `state(key, initial)`

Create a delegated state property:

```kotlin
var count by state("count", 0)        // Int state
var name by state("name", "Steve")     // String state
var enabled by state("enabled", true)  // Boolean state
```

### `render(block)`

Define the render function that rebuilds slots from current state. Called on every state change:

```kotlin
render { player ->
    // Use state values to build dynamic slots
    setSlot(GuiSlot(index = 13, item = buildItemFromState()))
}
```

### Programmatic State Access

```kotlin
val gui: StatefulGui = KGui.stateful(plugin, "Title", 3) { ... }

// Read state
val value: Int? = gui.getState<Int>("count")

// Write state (triggers re-render)
gui.setState("count", 42)

// Listen for state changes
gui.onStateChange("count") { old, new ->
    println("Count changed from $old to $new")
}
```
