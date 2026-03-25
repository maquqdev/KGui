# Merchant GUI

A villager-style trading interface with custom trade recipes.

## Basic Usage

```kotlin
val gui = KGui.merchant(plugin, "<green>Weapon Shop") {
    // DSL trade builder
    trade {
        result(Material.DIAMOND_SWORD) {
            name("<gold>Sharp Sword")
            enchant(Enchantment.SHARPNESS, 5)
        }
        ingredient(Material.EMERALD) { amount(10) }
        ingredient(Material.IRON_SWORD)  // Second ingredient (optional)
        maxUses = 5
        experienceReward = true
    }

    // Quick trade (no DSL)
    trade(
        result = ItemStack(Material.DIAMOND_CHESTPLATE),
        ingredient1 = ItemStack(Material.EMERALD, 20),
        maxUses = 3
    )

    // Simple consumable trade
    trade {
        result(Material.GOLDEN_APPLE) { amount(5) }
        ingredient(Material.EMERALD) { amount(3) }
        maxUses = 10
    }

    onTrade { player, tradeIndex ->
        player.sendMessage("Purchased trade #${tradeIndex + 1}!")
    }
}
gui.open(player)
```

## Trade Builder

### DSL Trade

```kotlin
trade {
    result(Material.DIAMOND_SWORD) { // Required: the item player receives
        name("<gold>My Sword")
        enchant(Enchantment.SHARPNESS, 3)
    }
    ingredient(Material.EMERALD) {    // Required: first ingredient
        amount(10)
    }
    ingredient(Material.IRON_SWORD)   // Optional: second ingredient
    maxUses = 5                       // Max times this trade can be used (default: unlimited)
    experienceReward = false          // Whether trade gives XP (default: false)
}
```

### Quick Trade

```kotlin
trade(
    result = ItemStack(Material.DIAMOND, 3),
    ingredient1 = ItemStack(Material.EMERALD, 5),
    ingredient2 = ItemStack(Material.GOLD_INGOT, 2),  // Optional
    maxUses = 10,
    experienceReward = true
)
```

### TradeBuilder Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `result` | `ItemStack?` | `null` | Item the player receives (required) |
| `ingredient1` | `ItemStack?` | `null` | First ingredient (required) |
| `ingredient2` | `ItemStack?` | `null` | Second ingredient (optional) |
| `maxUses` | `Int` | `Int.MAX_VALUE` | Maximum trade uses |
| `experienceReward` | `Boolean` | `false` | Whether trade gives XP |

## Event Handler

```kotlin
onTrade { player, tradeIndex ->
    // tradeIndex is 0-based
    player.sendMessage("You completed trade #${tradeIndex + 1}")
}
```

> **Note:** MerchantGui does NOT extend BaseGui -- it uses Bukkit's Merchant API directly. This means `interactable`/`takeable` settings don't apply; trades work through the vanilla trading interface.
