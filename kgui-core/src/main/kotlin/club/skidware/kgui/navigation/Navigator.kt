package club.skidware.kgui.navigation

import club.skidware.kgui.core.BaseGui
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages per-player GUI navigation history, enabling back/forward traversal.
 *
 * Maintains a stack-based history for each player, allowing nested GUI flows
 * without losing the previous GUI state.
 *
 * @since 1.0.0
 * @see BaseGui
 * @see KGui
 */
object Navigator {
    private val history: MutableMap<UUID, ArrayDeque<BaseGui>> = ConcurrentHashMap()

    /**
     * Opens a new GUI for the player and pushes the current GUI onto the history stack.
     *
     * If the player has no currently open KGui, only the new GUI is opened
     * (nothing is pushed).
     *
     * @param player the player to navigate
     * @param gui the destination GUI to open
     */
    fun navigate(player: Player, gui: BaseGui) {
        getCurrentGui(player)?.let { getHistory(player).addLast(it) }
        gui.open(player)
    }

    /**
     * Pops the most recent GUI from the history stack and opens it.
     *
     * @param player the player to navigate back
     * @return true if a previous GUI was found and opened, false if the history was empty
     */
    fun back(player: Player): Boolean {
        val stack = getHistory(player)
        if (stack.isEmpty()) return false
        stack.removeLast().open(player)
        return true
    }

    /**
     * Clears the entire navigation history for a player.
     *
     * Should be called when a player logs out or when the navigation flow is
     * intentionally reset to avoid memory leaks.
     *
     * @param player the player whose history should be cleared
     */
    fun clear(player: Player) { history.remove(player.uniqueId) }

    /**
     * Checks whether the player has any previous GUIs in their navigation stack.
     *
     * @param player the player to check
     * @return true if the player can navigate back
     */
    fun hasHistory(player: Player): Boolean = getHistory(player).isNotEmpty()

    /**
     * Returns how many GUIs are stacked in the player's navigation history.
     *
     * @param player the player to query
     * @return the number of entries in the history stack
     */
    fun getDepth(player: Player): Int = getHistory(player).size

    private fun getCurrentGui(player: Player): BaseGui? = player.openInventory.topInventory.holder as? BaseGui
    private fun getHistory(player: Player): ArrayDeque<BaseGui> = history.getOrPut(player.uniqueId) { ArrayDeque() }
}
