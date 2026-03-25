# Anvil & Sign Input

KGui provides two text input mechanisms: anvil rename and sign editing.

## Anvil GUI

Uses the anvil rename mechanic to capture text input from the player.

```kotlin
val gui = KGui.anvil(plugin, "<yellow>Enter Name") {
    inputItem(Material.NAME_TAG) {
        name("<gray>Type here...")
    }
    defaultText("Steve")

    onSubmit { player, text ->
        if (text.length < 3) {
            player.sendMessage("<red>Must be at least 3 characters!")
            false  // Keep the GUI open
        } else {
            player.sendMessage("Your name: $text")
            true   // Close the GUI
        }
    }

    onClose { player ->
        player.sendMessage("Input cancelled")
    }
}
gui.open(player)
```

### Builder Methods

| Method | Description |
|--------|-------------|
| `inputItem(material, block)` | Set the item shown in the left anvil slot |
| `defaultText(text)` | Pre-fill the rename field with text |
| `onSubmit(handler)` | Called when player clicks the result slot. Return `true` to close, `false` to keep open |
| `onClose(handler)` | Called when the anvil GUI is closed |

### `onSubmit` Return Values

| Return | Behavior |
|--------|----------|
| `true` | Close the anvil GUI |
| `false` | Keep the anvil GUI open (e.g., for validation errors) |

## Sign GUI

Opens a temporary sign for the player to type on. The sign is placed at bedrock level and automatically cleaned up.

```kotlin
val gui = KGui.sign(plugin) {
    line(0, "Enter your")
    line(1, "message below")
    line(2, "")
    line(3, "")

    onComplete { player, lines ->
        val input = lines[2] // Player typically types on line 3 (index 2)
        player.sendMessage("You typed: $input")
    }
}
gui.open(player)
```

### Builder Methods

| Method | Description |
|--------|-------------|
| `line(index, text)` | Set a sign line (0-3). Pre-filled text |
| `onComplete(handler)` | Called when the player finishes editing. Receives all 4 lines as `List<String>` |

> **Note:** Sign GUI places a temporary sign block at the world's minimum Y level. It's cleaned up after 2 ticks. This works reliably in all dimensions.
