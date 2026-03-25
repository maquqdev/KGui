# Templates

Reusable layout presets for consistent GUI styling across your plugin.

## Creating a Template

```kotlin
val shopTemplate = KGui.template("shop") {
    border(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
    previousButton(48, Material.ARROW) { name("<yellow>← Previous") }
    nextButton(50, Material.ARROW) { name("<yellow>Next →") }
    closeButton(49, Material.BARRIER) { name("<red>Close") }
    backButton(45, Material.DARK_OAK_DOOR) { name("<gray>Back") }
}
```

## Using a Template

Templates store configuration that you apply when building GUIs:

```kotlin
val gui = KGui.paginated(plugin, "<gold>Sword Shop", rows = 6) {
    // Apply border from template
    border(shopTemplate.borderMaterial ?: Material.BLACK_STAINED_GLASS_PANE) { name(" ") }

    // Apply navigation buttons from template
    previousButton(
        shopTemplate.previousButtonSlot ?: 48,
        shopTemplate.previousButtonMaterial
    ) { name("<yellow>← Previous") }

    nextButton(
        shopTemplate.nextButtonSlot ?: 50,
        shopTemplate.nextButtonMaterial
    ) { name("<yellow>Next →") }

    items(swordList) { sword -> /* ... */ }
}
```

## Template Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `borderMaterial` | `Material?` | `null` | Border filler material |
| `fillMaterial` | `Material?` | `null` | Fill material for empty slots |
| `previousButtonSlot` | `Int?` | `null` | Slot index for previous page button |
| `previousButtonMaterial` | `Material` | `ARROW` | Material for previous button |
| `nextButtonSlot` | `Int?` | `null` | Slot index for next page button |
| `nextButtonMaterial` | `Material` | `ARROW` | Material for next button |
| `closeButtonSlot` | `Int?` | `null` | Slot index for close button |
| `closeButtonMaterial` | `Material` | `BARRIER` | Material for close button |
| `backButtonSlot` | `Int?` | `null` | Slot index for back button |
| `backButtonMaterial` | `Material` | `ARROW` | Material for back button |

## Retrieving Templates

```kotlin
val template = KGui.getTemplate("shop") // Returns GuiTemplate? (null if not found)
```

## Template with Pattern

```kotlin
val menuTemplate = KGui.template("menu") {
    pattern {
        lines("XXXXXXXXX", "X.......X", "X.......X", "X.......X", "XXXXCXXXX")
        'X' means filler(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }
        'C' means clickable(Material.BARRIER) {
            item { name("<red>Close") }
            onClick { p, _ -> p.closeInventory() }
        }
    }
}
```
