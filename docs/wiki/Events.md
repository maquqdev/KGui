# Events

KGui fires custom Bukkit events that you can listen to for cross-cutting concerns.

## Available Events

### `GuiOpenEvent`

Fired when a player opens a KGui inventory. Cancellable.

```kotlin
@EventHandler
fun onGuiOpen(event: GuiOpenEvent) {
    val player = event.player
    val gui = event.gui

    // Cancel opening for banned players
    if (isBanned(player)) {
        event.isCancelled = true
        player.sendMessage("You can't use GUIs!")
    }
}
```

### `GuiCloseEvent`

Fired when a player closes a KGui inventory.

```kotlin
@EventHandler
fun onGuiClose(event: GuiCloseEvent) {
    val player = event.player
    val gui = event.gui
    savePlayerData(player)
}
```

### `GuiClickEvent`

Fired when a player clicks in a KGui inventory. Cancellable.

```kotlin
@EventHandler
fun onGuiClick(event: GuiClickEvent) {
    val player = event.player
    val gui = event.gui
    val slot = event.slot
    val clickType = event.clickType

    // Log all GUI interactions
    logger.info("${player.name} clicked slot $slot with $clickType")
}
```

### `GuiUpdateEvent`

Fired when a GUI is updated/re-rendered.

```kotlin
@EventHandler
fun onGuiUpdate(event: GuiUpdateEvent) {
    val gui = event.gui
    val player = event.player // Nullable — null if update() was called for all viewers
}
```

## Event Registration

Register your listeners as normal Bukkit listeners:

```kotlin
class MyGuiListener : Listener {
    @EventHandler
    fun onOpen(event: GuiOpenEvent) { /* ... */ }

    @EventHandler
    fun onClick(event: GuiClickEvent) { /* ... */ }
}

// In your plugin
Bukkit.getPluginManager().registerEvents(MyGuiListener(), plugin)
```

## Event Imports

```kotlin
import club.skidware.kgui.event.GuiOpenEvent
import club.skidware.kgui.event.GuiCloseEvent
import club.skidware.kgui.event.GuiClickEvent
import club.skidware.kgui.event.GuiUpdateEvent
```
