package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object AnvilExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.anvil(plugin, "<yellow>Enter your name") {
            inputItem(Material.NAME_TAG) { name("<gray>Type here...") }
            defaultText("Steve")
            onSubmit { p, text ->
                if (text.length < 3) { p.sendMessage("Name must be at least 3 characters!"); false }
                else { p.sendMessage("Your new name: $text"); true }
            }
        }
        gui.open(player)
    }
}
