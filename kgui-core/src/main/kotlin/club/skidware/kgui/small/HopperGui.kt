package club.skidware.kgui.small

import club.skidware.kgui.core.BaseGui
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

/**
 * Hopper-based GUI providing a compact 5-slot horizontal layout.
 *
 * Ideal for simple yes/no prompts, confirmations, or small selection menus
 * where a full chest inventory would be excessive.
 *
 * Use [HopperGuiBuilder] via [club.skidware.kgui.KGui.hopper] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @since 1.0.0
 * @see HopperGuiBuilder
 */
class HopperGui(plugin: JavaPlugin, title: Component) : BaseGui(plugin, title, 1) {
    override val size: Int = 5
    override fun createInventory(player: Player): Inventory = Bukkit.createInventory(this, InventoryType.HOPPER, title)
}
