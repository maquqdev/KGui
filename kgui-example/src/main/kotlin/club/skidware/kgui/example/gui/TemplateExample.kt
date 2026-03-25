package club.skidware.kgui.example.gui

import club.skidware.kgui.KGui
import club.skidware.kgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

object TemplateExample {
    private val shopTemplate = KGui.template("shop") {
        border(Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
        previousButton(48, Material.ARROW) { name("<yellow>Previous") }
        nextButton(50, Material.ARROW) { name("<yellow>Next") }
        closeButton(49, Material.BARRIER) { name("<red>Close") }
    }

    fun open(plugin: JavaPlugin, player: Player) {
        val swords = listOf(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD)
        val gui = KGui.paginated(plugin, "<gold>Sword Shop", rows = 6) {
            border(shopTemplate.borderMaterial ?: Material.BLACK_STAINED_GLASS_PANE) { name(" ") }
            items(swords) { mat ->
                item(mat) { name("<yellow>${mat.name.lowercase().replace('_', ' ')}"); lore("<gray>Click to buy!") }
                onClick { p -> p.inventory.addItem(ItemBuilder(mat).build()); p.sendMessage("Purchased ${mat.name}!") }
            }
            previousButton(shopTemplate.previousButtonSlot ?: 48, shopTemplate.previousButtonMaterial) { name("<yellow>Previous") }
            nextButton(shopTemplate.nextButtonSlot ?: 50, shopTemplate.nextButtonMaterial) { name("<yellow>Next") }
        }
        gui.open(player)
    }
}
