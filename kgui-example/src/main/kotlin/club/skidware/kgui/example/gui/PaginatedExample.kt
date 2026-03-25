package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import club.skidware.kgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object PaginatedExample {
    fun open(plugin: JavaPlugin, player: Player) {
        val allMaterials = Material.entries.filter { it.isItem && !it.isAir }
        val gui = KGui.paginated(plugin, "<blue>All Items", rows = 6) {
            border(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }

            items(allMaterials) { mat ->
                item(mat) { name("<white>${mat.name.lowercase().replace('_', ' ')}"); lore("<gray>Click to get") }
                onClick { p -> p.inventory.addItem(ItemBuilder(mat).build()); p.sendMessage("You received ${mat.name}!") }
            }

            previousButton(48, Material.ARROW) { name("<yellow>Previous Page") }
            nextButton(50, Material.ARROW) { name("<yellow>Next Page") }
            pageInfo(49) { current, max -> ItemBuilder(Material.PAPER).name("<gray>Page $current/$max").build() }
        }
        gui.open(player)
    }
}
