package club.skidware.kgui.template

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import club.skidware.kgui.pattern.PatternBuilder
import club.skidware.kgui.slot.GuiSlot
import club.skidware.kgui.slot.SlotBuilder
import org.bukkit.Material

/**
 * Reusable visual template that defines common GUI layout elements.
 *
 * Templates allow you to standardise borders, fills, navigation buttons, and
 * fixed decorative slots across multiple GUIs. Register templates globally via
 * [KGui.template][club.skidware.kgui.KGui.template] and apply them to individual
 * GUI builders.
 *
 * @param name unique identifier used for registry lookup via [KGui.getTemplate]
 * @since 1.0.0
 * @see PatternBuilder
 * @see KGui
 */
@GuiDslMarker
class GuiTemplate(val name: String) {

    /** Material used for the outer border ring. Null means no border. */
    var borderMaterial: Material? = null
    internal var borderBuilder: (ItemBuilder.() -> Unit)? = null

    /** Material used to fill all empty (non-slot, non-border) positions. */
    var fillMaterial: Material? = null
    internal var fillBuilder: (ItemBuilder.() -> Unit)? = null
    internal val fixedSlots: MutableMap<Int, SlotBuilder> = mutableMapOf()

    /** Slot index for the "previous page" button, or null to omit it. */
    var previousButtonSlot: Int? = null
    var previousButtonMaterial: Material = Material.ARROW
    internal var previousButtonBuilder: (ItemBuilder.() -> Unit) = { name("<gray>Previous Page") }

    /** Slot index for the "next page" button, or null to omit it. */
    var nextButtonSlot: Int? = null
    var nextButtonMaterial: Material = Material.ARROW
    internal var nextButtonBuilder: (ItemBuilder.() -> Unit) = { name("<gray>Next Page") }

    /** Slot index for the "close" button, or null to omit it. */
    var closeButtonSlot: Int? = null
    var closeButtonMaterial: Material = Material.BARRIER
    internal var closeButtonBuilder: (ItemBuilder.() -> Unit) = { name("<red>Close") }

    /** Slot index for the "back" navigation button, or null to omit it. */
    var backButtonSlot: Int? = null
    var backButtonMaterial: Material = Material.ARROW
    internal var backButtonBuilder: (ItemBuilder.() -> Unit) = { name("<gray>Back") }
    internal var patternSlots: Map<Int, GuiSlot> = emptyMap()

    /**
     * Defines the border material and optional item customisation.
     *
     * The border fills the outermost ring of slots in the inventory.
     *
     * @param material the material to use for border items
     * @param block optional [ItemBuilder] configuration (e.g. name, glow)
     */
    fun border(material: Material, block: ItemBuilder.() -> Unit = {}) { borderMaterial = material; borderBuilder = block }

    /**
     * Defines the fill material for all unoccupied interior slots.
     *
     * @param material the material to use for filler items
     * @param block optional [ItemBuilder] configuration
     */
    fun fill(material: Material, block: ItemBuilder.() -> Unit = {}) { fillMaterial = material; fillBuilder = block }

    /**
     * Configures a fixed slot that will be applied to every GUI using this template.
     *
     * @param index the slot position
     * @param block [SlotBuilder] configuration lambda
     */
    fun slot(index: Int, block: SlotBuilder.() -> Unit) { fixedSlots[index] = SlotBuilder(index).apply(block) }

    /**
     * Configures the "previous page" navigation button.
     *
     * @param slot the slot index to place the button
     * @param material the button material (default [Material.ARROW])
     * @param block optional [ItemBuilder] configuration
     */
    fun previousButton(slot: Int, material: Material = Material.ARROW, block: ItemBuilder.() -> Unit = { name("<gray>Previous Page") }) { previousButtonSlot = slot; previousButtonMaterial = material; previousButtonBuilder = block }

    /**
     * Configures the "next page" navigation button.
     *
     * @param slot the slot index to place the button
     * @param material the button material (default [Material.ARROW])
     * @param block optional [ItemBuilder] configuration
     */
    fun nextButton(slot: Int, material: Material = Material.ARROW, block: ItemBuilder.() -> Unit = { name("<gray>Next Page") }) { nextButtonSlot = slot; nextButtonMaterial = material; nextButtonBuilder = block }

    /**
     * Configures the "close" button that closes the GUI for the player.
     *
     * @param slot the slot index to place the button
     * @param material the button material (default [Material.BARRIER])
     * @param block optional [ItemBuilder] configuration
     */
    fun closeButton(slot: Int, material: Material = Material.BARRIER, block: ItemBuilder.() -> Unit = { name("<red>Close") }) { closeButtonSlot = slot; closeButtonMaterial = material; closeButtonBuilder = block }

    /**
     * Configures the "back" navigation button that returns to the previous GUI.
     *
     * @param slot the slot index to place the button
     * @param material the button material (default [Material.ARROW])
     * @param block optional [ItemBuilder] configuration
     */
    fun backButton(slot: Int, material: Material = Material.ARROW, block: ItemBuilder.() -> Unit = { name("<gray>Back") }) { backButtonSlot = slot; backButtonMaterial = material; backButtonBuilder = block }

    /**
     * Defines a character-mapped layout pattern for this template.
     *
     * Patterns provide a visual, grid-based way to arrange slots instead of
     * specifying individual slot indices.
     *
     * @param block [PatternBuilder] configuration lambda
     * @see PatternBuilder
     */
    fun pattern(block: PatternBuilder.() -> Unit) { patternSlots = PatternBuilder().apply(block).buildSlots() }
}
