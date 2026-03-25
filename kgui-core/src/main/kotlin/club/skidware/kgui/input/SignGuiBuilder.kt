package club.skidware.kgui.input

import club.skidware.kgui.dsl.GuiDslMarker
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [SignGui] for multi-line text input.
 *
 * ```kotlin
 * KGui.sign(plugin) {
 *     line(0, "Enter your")
 *     line(1, "name below:")
 *     onComplete { player, lines ->
 *         player.sendMessage("You wrote: ${lines.joinToString()}")
 *     }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @since 1.0.0
 * @see SignGui
 */
@GuiDslMarker
class SignGuiBuilder(private val plugin: JavaPlugin) {
    private val lines: Array<String> = Array(4) { "" }
    private var onComplete: ((Player, List<String>) -> Unit)? = null

    /**
     * Sets a pre-filled line on the sign.
     *
     * @param index line index (0-3)
     * @param text plain text content
     */
    fun line(index: Int, text: String) { if (index in 0..3) lines[index] = text }

    /**
     * Registers the completion handler called when the player closes the sign editor.
     *
     * @param handler receives the player and all 4 sign lines as strings
     */
    fun onComplete(handler: (Player, List<String>) -> Unit) { this.onComplete = handler }

    internal fun build(): SignGui {
        val gui = SignGui(plugin)
        lines.forEachIndexed { i, t -> gui.line(i, t) }
        gui.onComplete = onComplete
        return gui
    }
}
