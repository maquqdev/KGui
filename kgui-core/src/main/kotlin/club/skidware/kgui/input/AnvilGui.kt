package club.skidware.kgui.input

import club.skidware.kgui.core.BaseGui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * Anvil-based GUI that captures text input from players.
 *
 * Places an [inputItem] in the first anvil slot and reads the renamed text
 * when the player clicks the result slot. The [onSubmit] callback controls
 * whether the GUI closes after submission.
 *
 * Use [AnvilGuiBuilder] via [club.skidware.kgui.KGui.anvil] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @since 1.0.0
 * @see AnvilGuiBuilder
 */
class AnvilGui(
    plugin: JavaPlugin,
    title: Component
) : BaseGui(plugin, title, 1) {

    override val size: Int = 3

    /** The item placed in the left input slot for the player to rename. */
    var inputItem: ItemStack = ItemStack(Material.PAPER)

    /** Pre-filled text shown in the anvil rename field. */
    var defaultText: String = ""

    /**
     * Called when the player clicks the result slot.
     *
     * @return `true` to close the GUI after submission, `false` to keep it open
     */
    var onSubmit: ((Player, String) -> Boolean)? = null

    /** Called when the player closes the anvil without submitting. */
    var onCloseAction: ((Player) -> Unit)? = null

    override fun createInventory(player: Player): Inventory =
        Bukkit.createInventory(this, InventoryType.ANVIL, title)

    override fun renderFor(player: Player, inventory: Inventory) {
        val input = inputItem.clone()
        if (defaultText.isNotEmpty()) {
            val meta = input.itemMeta
            meta?.displayName(Component.text(defaultText))
            input.itemMeta = meta
        }
        inventory.setItem(0, input)
    }

    internal fun handleAnvilResult(player: Player, resultItem: ItemStack?) {
        val text = resultItem?.itemMeta?.displayName()?.let {
            PlainTextComponentSerializer.plainText().serialize(it)
        } ?: ""
        val shouldClose = onSubmit?.invoke(player, text) ?: true
        if (shouldClose) {
            Bukkit.getScheduler().runTask(plugin, Runnable { player.closeInventory() })
        }
    }
}
