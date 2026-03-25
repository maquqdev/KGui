package club.skidware.kgui.scrollable

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [ScrollableGui] with smooth content scrolling.
 *
 * Supports vertical/horizontal direction, scroll buttons, data-driven content,
 * and automatic inner-slot detection when the content area is not explicitly set.
 *
 * ```kotlin
 * KGui.scrollable(plugin, "<gold>Log Viewer", rows = 6) {
 *     direction(ScrollableGui.Direction.VERTICAL)
 *     border(Material.BLACK_STAINED_GLASS_PANE)
 *     items(logEntries) { entry ->
 *         item(Material.PAPER) { name(entry.message) }
 *     }
 *     scrollUpButton(0, Material.ARROW) { name("<red>Scroll Up") }
 *     scrollDownButton(8, Material.ARROW) { name("<green>Scroll Down") }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @param rows number of inventory rows (2-6)
 * @since 1.0.0
 * @see ScrollableGui
 */
@GuiDslMarker
class ScrollableGuiBuilder(
    private val plugin: JavaPlugin,
    private val title: String,
    private val rows: Int
) {
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private val contentBuilders: MutableList<SlotBuilder> = mutableListOf()
    private var contentSlots: List<Int> = emptyList()
    private var contentColumns: Int = 7
    private var direction: ScrollableGui.Direction = ScrollableGui.Direction.VERTICAL
    private var scrollUpBtn: SlotBuilder? = null
    private var scrollDownBtn: SlotBuilder? = null
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null
    private var interactable: Boolean = false

    /**
     * Configures a fixed (non-scrollable) slot, such as decoration or controls.
     *
     * @param index zero-based slot position
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) { slotBuilders[index] = SlotBuilder(index).apply(block) }

    /** Sets the scroll axis to vertical or horizontal. */
    fun direction(dir: ScrollableGui.Direction) { this.direction = dir }

    /**
     * Explicitly defines which inventory positions form the scrollable viewport.
     *
     * If not called, inner slots (excluding border) are used automatically.
     *
     * @param slots ordered list of viewport slot positions
     * @param columns number of columns per row, used for vertical scroll step calculation
     */
    fun contentArea(slots: List<Int>, columns: Int = 7) { this.contentSlots = slots; this.contentColumns = columns }

    /**
     * Maps a data collection into scrollable content items.
     *
     * @param T the data element type
     * @param data source collection to scroll through
     * @param builder DSL configuration receiving each data element
     */
    fun <T> items(data: List<T>, builder: SlotBuilder.(T) -> Unit) {
        data.forEachIndexed { index, item -> contentBuilders.add(SlotBuilder(index).apply { builder(item) }) }
    }

    /**
     * Configures the "scroll up" navigation button.
     *
     * @param slot inventory position for the button
     * @param material button item material
     * @param block optional item customization
     */
    fun scrollUpButton(slot: Int, material: Material, block: ItemBuilder.() -> Unit = {}) {
        scrollUpBtn = SlotBuilder(slot).apply { item(material, block) }
    }

    /**
     * Configures the "scroll down" navigation button.
     *
     * @param slot inventory position for the button
     * @param material button item material
     * @param block optional item customization
     */
    fun scrollDownButton(slot: Int, material: Material, block: ItemBuilder.() -> Unit = {}) {
        scrollDownBtn = SlotBuilder(slot).apply { item(material, block) }
    }

    /**
     * Fills the outer edge with a decorative border, skipping already-configured slots.
     *
     * @param material border item material
     * @param block optional item customization
     */
    fun border(material: Material, block: ItemBuilder.() -> Unit = {}) {
        val item = ItemBuilder(material).apply(block).build()
        for (i in 0 until rows * 9) {
            val row = i / 9; val col = i % 9
            if (row == 0 || row == rows - 1 || col == 0 || col == 8) {
                slotBuilders.putIfAbsent(i, SlotBuilder(i).apply { item(item) })
            }
        }
    }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { this.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): ScrollableGui {
        val gui = ScrollableGui(plugin, MiniMessage.miniMessage().deserialize(title), rows)
        gui.direction = direction
        gui.interactable = interactable
        gui.onOpenHandler = onOpen; gui.onCloseHandler = onClose
        if (contentSlots.isEmpty()) {
            val inner = mutableListOf<Int>()
            for (i in 0 until rows * 9) { val r = i / 9; val c = i % 9; if (r in 1 until rows - 1 && c in 1..7) inner.add(i) }
            gui.setContentArea(inner, contentColumns)
        } else { gui.setContentArea(contentSlots, contentColumns) }
        slotBuilders.values.forEach { gui.setSlot(it.build()) }
        gui.setContentItems(contentBuilders.map { it.build() })
        scrollUpBtn?.let { gui.scrollUpButton = it.build() }
        scrollDownBtn?.let { gui.scrollDownButton = it.build() }
        return gui
    }
}
