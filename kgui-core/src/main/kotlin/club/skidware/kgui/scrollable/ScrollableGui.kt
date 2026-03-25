package club.skidware.kgui.scrollable

import club.skidware.kgui.core.BaseGui
import club.skidware.kgui.slot.GuiSlot
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Chest-based GUI that scrolls content smoothly rather than paging discretely.
 *
 * Supports both vertical (row-by-row) and horizontal (column-by-column) scrolling.
 * Each player's scroll offset is tracked independently via a [ConcurrentHashMap].
 *
 * Use [ScrollableGuiBuilder] via [club.skidware.kgui.KGui.scrollable] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @param rows number of rows (clamped to 2-6)
 * @since 1.0.0
 * @see ScrollableGuiBuilder
 */
class ScrollableGui(
    plugin: JavaPlugin,
    title: Component,
    rows: Int = 6
) : BaseGui(plugin, title, rows.coerceIn(2, 6)) {

    /** Controls whether content shifts by rows or columns when scrolling. */
    enum class Direction { VERTICAL, HORIZONTAL }

    /** The scroll axis for this GUI. Defaults to [Direction.VERTICAL]. */
    var direction: Direction = Direction.VERTICAL
    private val contentItems: MutableList<GuiSlot> = mutableListOf()
    private val playerOffsets: MutableMap<UUID, Int> = ConcurrentHashMap()
    private var contentSlots: List<Int> = emptyList()
    private var contentColumns: Int = 7

    /** Slot rendered when the player can scroll upward/leftward. */
    var scrollUpButton: GuiSlot? = null

    /** Slot rendered when the player can scroll downward/rightward. */
    var scrollDownButton: GuiSlot? = null

    /**
     * Defines the content viewport area within the inventory.
     *
     * @param slots ordered list of inventory positions that form the viewport
     * @param columns number of columns per row, used to calculate vertical scroll step
     */
    fun setContentArea(slots: List<Int>, columns: Int = 7) {
        this.contentSlots = slots
        this.contentColumns = columns
    }

    /** Appends a single item to the scrollable content list. */
    fun addContentItem(slot: GuiSlot) { contentItems.add(slot) }

    /** Replaces all scrollable content with the given [items]. */
    fun setContentItems(items: List<GuiSlot>) { contentItems.clear(); contentItems.addAll(items) }

    /**
     * Returns the current scroll offset for the given [player].
     *
     * @return zero-based offset, defaulting to 0 if not yet tracked
     */
    fun getOffset(player: Player): Int = playerOffsets[player.uniqueId] ?: 0

    /**
     * Calculates the maximum valid scroll offset.
     *
     * @return max offset, or 0 if no content area is configured
     */
    fun getMaxOffset(): Int {
        if (contentSlots.isEmpty()) return 0
        return maxOf(0, contentItems.size - contentSlots.size)
    }

    /**
     * Scrolls the [player]'s view upward (or leftward in horizontal mode).
     *
     * @param player target player
     * @param amount number of scroll steps
     */
    fun scrollUp(player: Player, amount: Int = 1) {
        val current = getOffset(player)
        val step = if (direction == Direction.VERTICAL) contentColumns else 1
        val newOffset = maxOf(0, current - step * amount)
        if (newOffset != current) { playerOffsets[player.uniqueId] = newOffset; update(player) }
    }

    /**
     * Scrolls the [player]'s view downward (or rightward in horizontal mode).
     *
     * @param player target player
     * @param amount number of scroll steps
     */
    fun scrollDown(player: Player, amount: Int = 1) {
        val current = getOffset(player)
        val step = if (direction == Direction.VERTICAL) contentColumns else 1
        val newOffset = minOf(getMaxOffset(), current + step * amount)
        if (newOffset != current) { playerOffsets[player.uniqueId] = newOffset; update(player) }
    }

    override fun renderFor(player: Player, inventory: Inventory) {
        inventory.clear()
        for ((index, slot) in slots) { inventory.setItem(index, slot.getItemFor(player)) }
        val offset = getOffset(player)
        for (i in contentSlots.indices) {
            val dataIdx = offset + i
            if (dataIdx < contentItems.size) {
                inventory.setItem(contentSlots[i], contentItems[dataIdx].getItemFor(player))
            }
        }
        scrollUpButton?.let { if (offset > 0) inventory.setItem(it.index, it.getItemFor(player)) }
        scrollDownButton?.let { if (offset < getMaxOffset()) inventory.setItem(it.index, it.getItemFor(player)) }
    }

    internal fun handleScrollClick(player: Player, rawSlot: Int, clickType: ClickType) {
        scrollUpButton?.let {
            if (rawSlot == it.index) {
                it.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                scrollUp(player); return
            }
        }
        scrollDownButton?.let {
            if (rawSlot == it.index) {
                it.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                scrollDown(player); return
            }
        }
        val slotIdx = contentSlots.indexOf(rawSlot)
        if (slotIdx >= 0) {
            val dataIdx = getOffset(player) + slotIdx
            if (dataIdx < contentItems.size) {
                val cs = contentItems[dataIdx]
                cs.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                cs.onClick?.invoke(player, clickType); return
            }
        }
        handleClick(player, rawSlot, clickType)
    }

    override fun close(player: Player) { playerOffsets.remove(player.uniqueId); super.close(player) }
}
