package club.skidware.kgui.core

import club.skidware.kgui.slot.GuiSlot
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

/**
 * Core contract for all GUI types in KGui.
 *
 * Every GUI implementation -- chest, paginated, scrollable, etc. -- must satisfy
 * this interface, ensuring a uniform lifecycle (open/close/update) and consistent
 * slot-based interaction model across the library.
 *
 * @since 1.0.0
 * @see BaseGui
 */
interface Gui {

    /** The owning plugin, used for event registration and scheduler tasks. */
    val plugin: JavaPlugin

    /** Total number of inventory slots (rows * 9 for chest-type GUIs). */
    val size: Int

    /** Registered slot configurations keyed by slot index. */
    val slots: Map<Int, GuiSlot>

    /**
     * Whether players can freely interact with (take/place) items in this GUI.
     *
     * Acts as the default policy; individual [GuiSlot.takeable] settings override this.
     */
    var interactable: Boolean

    /**
     * Creates the inventory and displays it to the player.
     *
     * @param player the player who will see the GUI
     */
    fun open(player: Player)

    /**
     * Cleans up viewer state when a player's inventory is closed.
     *
     * @param player the player leaving the GUI
     */
    fun close(player: Player)

    /** Re-renders the GUI contents for every current viewer. */
    fun update()

    /**
     * Re-renders the GUI contents for a single player.
     *
     * @param player the player whose view should be refreshed
     */
    fun update(player: Player)

    /**
     * Returns all online players currently viewing this GUI.
     *
     * @return an immutable snapshot of current viewers
     */
    fun getGuiViewers(): Set<Player>

    /**
     * Retrieves (or creates) the Bukkit inventory associated with a player.
     *
     * @param player the player whose inventory instance is needed
     * @return the inventory backing this GUI for the given player
     */
    fun getInventory(player: Player): Inventory

    /**
     * Determines whether a player can extract an item from a specific slot.
     *
     * Resolution order: per-slot [GuiSlot.takeable] overrides GUI-level [interactable].
     * If the slot has no explicit takeable setting, falls back to the GUI default.
     *
     * @param rawSlot the inventory slot index to check
     * @return true if the item can be taken by the player
     */
    fun canTakeItem(rawSlot: Int): Boolean
}
