package club.skidware.kgui.small

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [HopperGui] with a compact 5-slot layout.
 *
 * ```kotlin
 * KGui.hopper(plugin, "<gold>Confirm") {
 *     slot(0) { item(Material.RED_WOOL) { name("<red>Cancel") } }
 *     slot(4) { item(Material.GREEN_WOOL) { name("<green>Confirm") } }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @since 1.0.0
 * @see HopperGui
 */
@GuiDslMarker
class HopperGuiBuilder(private val plugin: JavaPlugin, private val title: String) {
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null
    private var interactable: Boolean = false

    /**
     * Configures a slot at the given inventory [index] (0-4).
     *
     * @param index zero-based slot position
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) { slotBuilders[index] = SlotBuilder(index).apply(block) }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { this.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): HopperGui {
        val gui = HopperGui(plugin, MiniMessage.miniMessage().deserialize(title))
        gui.interactable = interactable; gui.onOpenHandler = onOpen; gui.onCloseHandler = onClose
        slotBuilders.values.forEach { gui.setSlot(it.build()) }
        return gui
    }
}
