package club.skidware.kgui.input

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Sign-based GUI that captures multi-line text input from players.
 *
 * Temporarily places a sign block at the player's location to trigger the
 * native sign editor. The block is removed automatically after 2 ticks.
 *
 * Use [SignGuiBuilder] via [club.skidware.kgui.KGui.sign] for DSL-based construction.
 *
 * @param plugin owning plugin instance for scheduling cleanup
 * @since 1.0.0
 * @see SignGuiBuilder
 */
class SignGui(val plugin: JavaPlugin) {
    private var lines: Array<Component> = Array(4) { Component.empty() }

    /** Called when the player closes the sign editor, receiving all 4 lines as strings. */
    var onComplete: ((Player, List<String>) -> Unit)? = null

    /**
     * Sets a pre-filled line on the sign.
     *
     * @param index line index (0-3)
     * @param text plain text content
     * @return this instance for chaining
     */
    fun line(index: Int, text: String) = apply { if (index in 0..3) lines[index] = Component.text(text) }

    /**
     * Sets a pre-filled line on the sign using an Adventure [Component].
     *
     * @param index line index (0-3)
     * @param component rich text content
     * @return this instance for chaining
     */
    fun line(index: Int, component: Component) = apply { if (index in 0..3) lines[index] = component }

    /** Opens the sign editor for the given [player]. */
    fun open(player: Player) { player.openSign(createSign(player)) }

    private fun createSign(player: Player): org.bukkit.block.Sign {
        val loc = player.location.clone(); loc.y = loc.world.minHeight.toDouble()
        loc.block.type = Material.OAK_SIGN
        val sign = loc.block.state as org.bukkit.block.Sign
        lines.forEachIndexed { i, l -> sign.line(i, l) }
        sign.update(true, false)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { loc.block.type = Material.AIR }, 2L)
        return sign
    }
}
