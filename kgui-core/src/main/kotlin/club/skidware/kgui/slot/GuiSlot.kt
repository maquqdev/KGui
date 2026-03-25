package club.skidware.kgui.slot

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * Immutable configuration for a single inventory slot within a GUI.
 *
 * Each slot can display a static [item], a per-player [dynamicItem], respond to
 * clicks, play a feedback [sound], run a frame-based [animation], and optionally
 * override the GUI-level interactability via [takeable].
 *
 * @param index the zero-based inventory slot position
 * @param item a static item to display; ignored when [dynamicItem] is set
 * @param dynamicItem a per-player item provider, evaluated on every render
 * @param onClick handler invoked when a player clicks this slot
 * @param sound feedback sound played on click
 * @param animation frame-based animation that cycles this slot's display item
 * @param takeable per-slot override for item extraction; `null` defers to the GUI default
 * @since 1.0.0
 * @see SlotBuilder
 */
data class GuiSlot(
    val index: Int,
    val item: ItemStack? = null,
    val dynamicItem: ((Player) -> ItemStack)? = null,
    val onClick: ((Player, ClickType) -> Unit)? = null,
    val sound: Sound? = null,
    val animation: SlotAnimation? = null,
    val takeable: Boolean? = null
) {
    /**
     * Resolves the item to display for a given player.
     *
     * Prefers the [dynamicItem] provider when present; otherwise falls back to [item].
     *
     * @param player the player viewing this slot
     * @return the resolved item, or null if neither source is configured
     */
    fun getItemFor(player: Player): ItemStack? =
        dynamicItem?.invoke(player) ?: item
}
