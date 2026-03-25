# KGui - Minecraft GUI Library

**KGui** is a Kotlin DSL-based GUI library for Paper/Bukkit plugins. It provides a type-safe, fluent API for creating interactive inventory menus.

## Features

- **7 GUI types**: Chest, Paginated, Scrollable, Stateful, Anvil/Sign input, Hopper/Dispenser, Merchant
- **DSL Builder API**: Type-safe Kotlin DSL for all GUI types
- **Custom ItemBuilder**: Fluent ItemStack creation with MiniMessage support
- **Item extraction control**: GUI-level `interactable` + per-slot `takeable` override
- **Pagination & Scrolling**: Built-in multi-page and scrollable content support
- **Reactive state**: `StatefulGui` with property delegates that auto-refresh the GUI
- **Pattern layouts**: Define GUI layouts using character patterns
- **Navigation**: Stack-based back/forward GUI history per player
- **Templates**: Reusable layout presets for consistent styling
- **Animations**: Frame-based slot animations with configurable intervals
- **MiniMessage**: Full MiniMessage formatting support in all text fields
- **Dynamic items**: Per-player item rendering via lambdas

## Requirements

- **Kotlin** 2.1+
- **Paper** 1.21.4+
- **JDK** 21+

## Quick Example

```kotlin
class MyPlugin : JavaPlugin() {
    override fun onEnable() {
        KGui.setup(this)
    }
}

// Create and open a simple menu
val gui = KGui.chest(plugin, "<gold>My Menu", rows = 3) {
    border(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }

    slot(13) {
        item(Material.DIAMOND) {
            name("<aqua>Click me!")
            lore("<gray>This is a demo item")
            glow()
        }
        sound(Sound.UI_BUTTON_CLICK)
        onClick { player ->
            player.sendMessage("You clicked the diamond!")
        }
    }
}
gui.open(player)
```

## Installation

### Gradle (JitPack)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.maquqdev.KGui:kgui-core:v1.0.0")
}
```

### Maven (JitPack)

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.maquqdev.KGui</groupId>
    <artifactId>kgui-core</artifactId>
    <version>v1.0.0</version>
</dependency>
```

## Pages

- [Getting Started](Getting-Started)
- [ItemBuilder](ItemBuilder)
- [Chest GUI](Chest-GUI)
- [Paginated GUI](Paginated-GUI)
- [Scrollable GUI](Scrollable-GUI)
- [Stateful GUI](Stateful-GUI)
- [Anvil & Sign Input](Anvil-and-Sign-Input)
- [Hopper & Dispenser GUI](Hopper-and-Dispenser-GUI)
- [Merchant GUI](Merchant-GUI)
- [Pattern Layouts](Pattern-Layouts)
- [Templates](Templates)
- [Navigation](Navigation)
- [Item Extraction Control](Item-Extraction-Control)
- [Animations](Animations)
- [Events](Events)
