package club.skidware.kgui.small

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [DispenserGui] with a 3x3 grid layout.
 *
 * ```kotlin
 * KGui.dispenser(plugin, "<gold>Select Option") {
 *     slot(4) { item(Material.COMPASS) { name("<aqua>Center") } }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @since 1.0.0
 * @see DispenserGui
 */
@GuiDslMarker
class DispenserGuiBuilder(private val plugin: JavaPlugin, private val title: String) {
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null
    private var interactable: Boolean = false

    /**
     * Configures a slot at the given inventory [index] (0-8).
     *
     * @param index zero-based slot position in the 3x3 grid
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) { slotBuilders[index] = SlotBuilder(index).apply(block) }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { this.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): DispenserGui {
        val gui = DispenserGui(plugin, MiniMessage.miniMessage().deserialize(title))
        gui.interactable = interactable; gui.onOpenHandler = onOpen; gui.onCloseHandler = onClose
        slotBuilders.values.forEach { gui.setSlot(it.build()) }
        return gui
    }
}
