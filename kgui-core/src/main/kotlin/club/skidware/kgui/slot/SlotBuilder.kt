package club.skidware.kgui.slot

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * DSL builder for configuring a single [GuiSlot].
 *
 * Provides a fluent API to define the display item, click behaviour, sound
 * feedback, animation, and interactability of a slot. Used inside GUI builder
 * lambdas via the `slot(index) { ... }` syntax.
 *
 * @param index the zero-based slot position in the inventory
 * @since 1.0.0
 * @see GuiSlot
 */
@GuiDslMarker
class SlotBuilder(private val index: Int) {

    private var item: ItemStack? = null
    private var dynamicItem: ((Player) -> ItemStack)? = null
    private var clickHandler: ((Player, ClickType) -> Unit)? = null
    private var sound: Sound? = null
    private var takeable: Boolean? = null
    private var animation: SlotAnimation? = null

    /**
     * Sets the slot's display item using a material and an optional [ItemBuilder] configuration.
     *
     * @param material the base material for the item
     * @param block optional builder lambda for name, lore, enchantments, etc.
     */
    fun item(material: Material, block: ItemBuilder.() -> Unit = {}) {
        this.item = ItemBuilder(material).apply(block).build()
    }

    /**
     * Sets the slot's display item from a pre-built [ItemStack].
     *
     * @param itemStack the item to display
     */
    fun item(itemStack: ItemStack) {
        this.item = itemStack
    }

    /**
     * Provides a per-player item that is resolved on every render.
     *
     * Use this when the displayed item depends on the viewer's state
     * (e.g. showing their balance or online status).
     *
     * @param provider function that produces an [ItemStack] for the given player
     */
    fun dynamicItem(provider: (Player) -> ItemStack) {
        this.dynamicItem = provider
    }

    /**
     * Registers a click handler that receives both the player and click type.
     *
     * @param handler the callback invoked on click
     */
    fun onClick(handler: (Player, ClickType) -> Unit) {
        this.clickHandler = handler
    }

    /**
     * Registers a simplified click handler that ignores the click type.
     *
     * @param handler the callback invoked on click
     */
    fun onClick(handler: (Player) -> Unit) {
        this.clickHandler = { player, _ -> handler(player) }
    }

    /**
     * Sets a feedback sound to play when this slot is clicked.
     *
     * @param sound the Bukkit sound to play
     */
    fun sound(sound: Sound) {
        this.sound = sound
    }

    /**
     * Overrides the GUI-level [interactable][club.skidware.kgui.core.Gui.interactable]
     * setting for this slot, controlling whether the player can take the item.
     *
     * @param value true to allow item extraction, false to deny it
     */
    fun takeable(value: Boolean = true) {
        this.takeable = value
    }

    /**
     * Configures a frame-based animation for this slot.
     *
     * @param intervalTicks ticks between frame changes (20 = 1 second)
     * @param loop whether the animation restarts after the last frame
     * @param block builder lambda to add [AnimationFrame]s
     * @see SlotAnimation
     */
    fun animate(intervalTicks: Long = 20L, loop: Boolean = true, block: AnimationBuilder.() -> Unit) {
        this.animation = SlotAnimation(AnimationBuilder().apply(block).frames, intervalTicks, loop)
    }

    /**
     * Builds the final immutable [GuiSlot] from the current configuration.
     *
     * @return the constructed [GuiSlot]
     */
    internal fun build(): GuiSlot = GuiSlot(
        index = index,
        item = item,
        dynamicItem = dynamicItem,
        onClick = clickHandler,
        sound = sound,
        animation = animation,
        takeable = takeable
    )
}

/**
 * DSL builder for defining the frames of a [SlotAnimation].
 *
 * Used inside [SlotBuilder.animate] to add frames that the animation will cycle through.
 *
 * @since 1.0.0
 * @see SlotAnimation
 */
@GuiDslMarker
class AnimationBuilder {
    internal val frames: MutableList<AnimationFrame> = mutableListOf()

    /**
     * Adds an animation frame built from a material and optional item configuration.
     *
     * @param material the base material for the frame's item
     * @param block optional builder lambda for name, lore, etc.
     */
    fun frame(material: Material, block: ItemBuilder.() -> Unit = {}) {
        frames.add(AnimationFrame(ItemBuilder(material).apply(block).build()))
    }

    /**
     * Adds an animation frame from a pre-built [ItemStack].
     *
     * @param itemStack the item to display during this frame
     */
    fun frame(itemStack: ItemStack) {
        frames.add(AnimationFrame(itemStack))
    }
}
