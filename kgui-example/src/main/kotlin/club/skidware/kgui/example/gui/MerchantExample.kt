package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

object MerchantExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.merchant(plugin, "<green>Item Shop") {
            trade {
                result(Material.DIAMOND_SWORD) { name("<gold>Sharp Sword"); enchant(Enchantment.SHARPNESS, 5) }
                ingredient(Material.EMERALD) { amount(10) }
                ingredient(Material.IRON_SWORD)
                maxUses = 5
            }
            trade(result = ItemStack(Material.DIAMOND_CHESTPLATE), ingredient1 = ItemStack(Material.EMERALD, 20), maxUses = 3)
            trade { result(Material.GOLDEN_APPLE) { amount(5) }; ingredient(Material.EMERALD) { amount(3) }; maxUses = 10 }
            onTrade { p, index -> p.sendMessage("Completed trade #${index + 1}!") }
        }
        gui.open(player)
    }
}
