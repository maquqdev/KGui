# Animations

Frame-based slot animations with configurable intervals and looping.

## Basic Usage

```kotlin
slot(13) {
    animate(intervalTicks = 10L, loop = true) {
        frame(Material.DIAMOND) { name("<aqua>Frame 1") }
        frame(Material.GOLD_INGOT) { name("<gold>Frame 2") }
        frame(Material.EMERALD) { name("<green>Frame 3") }
        frame(Material.REDSTONE) { name("<red>Frame 4") }
    }
    onClick { player -> player.sendMessage("Caught it!") }
}
```

## Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `intervalTicks` | `Long` | `20L` | Ticks between frame changes (20 ticks = 1 second) |
| `loop` | `Boolean` | `true` | Whether to restart from frame 1 after the last frame |

## Frame Types

### From Material

```kotlin
animate(intervalTicks = 5L) {
    frame(Material.WHITE_WOOL) { name("<white>Loading.") }
    frame(Material.LIGHT_GRAY_WOOL) { name("<gray>Loading..") }
    frame(Material.GRAY_WOOL) { name("<dark_gray>Loading...") }
}
```

### From ItemStack

```kotlin
val customItem = ItemBuilder(Material.DIAMOND_SWORD)
    .enchant(Enchantment.SHARPNESS, 5)
    .glow()
    .build()

animate(intervalTicks = 20L) {
    frame(customItem)
    frame(Material.AIR) // Empty frame (blink effect)
}
```

## Common Tick Values

| Ticks | Duration | Use Case |
|-------|----------|----------|
| `1` | 50ms | Very fast flashing |
| `5` | 250ms | Fast animation |
| `10` | 500ms | Smooth cycling |
| `20` | 1 second | Gentle pulsing |
| `40` | 2 seconds | Slow rotation |

## How It Works

- Animations use a single `BukkitRunnable` per GUI instance
- The task interval is set to the shortest `intervalTicks` across all animated slots
- Each animated slot tracks its own frame index
- When `loop = false`, the animation stops at the last frame
- Animations start when a player opens the GUI
- Animations stop when the last viewer closes the GUI

## Animation with Click Handlers

Animations and click handlers work independently. The click handler always uses the slot's configured `onClick`, regardless of which animation frame is showing:

```kotlin
slot(22) {
    animate(intervalTicks = 10L) {
        frame(Material.DIAMOND) { name("<aqua>Buy!") }
        frame(Material.EMERALD) { name("<green>Buy!") }
    }
    sound(Sound.UI_BUTTON_CLICK)
    onClick { player ->
        // This handler works regardless of current frame
        processPayment(player)
    }
}
```
