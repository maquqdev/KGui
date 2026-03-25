package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object HopperExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.hopper(plugin, "<aqua>Quick Select") {
            slot(0) { item(Material.IRON_SWORD) { name("<white>Iron") }; sound(Sound.UI_BUTTON_CLICK); onClick { p -> p.sendMessage("Iron tier!") } }
            slot(1) { item(Material.GOLDEN_SWORD) { name("<gold>Gold") }; sound(Sound.UI_BUTTON_CLICK); onClick { p -> p.sendMessage("Gold tier!") } }
            slot(2) { item(Material.DIAMOND_SWORD) { name("<aqua>Diamond") }; sound(Sound.UI_BUTTON_CLICK); onClick { p -> p.sendMessage("Diamond tier!") } }
            slot(3) { item(Material.NETHERITE_SWORD) { name("<dark_red>Netherite") }; sound(Sound.UI_BUTTON_CLICK); onClick { p -> p.sendMessage("Netherite tier!") } }
            slot(4) { item(Material.BARRIER) { name("<red>Cancel") }; onClick { p -> p.closeInventory() } }
        }
        gui.open(player)
    }
}
