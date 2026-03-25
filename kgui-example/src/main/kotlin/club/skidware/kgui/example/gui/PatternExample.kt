package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object PatternExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.chest(plugin, "<light_purple>Pattern Layout", rows = 5) {
            pattern {
                lines("XXXXXXXXX", "X.......X", "X..ABA..X", "X.......X", "XXXXXXXXX")
                'X' means filler(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
                'A' means clickable(Material.EMERALD) {
                    item { name("<green>Option A") }; onClick { p, _ -> p.sendMessage("You chose A!") }; sound(Sound.UI_BUTTON_CLICK)
                }
                'B' means clickable(Material.DIAMOND) {
                    item { name("<aqua>Option B") }; onClick { p, _ -> p.sendMessage("You chose B!") }; sound(Sound.UI_BUTTON_CLICK)
                }
            }
        }
        gui.open(player)
    }
}
