package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.slot.GuiSlot
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object StatefulExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val gui = KGui.stateful(plugin, "<gold>Click Counter", rows = 3) {
            var clicks by state("clicks", 0)
            border(Material.ORANGE_STAINED_GLASS_PANE) { name(" ") }
            render { p ->
                setSlot(GuiSlot(index = 13, item = ItemBuilder(Material.DIAMOND).name("<aqua>Clicks: <white>$clicks").amount(maxOf(1, minOf(64, clicks))).build(),
                    onClick = { _, _ -> clicks++; p.playSound(p.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f) }))
                setSlot(GuiSlot(index = 11, item = ItemBuilder(Material.RED_WOOL).name("<red>Reset").build(),
                    onClick = { _, _ -> clicks = 0 }))
                setSlot(GuiSlot(index = 15, item = ItemBuilder(Material.EMERALD).name("<green>+10").build(),
                    onClick = { _, _ -> clicks += 10 }))
            }
        }
        gui.open(player)
    }
}
