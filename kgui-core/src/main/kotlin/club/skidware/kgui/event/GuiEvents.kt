package club.skidware.kgui.event

import club.skidware.kgui.core.BaseGui
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.inventory.ClickType

/**
 * Fired when a player is about to see a KGui inventory.
 *
 * Cancelling this event prevents the GUI from being opened.
 *
 * @param player the player opening the GUI
 * @param gui the GUI being opened
 * @since 1.0.0
 * @see GuiCloseEvent
 */
class GuiOpenEvent(
    val player: Player,
    val gui: BaseGui
) : Event(), Cancellable {
    private var cancelled = false
    override fun getHandlers(): HandlerList = handlerList
    override fun isCancelled(): Boolean = cancelled
    override fun setCancelled(cancel: Boolean) { cancelled = cancel }
    companion object { @JvmStatic val handlerList = HandlerList() }
}

/**
 * Fired after a player's KGui inventory has been closed.
 *
 * This event is informational and cannot be cancelled.
 *
 * @param player the player who closed the GUI
 * @param gui the GUI that was closed
 * @since 1.0.0
 * @see GuiOpenEvent
 */
class GuiCloseEvent(
    val player: Player,
    val gui: BaseGui
) : Event() {
    override fun getHandlers(): HandlerList = handlerList
    companion object { @JvmStatic val handlerList = HandlerList() }
}

/**
 * Fired when a player clicks inside a KGui inventory.
 *
 * Cancelling this event prevents the click from being processed by
 * per-slot handlers, but the Bukkit [InventoryClickEvent][org.bukkit.event.inventory.InventoryClickEvent]
 * is handled separately by [GuiListener][club.skidware.kgui.listener.GuiListener].
 *
 * @param player the player who clicked
 * @param gui the GUI that received the click
 * @param slot the raw slot index that was clicked
 * @param clickType the type of click performed
 * @since 1.0.0
 */
class GuiClickEvent(
    val player: Player,
    val gui: BaseGui,
    val slot: Int,
    val clickType: ClickType
) : Event(), Cancellable {
    private var cancelled = false
    override fun getHandlers(): HandlerList = handlerList
    override fun isCancelled(): Boolean = this.cancelled
    override fun setCancelled(cancel: Boolean) { this.cancelled = cancel }
    companion object { @JvmStatic val handlerList = HandlerList() }
}

/**
 * Fired when a KGui inventory is re-rendered.
 *
 * When [player] is null, the update was triggered for all viewers.
 *
 * @param gui the GUI being updated
 * @param player the specific player being updated, or null for a global update
 * @since 1.0.0
 */
class GuiUpdateEvent(
    val gui: BaseGui,
    val player: Player?
) : Event() {
    override fun getHandlers(): HandlerList = handlerList
    companion object { @JvmStatic val handlerList = HandlerList() }
}
