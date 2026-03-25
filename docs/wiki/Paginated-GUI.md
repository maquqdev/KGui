# Paginated GUI

Displays a list of items across multiple pages with automatic navigation.

## Basic Usage

```kotlin
val allItems = Material.entries.filter { it.isItem && !it.isAir }

val gui = KGui.paginated(plugin, "<blue>Item Browser", rows = 6) {
    border(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }

    // Add items to paginate
    items(allItems) { material ->
        item(material) {
            name("<white>${material.name.lowercase().replace('_', ' ')}")
            lore("<gray>Click to receive")
        }
        onClick { player ->
            player.inventory.addItem(ItemBuilder(material).build())
        }
    }

    // Navigation buttons
    previousButton(48, Material.ARROW) { name("<yellow>← Previous Page") }
    nextButton(50, Material.ARROW) { name("<yellow>Next Page →") }

    // Page indicator
    pageInfo(49) { current, max ->
        ItemBuilder(Material.PAPER)
            .name("<gray>Page $current / $max")
            .build()
    }
}
gui.open(player)
```

## Builder Methods

### `items(data, builder)`

Add a list of items to paginate. Each item gets its own slot across pages:

```kotlin
items(playerList) { p ->
    item(Material.PLAYER_HEAD) {
        name("<yellow>${p.name}")
    }
    onClick { viewer -> viewer.teleport(p.location) }
}
```

### `itemSlots(slots)`

Define which slots display paginated content. By default, all inner slots (excluding borders) are used:

```kotlin
// Explicit slot list
itemSlots(listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25))

// Using range
itemSlots(10..34)

// Using varargs
itemSlots(10, 11, 12, 13, 14, 15, 16)
```

### `previousButton(slot, material, block)`

Add a "previous page" button. Only visible when not on the first page:

```kotlin
previousButton(48, Material.ARROW) {
    name("<yellow>← Previous")
    lore("<gray>Go to the previous page")
}
```

### `nextButton(slot, material, block)`

Add a "next page" button. Only visible when not on the last page:

```kotlin
nextButton(50, Material.ARROW) {
    name("<yellow>Next →")
}
```

### `pageInfo(slot, provider)`

Display a page indicator item. Receives current page and max page (1-indexed):

```kotlin
pageInfo(49) { current, max ->
    ItemBuilder(Material.BOOK)
        .name("<white>Page $current of $max")
        .amount(current)
        .build()
}
```

### `slot(index, block)` / `border()` / `pattern()`

Static slots work the same as [Chest GUI](Chest-GUI). They appear on every page.

## Programmatic Page Control

```kotlin
val gui: PaginatedGui = KGui.paginated(plugin, "Title", 6) { ... }

gui.getPage(player)        // Current page (0-indexed)
gui.getMaxPage()           // Last page index
gui.nextPage(player)       // Go to next page
gui.previousPage(player)   // Go to previous page
gui.setPage(player, 3)     // Jump to specific page
```
