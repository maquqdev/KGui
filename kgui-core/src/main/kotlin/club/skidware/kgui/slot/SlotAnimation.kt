package club.skidware.kgui.slot

import org.bukkit.inventory.ItemStack

/**
 * A frame-based animation that cycles through a sequence of items in a GUI slot.
 *
 * The animation advances one frame each time [nextFrame] is called (driven by
 * the scheduler in [BaseGui][club.skidware.kgui.core.BaseGui]). When [loop] is
 * true, the sequence restarts after the last frame; otherwise it holds on the
 * final frame indefinitely.
 *
 * @param frames the ordered list of animation frames to display
 * @param intervalTicks server ticks between frame advances (20 ticks = 1 second)
 * @param loop whether the animation restarts after the last frame
 * @since 1.0.0
 * @see AnimationFrame
 * @see GuiSlot
 */
data class SlotAnimation(
    val frames: List<AnimationFrame>,
    val intervalTicks: Long = 20L,
    val loop: Boolean = true
) {
    private var currentFrame: Int = 0

    /**
     * Advances the animation by one frame and returns it.
     *
     * @return the next frame, or null if [frames] is empty
     */
    fun nextFrame(): AnimationFrame? {
        if (frames.isEmpty()) return null
        val frame = frames[currentFrame]
        currentFrame++
        if (currentFrame >= frames.size) {
            currentFrame = if (loop) 0 else frames.size - 1
        }
        return frame
    }

    /** Resets the animation back to the first frame. */
    fun reset() {
        currentFrame = 0
    }
}

/**
 * A single frame in a [SlotAnimation], holding the item to display.
 *
 * @param item the item stack shown during this frame
 * @since 1.0.0
 */
data class AnimationFrame(
    val item: ItemStack
)
