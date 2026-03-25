# ItemBuilder

`ItemBuilder` provides a fluent API for creating `ItemStack` instances with MiniMessage formatting support.

## Basic Usage

```kotlin
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.item.item

// Using the DSL function
val sword = item(Material.DIAMOND_SWORD) {
    name("<gold>Excalibur")
    lore("<gray>A legendary sword", "<yellow>Damage: <red>+50")
    enchant(Enchantment.SHARPNESS, 5)
    glow()
}

// Using the builder directly
val head = ItemBuilder(Material.PLAYER_HEAD)
    .skullOwner(player)
    .name("<yellow>${player.name}")
    .lore("<gray>Click to view profile")
    .build()

// Using companion factory methods
val skull = ItemBuilder.skull(player).name("<green>Head").build()
val texturedSkull = ItemBuilder.skull("eyJ0ZXh0dXJlcy...").name("<red>Custom Head").build()
```

## All Methods

### Display

| Method | Description | Example |
|--------|-------------|---------|
| `name(text: String)` | Set display name with MiniMessage | `name("<gold>My Item")` |
| `name(component: Component)` | Set display name with Adventure Component | `name(Component.text("Hi"))` |
| `lore(vararg lines: String)` | Set lore lines (replaces existing) | `lore("<gray>Line 1", "<gray>Line 2")` |
| `lore(lines: List<String>)` | Set lore from list | `lore(myList)` |
| `loreComponents(lines: List<Component>)` | Set lore from Adventure Components | `loreComponents(components)` |
| `addLore(line: String)` | Append a lore line | `addLore("<red>New line")` |
| `addLore(component: Component)` | Append a lore Component | `addLore(comp)` |

### Material & Amount

| Method | Description | Example |
|--------|-------------|---------|
| `material(material: Material)` | Change the material type | `material(Material.GOLD_INGOT)` |
| `amount(amount: Int)` | Set stack size (clamped 1-64) | `amount(32)` |
| `damage(damage: Int)` | Set item durability damage | `damage(100)` |

### Enchantments & Flags

| Method | Description | Example |
|--------|-------------|---------|
| `enchant(enchantment, level)` | Add enchantment (allows unsafe levels) | `enchant(Enchantment.SHARPNESS, 10)` |
| `clearEnchantments()` | Remove all enchantments | `clearEnchantments()` |
| `glow(enabled: Boolean)` | Add enchantment glow without visible enchant | `glow()` |
| `flag(vararg flags: ItemFlag)` | Add item flags | `flag(ItemFlag.HIDE_ATTRIBUTES)` |
| `hideAllFlags()` | Hide all item flags | `hideAllFlags()` |
| `unbreakable(value: Boolean)` | Set unbreakable state | `unbreakable()` |
| `customModelData(data: Int)` | Set custom model data for resource packs | `customModelData(1001)` |

### Skulls

| Method | Description | Example |
|--------|-------------|---------|
| `skullOwner(player: OfflinePlayer)` | Set skull to player's skin | `skullOwner(player)` |
| `skullTexture(base64: String)` | Set skull texture from Base64 | `skullTexture("eyJ0ZXh...")` |

### Utilities

| Method | Description |
|--------|-------------|
| `build(): ItemStack` | Build the final ItemStack |
| `clone(): ItemBuilder` | Create a copy of this builder |

### Companion Factory Methods

| Method | Description |
|--------|-------------|
| `ItemBuilder.of(material)` | Create builder from material |
| `ItemBuilder.skull(player)` | Create player head builder |
| `ItemBuilder.skull(base64)` | Create textured skull builder |

## MiniMessage Formatting

All text fields support [MiniMessage](https://docs.advntr.dev/minimessage/format.html) formatting:

```kotlin
name("<gradient:gold:yellow>Gradient Name</gradient>")
name("<rainbow>Rainbow Text</rainbow>")
name("<bold><red>Bold Red</bold>")
lore(
    "<gray>Regular gray text",
    "<italic><yellow>Italic yellow",
    "<strikethrough>Crossed out</strikethrough>",
    "<hover:show_text:'<red>Tooltip'>Hover me</hover>"
)
```

## Using ItemBuilder in Slot DSL

Inside a `slot { }` block, you can use `item()` directly:

```kotlin
slot(13) {
    item(Material.DIAMOND_SWORD) {
        name("<gold>My Sword")
        enchant(Enchantment.SHARPNESS, 5)
        glow()
    }
}
```
