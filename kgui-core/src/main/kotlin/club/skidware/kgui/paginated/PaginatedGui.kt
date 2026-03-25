package club.skidware.kgui.paginated

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
 * Chest-based GUI that splits large item collections across multiple pages.
 *
 * Tracks each player's current page independently, allowing concurrent viewers
 * to browse at their own pace. Navigation buttons and page info are rendered
 * conditionally based on the current page position.
 *
 * Use [PaginatedGuiBuilder] via [club.skidware.kgui.KGui.paginated] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @param rows number of rows (clamped to 2-6; at least 2 needed for navigation)
 * @since 1.0.0
 * @see PaginatedGuiBuilder
 */
class PaginatedGui(
    plugin: JavaPlugin,
    title: Component,
    rows: Int = 6
) : BaseGui(plugin, title, rows.coerceIn(2, 6)) {

    private val pageItems: MutableList<GuiSlot> = mutableListOf()
    private val playerPages: MutableMap<UUID, Int> = ConcurrentHashMap()
    private var itemSlots: List<Int> = emptyList()

    /** Slot rendered on non-first pages to navigate backward. */
    var previousButton: GuiSlot? = null

    /** Slot rendered on non-last pages to navigate forward. */
    var nextButton: GuiSlot? = null

    /** Factory that produces a page indicator slot given 1-based current and max page numbers. */
    var pageInfoSlot: ((currentPage: Int, maxPage: Int) -> GuiSlot)? = null

    /**
     * Defines which inventory positions display paginated content.
     *
     * The order of [slots] determines the fill order on each page.
     */
    fun setItemSlots(slots: List<Int>) { this.itemSlots = slots }

    /** @see setItemSlots */
    fun setItemSlots(vararg slots: Int) { this.itemSlots = slots.toList() }

    /** Appends a single item to the paginated data set. */
    fun addPageItem(slot: GuiSlot) { pageItems.add(slot) }

    /** Appends multiple items to the paginated data set. */
    fun addPageItems(items: List<GuiSlot>) { pageItems.addAll(items) }

    /** Removes all paginated items, resetting content to empty. */
    fun clearPageItems() { pageItems.clear() }

    /**
     * Returns the zero-based page index for the given [player].
     *
     * @return current page, defaulting to 0 if not yet tracked
     */
    fun getPage(player: Player): Int = playerPages[player.uniqueId] ?: 0

    /**
     * Calculates the highest valid page index based on current item count.
     *
     * @return zero-based max page index, or 0 if no item slots are configured
     */
    fun getMaxPage(): Int {
        if (itemSlots.isEmpty()) return 0
        return maxOf(0, (pageItems.size - 1) / itemSlots.size)
    }

    /** Advances the [player] to the next page if not already on the last page. */
    fun nextPage(player: Player) {
        val current = getPage(player)
        if (current < getMaxPage()) {
            playerPages[player.uniqueId] = current + 1
            update(player)
        }
    }

    /** Moves the [player] to the previous page if not already on the first page. */
    fun previousPage(player: Player) {
        val current = getPage(player)
        if (current > 0) {
            playerPages[player.uniqueId] = current - 1
            update(player)
        }
    }

    /**
     * Jumps the [player] directly to the specified [page], clamped to valid bounds.
     *
     * @param player target player
     * @param page zero-based page index
     */
    fun setPage(player: Player, page: Int) {
        playerPages[player.uniqueId] = page.coerceIn(0, getMaxPage())
        update(player)
    }

    override fun renderFor(player: Player, inventory: Inventory) {
        inventory.clear()
        for ((index, slot) in slots) {
            inventory.setItem(index, slot.getItemFor(player))
        }

        val page = getPage(player)
        val start = page * itemSlots.size
        for (i in itemSlots.indices) {
            val dataIdx = start + i
            if (dataIdx < pageItems.size) {
                inventory.setItem(itemSlots[i], pageItems[dataIdx].getItemFor(player))
            } else {
                inventory.setItem(itemSlots[i], null)
            }
        }

        previousButton?.let { if (page > 0) inventory.setItem(it.index, it.getItemFor(player)) }
        nextButton?.let { if (page < getMaxPage()) inventory.setItem(it.index, it.getItemFor(player)) }
        pageInfoSlot?.let { provider ->
            val slot = provider(page + 1, getMaxPage() + 1)
            inventory.setItem(slot.index, slot.getItemFor(player))
        }
    }

    override fun close(player: Player) {
        playerPages.remove(player.uniqueId)
        super.close(player)
    }

    internal fun handlePageClick(player: Player, rawSlot: Int, clickType: ClickType) {
        previousButton?.let {
            if (rawSlot == it.index) {
                it.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                previousPage(player); return
            }
        }
        nextButton?.let {
            if (rawSlot == it.index) {
                it.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                nextPage(player); return
            }
        }
        val slotIdx = itemSlots.indexOf(rawSlot)
        if (slotIdx >= 0) {
            val dataIdx = getPage(player) * itemSlots.size + slotIdx
            if (dataIdx < pageItems.size) {
                val ps = pageItems[dataIdx]
                ps.sound?.let { s -> player.playSound(player.location, s, 1f, 1f) }
                ps.onClick?.invoke(player, clickType); return
            }
        }
        handleClick(player, rawSlot, clickType)
    }
}
