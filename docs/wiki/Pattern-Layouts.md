# Pattern Layouts

Define GUI layouts using character-based patterns for clear, visual slot arrangement.

## Basic Usage

```kotlin
val gui = KGui.chest(plugin, "<purple>Pattern Demo", rows = 5) {
    pattern {
        lines(
            "XXXXXXXXX",
            "X.......X",
            "X..ABA..X",
            "X.......X",
            "XXXXXXXXX"
        )

        'X' means filler(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
        'A' means clickable(Material.EMERALD) {
            item { name("<green>Option A") }
            onClick { player, _ -> player.sendMessage("Chose A!") }
            sound(Sound.UI_BUTTON_CLICK)
        }
        'B' means clickable(Material.DIAMOND) {
            item { name("<aqua>Option B") }
            onClick { player, _ -> player.sendMessage("Chose B!") }
            sound(Sound.UI_BUTTON_CLICK)
        }
    }
}
gui.open(player)
```

## Pattern Rules

- Each character maps to a slot type via the `means` infix function
- `.` (dot) and ` ` (space) are reserved as **empty slots** -- no item placed
- Spaces in the pattern string are stripped (so `"X X X"` = `"XXX"`)
- Each line represents one row (9 characters = 9 slots)
- Pattern lines map to rows top-to-bottom

## Slot Types

### `filler(material, block)`

Non-interactive decoration item:

```kotlin
'X' means filler(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }
'#' means filler(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
```

### `clickable(material, block)`

Interactive item with click handler and optional sound:

```kotlin
'S' means clickable(Material.DIAMOND_SWORD) {
    item {
        name("<gold>Shop")
        lore("<gray>Click to open the shop")
        glow()
    }
    onClick { player, clickType ->
        openShop(player)
    }
    sound(Sound.UI_BUTTON_CLICK)
}
```

### `clickable(itemStack, onClick)`

Quick clickable from an existing ItemStack:

```kotlin
val myItem = ItemBuilder(Material.COMPASS).name("<white>Navigate").build()
'N' means clickable(myItem) { player, _ -> openNavigation(player) }
```

### `dynamic(provider, onClick)`

Per-player dynamic item:

```kotlin
'P' means dynamic(
    provider = { player -> ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).name("<yellow>${player.name}").build() },
    onClick = { player, _ -> openProfile(player) }
)
```

## Combining Pattern with Manual Slots

Pattern slots can coexist with `slot()` calls. `slot()` calls made before `pattern()` can be overridden by the pattern; calls after `pattern()` override pattern slots:

```kotlin
KGui.chest(plugin, "Title", rows = 3) {
    pattern {
        lines("XXXXXXXXX", "X.......X", "XXXXXXXXX")
        'X' means filler(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
    }

    // This overrides slot 13 even if pattern already set it
    slot(13) {
        item(Material.NETHER_STAR) { name("<gold>Special") }
    }
}
```

## Common Patterns

### Full Border

```
XXXXXXXXX
X.......X
X.......X
X.......X
XXXXXXXXX
```

### Centered Item

```
XXXXXXXXX
X...A...X
XXXXXXXXX
```

### Grid Selection

```
XXXXXXXXX
X.A.B.C.X
X.D.E.F.X
X.G.H.I.X
XXXXXXXXX
```

### Navigation Bar

```
XXXXXXXXX
X.......X
X.......X
X.......X
XPXXIXXNX
```
Where `P` = Previous, `I` = Info, `N` = Next
