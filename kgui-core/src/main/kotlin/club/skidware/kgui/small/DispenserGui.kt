package club.skidware.kgui.small

import club.skidware.kgui.core.BaseGui
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

/**
 * Dispenser-based GUI providing a compact 3x3 grid layout.
 *
 * Useful for small selection grids, crafting-style interfaces, or any menu
 * needing exactly 9 slots arranged in a square.
 *
 * Use [DispenserGuiBuilder] via [club.skidware.kgui.KGui.dispenser] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @since 1.0.0
 * @see DispenserGuiBuilder
 */
class DispenserGui(plugin: JavaPlugin, title: Component) : BaseGui(plugin, title, 1) {
    override val size: Int = 9
    override fun createInventory(player: Player): Inventory = Bukkit.createInventory(this, InventoryType.DISPENSER, title)
}
