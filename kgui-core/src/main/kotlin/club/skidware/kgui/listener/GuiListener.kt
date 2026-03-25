package club.skidware.kgui.listener

import club.skidware.kgui.core.BaseGui
import club.skidware.kgui.input.AnvilGui
import club.skidware.kgui.paginated.PaginatedGui
import club.skidware.kgui.scrollable.ScrollableGui
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.PrepareAnvilEvent

/**
 * Internal Bukkit event listener that bridges vanilla inventory events to the KGui system.
 *
 * Handles click cancellation based on [BaseGui.canTakeItem], delegates clicks to
 * the appropriate GUI handler, blocks invalid drags, and triggers [BaseGui.close]
 * on inventory close. Registered automatically by [KGui.setup][club.skidware.kgui.KGui.setup].
 *
 * @since 1.0.0
 */
class GuiListener : Listener {

    /**
     * Routes inventory clicks to the correct GUI handler based on the holder type.
     *
     * Cancels the event when the clicked slot is not takeable, ensuring items
     * stay in the GUI unless explicitly allowed.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        val player = event.whoClicked as? Player ?: return

        when (holder) {
            is PaginatedGui -> {
                if (event.rawSlot < holder.size) {
                    event.isCancelled = !holder.canTakeItem(event.rawSlot)
                    holder.handlePageClick(player, event.rawSlot, event.click)
                }
            }
            is ScrollableGui -> {
                if (event.rawSlot < holder.size) {
                    event.isCancelled = !holder.canTakeItem(event.rawSlot)
                    holder.handleScrollClick(player, event.rawSlot, event.click)
                }
            }
            is BaseGui -> {
                if (event.rawSlot < holder.size) {
                    event.isCancelled = !holder.canTakeItem(event.rawSlot)
                    holder.handleClick(player, event.rawSlot, event.click)
                }
            }
        }
    }

    /**
     * Blocks drag operations that would place items into non-takeable GUI slots.
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is BaseGui) {
            val guiSlots = event.rawSlots.filter { it < holder.size }
            if (guiSlots.any { !holder.canTakeItem(it) }) {
                event.isCancelled = true
            }
        }
    }

    /** Notifies the GUI when a viewer closes the inventory, triggering cleanup. */
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        val player = event.player as? Player ?: return
        if (holder is BaseGui && holder.isViewer(player)) {
            holder.close(player)
        }
    }

    /** Forwards anvil result preparation to the [AnvilGui] for custom input handling. */
    @EventHandler
    fun onAnvilPrepare(event: PrepareAnvilEvent) {
        val holder = event.inventory.holder
        if (holder is AnvilGui) {
            val resultItem = event.result
            event.viewers.filterIsInstance<Player>().firstOrNull()?.let { player ->
                holder.handleAnvilResult(player, resultItem)
            }
        }
    }
}
