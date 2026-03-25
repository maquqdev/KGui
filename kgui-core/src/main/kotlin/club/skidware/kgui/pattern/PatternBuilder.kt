package club.skidware.kgui.pattern

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.slot.GuiSlot
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * DSL builder for defining GUI layouts using a visual character grid.
 *
 * Each row of the inventory is represented as a 9-character string, where
 * characters are mapped to slot configurations via the [means] infix function.
 * The `.` and space characters are reserved as "empty" markers.
 *
 * ```kotlin
 * pattern {
 *     line("BBBBBBBBB")
 *     line("B.......B")
 *     line("BBBBBBBBB")
 *
 *     'B' means filler(Material.BLACK_STAINED_GLASS_PANE)
 * }
 * ```
 *
 * @since 1.0.0
 * @see GuiTemplate
 * @see PatternSlotConfig
 */
@GuiDslMarker
class PatternBuilder {
    private val patternLines: MutableList<String> = mutableListOf()
    private val mappings: MutableMap<Char, PatternSlotConfig> = mutableMapOf()

    /**
     * Adds a single row to the pattern. Spaces are stripped for convenience.
     *
     * @param pattern a string of up to 9 characters representing one inventory row
     */
    fun line(pattern: String) { patternLines.add(pattern.replace(" ", "")) }

    /**
     * Adds multiple rows to the pattern at once.
     *
     * @param patterns row strings, one per inventory row
     */
    fun lines(vararg patterns: String) { patterns.forEach { line(it) } }

    /**
     * Maps a character to a [PatternSlotConfig], defining what that character
     * represents in the pattern grid.
     *
     * @param config the slot configuration for this character
     */
    infix fun Char.means(config: PatternSlotConfig) { mappings[this] = config }

    /**
     * Creates a non-interactive decorative slot configuration.
     *
     * @param material the filler material
     * @param block optional [ItemBuilder] configuration
     * @return a [PatternSlotConfig] with no click handler
     */
    fun filler(material: Material, block: ItemBuilder.() -> Unit = {}): PatternSlotConfig =
        PatternSlotConfig(item = ItemBuilder(material).apply(block).build())

    /**
     * Creates an interactive slot configuration using a builder DSL.
     *
     * @param material the base material for the slot's item
     * @param block [ClickablePatternBuilder] configuration lambda
     * @return a [PatternSlotConfig] with click handling
     */
    fun clickable(material: Material, block: ClickablePatternBuilder.() -> Unit): PatternSlotConfig =
        ClickablePatternBuilder(material).apply(block).build()

    /**
     * Creates an interactive slot configuration from a pre-built [ItemStack].
     *
     * @param itemStack the item to display
     * @param onClick optional click handler
     * @return a [PatternSlotConfig] with the given item and handler
     */
    fun clickable(itemStack: ItemStack, onClick: ((Player, ClickType) -> Unit)? = null): PatternSlotConfig =
        PatternSlotConfig(item = itemStack, onClick = onClick)

    /**
     * Creates a slot configuration with a per-player dynamic item provider.
     *
     * @param provider function producing a player-specific [ItemStack]
     * @param onClick optional click handler
     * @return a [PatternSlotConfig] with dynamic rendering
     */
    fun dynamic(provider: (Player) -> ItemStack, onClick: ((Player, ClickType) -> Unit)? = null): PatternSlotConfig =
        PatternSlotConfig(dynamicItem = provider, onClick = onClick)

    /**
     * Converts the pattern lines and character mappings into a map of slot
     * index to [GuiSlot].
     *
     * @return the resolved slot map
     */
    internal fun buildSlots(): Map<Int, GuiSlot> {
        val result = mutableMapOf<Int, GuiSlot>()
        for ((rowIndex, line) in patternLines.withIndex()) {
            for ((colIndex, char) in line.withIndex()) {
                if (char == '.' || char == ' ') continue
                val config = mappings[char] ?: continue
                val slotIndex = rowIndex * 9 + colIndex
                result[slotIndex] = GuiSlot(
                    index = slotIndex, item = config.item,
                    dynamicItem = config.dynamicItem, onClick = config.onClick,
                    sound = config.sound
                )
            }
        }
        return result
    }
}

/**
 * Holds the configuration for a single pattern-mapped slot.
 *
 * @param item a static display item
 * @param dynamicItem a per-player item provider (takes precedence over [item])
 * @param onClick handler invoked when the slot is clicked
 * @param sound feedback sound played on click
 * @since 1.0.0
 * @see PatternBuilder
 */
data class PatternSlotConfig(
    val item: ItemStack? = null,
    val dynamicItem: ((Player) -> ItemStack)? = null,
    val onClick: ((Player, ClickType) -> Unit)? = null,
    val sound: Sound? = null
)

/**
 * DSL builder for creating a clickable [PatternSlotConfig] with item customisation,
 * click handling, and optional sound feedback.
 *
 * @param material the base material for the slot's item
 * @since 1.0.0
 * @see PatternBuilder.clickable
 */
@GuiDslMarker
class ClickablePatternBuilder(private val material: Material) {
    private var itemBuilder: ItemBuilder.() -> Unit = {}
    private var onClick: ((Player, ClickType) -> Unit)? = null
    private var sound: Sound? = null

    /**
     * Configures the display item via an [ItemBuilder] lambda.
     *
     * @param block the item configuration
     */
    fun item(block: ItemBuilder.() -> Unit) { this.itemBuilder = block }

    /**
     * Sets the click handler for this slot.
     *
     * @param handler callback receiving the clicking player and click type
     */
    fun onClick(handler: (Player, ClickType) -> Unit) { this.onClick = handler }

    /**
     * Sets the feedback sound played on click.
     *
     * @param sound the Bukkit sound to play
     */
    fun sound(sound: Sound) { this.sound = sound }

    /**
     * Builds the final [PatternSlotConfig] from the current configuration.
     *
     * @return the constructed slot config
     */
    internal fun build(): PatternSlotConfig = PatternSlotConfig(
        item = ItemBuilder(material).apply(itemBuilder).build(),
        onClick = onClick, sound = sound
    )
}
