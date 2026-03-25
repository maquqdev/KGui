package club.skidware.kgui.stateful

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [StatefulGui] with reactive state management.
 *
 * State values are declared with [state] and used inside the [render] block,
 * which re-executes automatically when any state changes.
 *
 * ```kotlin
 * KGui.stateful(plugin, "<gold>Counter", rows = 3) {
 *     var count by state("count", 0)
 *     render { player ->
 *         slot(13) {
 *             item(Material.DIAMOND) { name("<aqua>Count: $count") }
 *             onClick { _, _ -> count++ }
 *         }
 *     }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @param rows number of inventory rows (1-6)
 * @since 1.0.0
 * @see StatefulGui
 */
@GuiDslMarker
class StatefulGuiBuilder(
    private val plugin: JavaPlugin,
    private val title: String,
    private val rows: Int
) {
    private val gui = StatefulGui(plugin, MiniMessage.miniMessage().deserialize(title), rows)
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null

    /**
     * Declares a reactive state variable backed by the GUI's state map.
     *
     * @param T the state value type
     * @param key unique identifier for this state entry
     * @param initial default value
     * @return a delegated property that triggers re-renders on write
     */
    fun <T> state(key: String, initial: T) = gui.state(key, initial)

    /**
     * Configures a fixed slot. For dynamic slots, define them inside [render] instead.
     *
     * @param index zero-based slot position
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) { slotBuilders[index] = SlotBuilder(index).apply(block) }

    /**
     * Defines the render function that rebuilds inventory contents from current state.
     *
     * Called automatically on every state change and when the GUI is first opened.
     *
     * @param block rendering logic receiving the viewing player
     */
    fun render(block: StatefulGui.(Player) -> Unit) { gui.renderFunction = block }

    /**
     * Fills the outer edge with a decorative border, skipping already-configured slots.
     *
     * @param material border item material
     * @param block optional item customization
     */
    fun border(material: Material, block: ItemBuilder.() -> Unit = {}) {
        val item = ItemBuilder(material).apply(block).build()
        for (i in 0 until rows * 9) {
            val r = i / 9; val c = i % 9
            if (r == 0 || r == rows - 1 || c == 0 || c == 8) slotBuilders.putIfAbsent(i, SlotBuilder(i).apply { item(item) })
        }
    }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { gui.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): StatefulGui {
        gui.onOpenHandler = onOpen; gui.onCloseHandler = onClose
        slotBuilders.values.forEach { gui.setSlot(it.build()) }
        return gui
    }
}
