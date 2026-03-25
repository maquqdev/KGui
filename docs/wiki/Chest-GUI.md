# Chest GUI

The most common GUI type — a standard chest inventory with 1-6 rows (9-54 slots).

## Basic Usage

```kotlin
val gui = KGui.chest(plugin, "<gold>My Menu", rows = 3) {
    slot(13) {
        item(Material.DIAMOND) { name("<aqua>Click me!") }
        onClick { player -> player.sendMessage("Clicked!") }
    }
}
gui.open(player)
```

## Builder Methods

### `slot(index, block)`

Configure a single slot:

```kotlin
slot(13) {
    item(Material.DIAMOND_SWORD) {
        name("<gold>Excalibur")
        lore("<gray>Legendary weapon")
        enchant(Enchantment.SHARPNESS, 5)
        glow()
    }
    sound(Sound.UI_BUTTON_CLICK)
    onClick { player, clickType ->
        when (clickType) {
            ClickType.LEFT -> player.sendMessage("Left click!")
            ClickType.RIGHT -> player.sendMessage("Right click!")
            else -> {}
        }
    }
    takeable() // Allow this item to be taken from the GUI
}
```

### `slots(indices, block)`

Configure multiple slots at once:

```kotlin
// Using IntRange
slots(10..16) { index ->
    item(Material.PAPER) { name("<white>Slot $index") }
}

// Using List
slots(listOf(10, 12, 14, 16)) { index ->
    item(Material.EMERALD) { name("<green>Option $index") }
}
```

### `border(material, block)`

Fill border slots (edges) with a filler item. Only fills slots that haven't been set yet:

```kotlin
border(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }
```

### `fill(material, block)`

Fill ALL empty slots with a filler item:

```kotlin
fill(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
```

### `pattern(block)`

Define layout using character patterns (see [Pattern Layouts](Pattern-Layouts)):

```kotlin
pattern {
    lines("XXXXXXXXX", "X...A...X", "XXXXXXXXX")
    'X' means filler(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
    'A' means clickable(Material.EMERALD) {
        item { name("<green>Click") }
        onClick { p, _ -> p.sendMessage("Clicked!") }
    }
}
```

### `interactable(value)`

Allow/disallow item extraction from the entire GUI (default: `false`):

```kotlin
interactable(true) // Players can take items from this GUI
```

### `dynamicItem(provider)`

Render a different item for each player:

```kotlin
slot(13) {
    dynamicItem { player ->
        ItemBuilder(Material.PLAYER_HEAD)
            .skullOwner(player)
            .name("<yellow>${player.name}")
            .build()
    }
}
```

### Event Handlers

```kotlin
onOpen { player -> player.sendMessage("Opened!") }
onClose { player -> player.sendMessage("Closed!") }
onClick { player, slot, clickType -> /* global click handler */ }
```

### `animate(intervalTicks, loop, block)`

Add frame-based animation to a slot:

```kotlin
slot(22) {
    animate(intervalTicks = 10L) {
        frame(Material.DIAMOND) { name("<aqua>Frame 1") }
        frame(Material.GOLD_INGOT) { name("<gold>Frame 2") }
        frame(Material.EMERALD) { name("<green>Frame 3") }
    }
}
```

## Updating the GUI

```kotlin
gui.update()         // Re-render for all viewers
gui.update(player)   // Re-render for one player
```
