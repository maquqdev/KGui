# Hopper & Dispenser GUI

Small-inventory GUIs for quick selections and compact menus.

## Hopper GUI (5 slots)

A compact 5-slot menu using the hopper inventory type.

```kotlin
val gui = KGui.hopper(plugin, "<aqua>Quick Select") {
    slot(0) {
        item(Material.IRON_SWORD) { name("<white>Easy") }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player -> player.sendMessage("Easy mode!") }
    }
    slot(1) {
        item(Material.DIAMOND_SWORD) { name("<yellow>Normal") }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player -> player.sendMessage("Normal mode!") }
    }
    slot(2) {
        item(Material.NETHERITE_SWORD) { name("<red>Hard") }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player -> player.sendMessage("Hard mode!") }
    }
    slot(3) {
        item(Material.BARRIER) { name("<dark_red>Impossible") }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player -> player.sendMessage("Good luck!") }
    }
    slot(4) {
        item(Material.DARK_OAK_DOOR) { name("<gray>Cancel") }
        onClick { player -> player.closeInventory() }
    }
}
gui.open(player)
```

### Slot Layout

```
[0] [1] [2] [3] [4]
```

## Dispenser GUI (9 slots)

A 9-slot 3x3 grid using the dispenser inventory type.

```kotlin
val gui = KGui.dispenser(plugin, "<gold>Options") {
    slot(0) { item(Material.RED_WOOL) { name("<red>Red") } }
    slot(1) { item(Material.GREEN_WOOL) { name("<green>Green") } }
    slot(2) { item(Material.BLUE_WOOL) { name("<blue>Blue") } }
    slot(4) { item(Material.COMPASS) { name("<white>Center") } }
    slot(8) { item(Material.BARRIER) { name("<gray>Close") }; onClick { p -> p.closeInventory() } }

    interactable(false) // Default — can't take items
}
gui.open(player)
```

### Slot Layout

```
[0] [1] [2]
[3] [4] [5]
[6] [7] [8]
```

## Common Builder Methods

Both hopper and dispenser builders support:

| Method | Description |
|--------|-------------|
| `slot(index, block)` | Configure a slot with item, click handler, sound, etc. |
| `interactable(value)` | Allow/disallow item extraction (default: `false`) |
| `onOpen(handler)` | Called when GUI opens |
| `onClose(handler)` | Called when GUI closes |
