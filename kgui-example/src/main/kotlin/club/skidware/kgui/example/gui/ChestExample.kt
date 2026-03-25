package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import club.skidware.kgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object ChestExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.chest(plugin, "<dark_purple>Main Menu", rows = 3) {
            border(Material.GRAY_STAINED_GLASS_PANE) { name(" ") }

            slot(11) {
                item(Material.DIAMOND_SWORD) { name("<gold>Weapons Shop"); lore("<gray>Click to browse weapons"); glow() }
                sound(Sound.UI_BUTTON_CLICK)
                onClick { p -> p.sendMessage("Opening weapons shop...") }
            }

            slot(13) {
                dynamicItem { p ->
                    ItemBuilder(Material.PLAYER_HEAD).skullOwner(p).name("<yellow>${p.name}")
                        .lore("<gray>Level: <white>${p.level}", "<gray>Health: <red>${p.health.toInt()}").build()
                }
                onClick { p -> p.sendMessage("That's you!") }
            }

            slot(15) {
                item(Material.REDSTONE) { name("<red>Settings"); lore("<gray>Configure your preferences") }
                sound(Sound.UI_BUTTON_CLICK)
                onClick { p -> p.sendMessage("Settings coming soon!") }
            }

            slot(22) {
                animate(intervalTicks = 10L) {
                    frame(Material.DIAMOND) { name("<aqua>Click me!") }
                    frame(Material.GOLD_INGOT) { name("<gold>Click me!") }
                    frame(Material.EMERALD) { name("<green>Click me!") }
                }
                onClick { p -> p.sendMessage("Caught the animation!") }
            }

            onOpen { p -> p.sendMessage("Welcome to the main menu!") }
        }
        gui.open(player)
    }
}
