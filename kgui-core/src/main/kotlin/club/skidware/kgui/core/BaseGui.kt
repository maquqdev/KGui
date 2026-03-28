package club.skidware.kgui.core

import club.skidware.kgui.slot.GuiSlot
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Shared foundation for all inventory-based GUIs.
 *
 * Manages viewer tracking, per-player inventories, slot rendering, click
 * delegation, and animation scheduling. Concrete GUI types (chest, paginated,
 * etc.) extend this class and may override [createInventory] or [renderFor]
 * to customize behaviour.
 *
 * @param plugin the owning plugin, used for scheduler tasks and event registration
 * @param title the display title rendered at the top of the inventory window
 * @param rows the number of inventory rows (each row has 9 slots)
 * @since 1.0.0
 * @see Gui
 */
abstract class BaseGui(
    override val plugin: JavaPlugin,
    val title: Component,
    val rows: Int
) : Gui, InventoryHolder {

    override val size: Int get() = rows * 9
    override val slots: MutableMap<Int, GuiSlot> = ConcurrentHashMap()
    override var interactable: Boolean = false

    /** Per-player inventory instances, allowing player-specific item rendering. */
    protected val inventories: MutableMap<UUID, Inventory> = ConcurrentHashMap()

    /** UUIDs of players currently viewing this GUI. */
    protected val viewers: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    /** Callback invoked after a player opens this GUI. */
    var onOpenHandler: ((Player) -> Unit)? = null

    /** Callback invoked after a player's view of this GUI is closed. */
    var onCloseHandler: ((Player) -> Unit)? = null

    /** Callback invoked on every click, before per-slot handlers run. */
    var onClickHandler: ((Player, Int, ClickType) -> Unit)? = null

    private var animationTask: BukkitTask? = null
    private val animatedSlots: MutableSet<Int> = mutableSetOf()

    /**
     * Registers or replaces a slot configuration in this GUI.
     *
     * If the slot carries an animation, the animation scheduler is started
     * automatically when the first animated slot is added.
     *
     * @param slot the slot configuration to register
     * @see GuiSlot
     */
    fun setSlot(slot: GuiSlot) {
        this.slots[slot.index] = slot
        if (slot.animation != null) {
            this.animatedSlots.add(slot.index)
            startAnimationIfNeeded()
        }
    }

    /**
     * Determines whether a player can extract an item from a specific slot.
     *
     * Resolution order: per-slot [GuiSlot.takeable] overrides GUI-level [interactable].
     *
     * @param rawSlot the inventory slot index to check
     * @return true if the item can be taken by the player
     */
    override fun canTakeItem(rawSlot: Int): Boolean {
        val slot = this.slots[rawSlot]
        // Per-slot takeable overrides GUI-level interactable
        slot?.takeable?.let { return it }
        // Fall back to GUI-level setting
        return this.interactable
    }

    /**
     * Creates the inventory, renders slots, and opens it for the player.
     *
     * Triggers [onOpenHandler] after the inventory is shown.
     *
     * @param player the player who will see the GUI
     */
    override fun open(player: Player) {
        val inv = createInventory(player)
        this.inventories[player.uniqueId] = inv
        this.viewers.add(player.uniqueId)
        renderFor(player, inv)
        player.openInventory(inv)
        this.onOpenHandler?.invoke(player)
    }

    /**
     * Removes the player from viewer tracking and invokes [onCloseHandler].
     *
     * If no viewers remain, all running slot animations are stopped to
     * avoid unnecessary tick processing.
     *
     * @param player the player leaving the GUI
     */
    override fun close(player: Player) {
        this.viewers.remove(player.uniqueId)
        this.inventories.remove(player.uniqueId)
        this.onCloseHandler?.invoke(player)
        if (viewers.isEmpty()) stopAnimation()
    }

    /** Re-renders the GUI for every online viewer. */
    override fun update() {
        this.viewers.mapNotNull { Bukkit.getPlayer(it) }.forEach { update(it) }
    }

    /**
     * Re-renders the GUI for a single player.
     *
     * @param player the player whose view should be refreshed
     */
    override fun update(player: Player) {
        val inv = this.inventories[player.uniqueId] ?: return
        renderFor(player, inv)
    }

    /** @return an immutable snapshot of all online players currently viewing this GUI */
    override fun getGuiViewers(): Set<Player> =
        this.viewers.mapNotNull { Bukkit.getPlayer(it) }.toSet()

    /**
     * Returns the player's inventory for this GUI, creating one if absent.
     *
     * @param player the target player
     * @return the Bukkit inventory backing this GUI for the given player
     */
    override fun getInventory(player: Player): Inventory =
        this.inventories[player.uniqueId] ?: createInventory(player)

    /**
     * Required by [InventoryHolder]. Returns a fresh inventory instance
     * without player context -- prefer [getInventory] with a player argument.
     */
    @Suppress("DEPRECATION")
    override fun getInventory(): Inventory =
        Bukkit.createInventory(this, size, title)

    /**
     * Factory method for creating a player's inventory instance.
     *
     * Override to customise inventory creation (e.g. different titles per player).
     *
     * @param player the player for whom the inventory is created
     * @return a new Bukkit inventory
     */
    protected open fun createInventory(player: Player): Inventory =
        Bukkit.createInventory(this, size, title)

    /**
     * Populates every slot of the inventory with the appropriate item.
     *
     * Override to add custom rendering logic (e.g. pagination controls).
     *
     * @param player the player for whom items may be personalised
     * @param inventory the Bukkit inventory to populate
     */
    protected open fun renderFor(player: Player, inventory: Inventory) {
        for (i in 0 until this.size) {
            val slot = this.slots[i]
            if (slot != null) {
                inventory.setItem(i, slot.getItemFor(player))
            }
        }
    }

    /**
     * Dispatches a click event to the GUI-level handler and the per-slot handler.
     *
     * Also plays the slot's feedback sound, if configured.
     *
     * @param player the player who clicked
     * @param rawSlot the raw inventory slot index that was clicked
     * @param clickType the type of click performed
     */
    internal fun handleClick(player: Player, rawSlot: Int, clickType: ClickType) {
        if (rawSlot < 0 || rawSlot >= size) return
        val slot = this.slots[rawSlot]
        this.onClickHandler?.invoke(player, rawSlot, clickType)
        slot?.let {
            it.sound?.let { sound -> player.playSound(player.location, sound, 1f, 1f) }
            it.onClick?.invoke(player, clickType)
        }
    }

    /**
     * Checks whether a player is currently tracked as a viewer of this GUI.
     *
     * @param player the player to check
     * @return true if the player is viewing this GUI
     */
    internal fun isViewer(player: Player): Boolean =
        this.viewers.contains(player.uniqueId)

    private fun startAnimationIfNeeded() {
        if (this.animationTask != null || this.animatedSlots.isEmpty()) return
        val interval = slots.values
            .mapNotNull { it.animation?.intervalTicks }
            .minOrNull() ?: 20L
        this.animationTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            for (slotIndex in this.animatedSlots) {
                val slot = this.slots[slotIndex] ?: continue
                val frame = slot.animation?.nextFrame() ?: continue
                for (viewerUuid in this.viewers) {
                    this.inventories[viewerUuid]?.setItem(slotIndex, frame.item)
                }
            }
        }, 0L, interval)
    }

    private fun stopAnimation() {
        this.animationTask?.cancel()
        this.animationTask = null
    }
}
