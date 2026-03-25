package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import club.skidware.kgui.scrollable.ScrollableGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object ScrollableExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val items = Material.entries.filter { it.isItem && !it.isAir }.take(100)
        val gui = KGui.scrollable(plugin, "<green>Scrollable List", rows = 6) {
            direction(ScrollableGui.Direction.VERTICAL)
            border(Material.LIME_STAINED_GLASS_PANE) { name(" ") }
            items(items) { mat ->
                item(mat) { name("<white>${mat.name.lowercase().replace('_', ' ')}") }
                onClick { p -> p.sendMessage("Selected: ${mat.name}") }
            }
            scrollUpButton(8, Material.ARROW) { name("<yellow>Scroll Up") }
            scrollDownButton(53, Material.ARROW) { name("<yellow>Scroll Down") }
        }
        gui.open(player)
    }
}
