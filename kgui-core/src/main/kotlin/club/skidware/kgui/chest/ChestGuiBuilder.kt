package club.skidware.kgui.chest

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.pattern.PatternBuilder
import club.skidware.kgui.slot.GuiSlot
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [ChestGui] with a fluent, type-safe API.
 *
 * Provides slot configuration, border/fill helpers, pattern-based layouts,
 * and lifecycle callbacks. Titles are parsed via MiniMessage.
 *
 * ```kotlin
 * KGui.chest(plugin, "<gold>My Menu", rows = 3) {
 *     border(Material.BLACK_STAINED_GLASS_PANE)
 *     slot(13) {
 *         item(Material.DIAMOND) { name("<aqua>Click me") }
 *         onClick { player, _ -> player.sendMessage("Clicked!") }
 *     }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @param rows number of inventory rows (1-6)
 * @since 1.0.0
 * @see ChestGui
 */
@GuiDslMarker
class ChestGuiBuilder(
    private val plugin: JavaPlugin,
    private val title: String,
    private val rows: Int
) {
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null
    private var onClick: ((Player, Int, ClickType) -> Unit)? = null
    private var patternSlots: Map<Int, GuiSlot> = emptyMap()
    private var interactable: Boolean = false

    /**
     * Configures a single slot at the given inventory [index].
     *
     * @param index zero-based slot position
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) {
        this.slotBuilders[index] = SlotBuilder(index).apply(block)
    }

    /**
     * Configures multiple slots across a contiguous [indices] range.
     *
     * The current index is passed to [block] so each slot can be customized individually.
     *
     * @param indices range of slot positions to configure
     * @param block DSL configuration receiving the current slot index
     */
    fun slots(indices: IntRange, block: SlotBuilder.(Int) -> Unit) {
        indices.forEach { i -> this.slotBuilders[i] = SlotBuilder(i).apply { block(i) } }
    }

    /**
     * Configures multiple slots at the specified [indices].
     *
     * @param indices list of slot positions to configure
     * @param block DSL configuration receiving the current slot index
     */
    fun slots(indices: List<Int>, block: SlotBuilder.(Int) -> Unit) {
        indices.forEach { i -> this.slotBuilders[i] = SlotBuilder(i).apply { block(i) } }
    }

    /**
     * Fills the outer edge of the inventory with a decorative border.
     *
     * Only populates slots not already configured, so explicit slots take priority.
     *
     * @param material border item material
     * @param block optional item customization
     */
    fun border(material: Material, block: ItemBuilder.() -> Unit = {}) {
        val item = ItemBuilder(material).apply(block).build()
        for (i in 0 until this.rows * 9) {
            val row = i / 9; val col = i % 9
            if (row == 0 || row == this.rows - 1 || col == 0 || col == 8) {
                this.slotBuilders.putIfAbsent(i, SlotBuilder(i).apply { item(item) })
            }
        }
    }

    /**
     * Fills all empty slots with the given material as a background.
     *
     * Only populates slots not already configured.
     *
     * @param material fill item material
     * @param block optional item customization
     */
    fun fill(material: Material, block: ItemBuilder.() -> Unit = {}) {
        val item = ItemBuilder(material).apply(block).build()
        for (i in 0 until rows * 9) {
            this.slotBuilders.putIfAbsent(i, SlotBuilder(i).apply { item(item) })
        }
    }

    /**
     * Applies a character-based pattern layout to the inventory.
     *
     * @param block DSL configuration for the pattern
     * @see PatternBuilder
     */
    fun pattern(block: PatternBuilder.() -> Unit) {
        this.patternSlots = PatternBuilder().apply(block).buildSlots()
    }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { this.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    /** Registers a global click handler for all slots in this GUI. */
    fun onClick(handler: (Player, Int, ClickType) -> Unit) { this.onClick = handler }

    internal fun build(): ChestGui {
        val gui = ChestGui(this.plugin, MiniMessage.miniMessage().deserialize(this.title), this.rows)
        gui.interactable = this.interactable
        gui.onOpenHandler = this.onOpen
        gui.onCloseHandler = this.onClose
        gui.onClickHandler = this.onClick
        this.slotBuilders.values.forEach { gui.setSlot(it.build()) }
       this. patternSlots.values.forEach { gui.setSlot(it) }
        return gui
    }
}
