# Scrollable GUI

A GUI where content scrolls vertically or horizontally within a content area.

## Basic Usage

```kotlin
val materials = Material.entries.filter { it.isItem && !it.isAir }.take(100)

val gui = KGui.scrollable(plugin, "<green>Scrollable List", rows = 6) {
    direction(ScrollableGui.Direction.VERTICAL)
    border(Material.LIME_STAINED_GLASS_PANE) { name(" ") }

    items(materials) { mat ->
        item(mat) { name("<white>${mat.name.lowercase().replace('_', ' ')}") }
        onClick { player -> player.sendMessage("Selected: ${mat.name}") }
    }

    scrollUpButton(8, Material.ARROW) { name("<yellow>↑ Scroll Up") }
    scrollDownButton(53, Material.ARROW) { name("<yellow>↓ Scroll Down") }
}
gui.open(player)
```

## Scroll Direction

```kotlin
direction(ScrollableGui.Direction.VERTICAL)   // Scrolls by rows (default)
direction(ScrollableGui.Direction.HORIZONTAL)  // Scrolls by columns
```

- **Vertical**: Each scroll step moves by `columns` items (default 7)
- **Horizontal**: Each scroll step moves by 1 item

## Builder Methods

### `contentArea(slots, columns)`

Define which slots display scrollable content and how many columns they span:

```kotlin
// Custom content area: 5 columns, 4 rows
contentArea(
    slots = listOf(10,11,12,13,14, 19,20,21,22,23, 28,29,30,31,32, 37,38,39,40,41),
    columns = 5
)
```

By default, all inner slots (excluding border rows) are used with 7 columns.

### `items(data, builder)`

Add scrollable content items:

```kotlin
items(warpList) { warp ->
    item(Material.ENDER_PEARL) { name("<aqua>${warp.name}") }
    onClick { player -> player.teleport(warp.location) }
}
```

### `scrollUpButton(slot, material, block)` / `scrollDownButton(...)`

Navigation buttons. Only visible when scrolling is possible in that direction:

```kotlin
scrollUpButton(8, Material.ARROW) { name("<yellow>↑ Up") }
scrollDownButton(53, Material.ARROW) { name("<yellow>↓ Down") }
```

## Programmatic Scroll Control

```kotlin
val gui: ScrollableGui = KGui.scrollable(plugin, "Title", 6) { ... }

gui.getOffset(player)       // Current scroll offset
gui.getMaxOffset()          // Maximum scroll offset
gui.scrollUp(player)        // Scroll up by one step
gui.scrollDown(player)      // Scroll down by one step
gui.scrollUp(player, 3)     // Scroll up by 3 steps
```
