package club.skidware.kgui.input

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing an [AnvilGui] for text input capture.
 *
 * ```kotlin
 * KGui.anvil(plugin, "<gold>Enter Name") {
 *     inputItem(Material.NAME_TAG) { name("<gray>Type here...") }
 *     defaultText("Steve")
 *     onSubmit { player, text ->
 *         player.sendMessage("You entered: $text")
 *         true // close after submit
 *     }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted anvil title
 * @since 1.0.0
 * @see AnvilGui
 */
@GuiDslMarker
class AnvilGuiBuilder(private val plugin: JavaPlugin, private val title: String) {
    private var inputItem: ItemStack? = null
    private var defaultText: String = ""
    private var onSubmit: ((Player, String) -> Boolean)? = null
    private var onClose: ((Player) -> Unit)? = null

    /**
     * Sets the item placed in the left anvil slot.
     *
     * @param material item material
     * @param block optional item customization
     */
    fun inputItem(material: Material, block: ItemBuilder.() -> Unit = {}) { inputItem = ItemBuilder(material).apply(block).build() }

    /** Sets the pre-filled rename text shown in the anvil field. */
    fun defaultText(text: String) { this.defaultText = text }

    /**
     * Registers the submission handler.
     *
     * @param handler receives the player and entered text; return `true` to close the GUI
     */
    fun onSubmit(handler: (Player, String) -> Boolean) { this.onSubmit = handler }

    /** Registers a callback invoked when the player closes without submitting. */
    fun onClose(handler: (Player) -> Unit) { this.onClose = handler }

    internal fun build(): AnvilGui {
        val gui = AnvilGui(plugin, MiniMessage.miniMessage().deserialize(title))
        inputItem?.let { gui.inputItem = it }
        gui.defaultText = defaultText
        gui.onSubmit = onSubmit
        gui.onCloseAction = onClose
        return gui
    }
}
