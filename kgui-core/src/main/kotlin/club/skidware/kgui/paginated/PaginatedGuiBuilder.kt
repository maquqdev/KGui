package club.skidware.kgui.paginated

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.pattern.PatternBuilder
import club.skidware.kgui.slot.GuiSlot
import club.skidware.kgui.slot.SlotBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [PaginatedGui] with automatic page management.
 *
 * Supports data-driven item lists, navigation buttons, page info display,
 * and automatic inner-slot detection when item slots are not explicitly set.
 *
 * ```kotlin
 * KGui.paginated(plugin, "<gold>Shop", rows = 6) {
 *     border(Material.BLACK_STAINED_GLASS_PANE)
 *     items(shopItems) { item ->
 *         item(item.material) { name(item.name) }
 *         onClick { player, _ -> player.sendMessage("Bought ${item.name}") }
 *     }
 *     previousButton(45, Material.ARROW) { name("<red>Previous") }
 *     nextButton(53, Material.ARROW) { name("<green>Next") }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted inventory title
 * @param rows number of inventory rows (2-6)
 * @since 1.0.0
 * @see PaginatedGui
 */
@GuiDslMarker
class PaginatedGuiBuilder(
    private val plugin: JavaPlugin,
    private val title: String,
    private val rows: Int
) {
    private val slotBuilders: MutableMap<Int, SlotBuilder> = mutableMapOf()
    private val pageItemBuilders: MutableList<SlotBuilder> = mutableListOf()
    private var itemSlots: List<Int> = emptyList()
    private var previousBtn: SlotBuilder? = null
    private var nextBtn: SlotBuilder? = null
    private var pageInfoProvider: ((Int, Int) -> GuiSlot)? = null
    private var onOpen: ((Player) -> Unit)? = null
    private var onClose: ((Player) -> Unit)? = null
    private var patternSlots: Map<Int, GuiSlot> = emptyMap()
    private var interactable: Boolean = false

    /**
     * Configures a fixed (non-paginated) slot, such as decoration or controls.
     *
     * @param index zero-based slot position
     * @param block DSL configuration for the slot
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) {
        slotBuilders[index] = SlotBuilder(index).apply(block)
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

    /**
     * Explicitly defines which inventory positions display paginated content.
     *
     * If not called, inner slots (excluding border) are used automatically.
     */
    fun itemSlots(slots: List<Int>) { this.itemSlots = slots }

    /** @see itemSlots */
    fun itemSlots(range: IntRange) { this.itemSlots = range.toList() }

    /** @see itemSlots */
    fun itemSlots(vararg slots: Int) { this.itemSlots = slots.toList() }

    /**
     * Maps a data collection into paginated slot items.
     *
     * Each element in [data] is passed to [builder] to produce a slot entry.
     *
     * @param T the data element type
     * @param data source collection to paginate
     * @param builder DSL configuration receiving each data element
     */
    fun <T> items(data: List<T>, builder: SlotBuilder.(T) -> Unit) {
        data.forEachIndexed { index, item ->
            pageItemBuilders.add(SlotBuilder(index).apply { builder(item) })
        }
    }

    /**
     * Configures the "previous page" navigation button.
     *
     * @param slot inventory position for the button
     * @param material button item material
     * @param block optional item customization
     */
    fun previousButton(slot: Int, material: Material, block: ItemBuilder.() -> Unit = {}) {
        previousBtn = SlotBuilder(slot).apply { item(material, block) }
    }

    /**
     * Configures the "next page" navigation button.
     *
     * @param slot inventory position for the button
     * @param material button item material
     * @param block optional item customization
     */
    fun nextButton(slot: Int, material: Material, block: ItemBuilder.() -> Unit = {}) {
        nextBtn = SlotBuilder(slot).apply { item(material, block) }
    }

    /**
     * Configures a dynamic page indicator that updates with current/max page numbers.
     *
     * @param slot inventory position for the indicator
     * @param provider factory receiving 1-based current and max page numbers
     */
    fun pageInfo(slot: Int, provider: (currentPage: Int, maxPage: Int) -> ItemStack) {
        pageInfoProvider = { current, max -> GuiSlot(index = slot, item = provider(current, max)) }
    }

    /**
     * Applies a character-based pattern layout to the inventory.
     *
     * @param block DSL configuration for the pattern
     * @see PatternBuilder
     */
    fun pattern(block: PatternBuilder.() -> Unit) {
        patternSlots = PatternBuilder().apply(block).buildSlots()
    }

    /** Allows players to move items within the inventory when set to `true`. */
    fun interactable(value: Boolean = true) { this.interactable = value }

    /** Registers a callback invoked when a player opens this GUI. */
    fun onOpen(handler: (Player) -> Unit) { this.onOpen = handler }

    /** Registers a callback invoked when a player closes this GUI. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): PaginatedGui {
        val gui = PaginatedGui(plugin, MiniMessage.miniMessage().deserialize(title), rows)
        gui.interactable = interactable
        gui.onOpenHandler = onOpen
        gui.onCloseHandler = onClose

        if (itemSlots.isEmpty()) {
            val inner = mutableListOf<Int>()
            for (i in 0 until rows * 9) {
                val row = i / 9; val col = i % 9
                if (row in 1 until rows - 1 && col in 1..7) inner.add(i)
            }
            gui.setItemSlots(inner)
        } else {
            gui.setItemSlots(itemSlots)
        }

        slotBuilders.values.forEach { gui.setSlot(it.build()) }
        patternSlots.values.forEach { gui.setSlot(it) }
        gui.addPageItems(pageItemBuilders.map { it.build() })
        previousBtn?.let { gui.previousButton = it.build() }
        nextBtn?.let { gui.nextButton = it.build() }
        pageInfoProvider?.let { gui.pageInfoSlot = it }
        return gui
    }
}
