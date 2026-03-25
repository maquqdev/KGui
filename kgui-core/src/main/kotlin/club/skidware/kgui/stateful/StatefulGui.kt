package club.skidware.kgui.stateful

import club.skidware.kgui.core.BaseGui
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Reactive chest GUI that re-renders automatically when state values change.
 *
 * Maintains a key-value state map and notifies registered listeners on mutation.
 * State can be accessed via delegated properties or explicit get/set methods.
 * The [renderFunction] is invoked on every update, enabling declarative UI.
 *
 * Use [StatefulGuiBuilder] via [club.skidware.kgui.KGui.stateful] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @param rows number of rows (clamped to 1-6)
 * @since 1.0.0
 * @see StatefulGuiBuilder
 */
class StatefulGui(
    plugin: JavaPlugin,
    title: Component,
    rows: Int = 3
) : BaseGui(plugin, title, rows.coerceIn(1, 6)) {

    private val stateMap: MutableMap<String, Any?> = ConcurrentHashMap()
    private val stateListeners: MutableMap<String, MutableList<(Any?, Any?) -> Unit>> = mutableMapOf()

    /** Called during each render pass to rebuild the inventory contents from current state. */
    var renderFunction: (StatefulGui.(Player) -> Unit)? = null

    /**
     * Creates a delegated property backed by the state map.
     *
     * Changes via the delegate automatically trigger re-renders and listener notifications.
     *
     * @param T the state value type
     * @param key unique identifier for this state entry
     * @param initial default value stored immediately
     * @return a [ReadWriteProperty] delegate
     */
    fun <T> state(key: String, initial: T): ReadWriteProperty<Any?, T> {
        stateMap[key] = initial
        return StateDelegate(key, initial)
    }

    /**
     * Reads a state value by key.
     *
     * @param T expected type (cast is unchecked)
     * @param key state entry identifier
     * @return the current value, or `null` if absent or wrong type
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getState(key: String): T? = stateMap[key] as? T

    /**
     * Updates a state value, notifies listeners, and triggers a re-render.
     *
     * @param T the state value type
     * @param key state entry identifier
     * @param value new value to store
     */
    fun <T : Any> setState(key: String, value: T) {
        val old = stateMap[key]
        stateMap[key] = value
        stateListeners[key]?.forEach { it(old, value) }
        update()
    }

    /**
     * Registers a listener invoked whenever the state entry identified by [key] changes.
     *
     * @param key state entry identifier to observe
     * @param listener callback receiving old and new values
     */
    fun onStateChange(key: String, listener: (old: Any?, new: Any?) -> Unit) {
        stateListeners.getOrPut(key) { mutableListOf() }.add(listener)
    }

    override fun renderFor(player: Player, inventory: Inventory) {
        inventory.clear()
        renderFunction?.invoke(this, player)
        for ((index, slot) in slots) {
            inventory.setItem(index, slot.getItemFor(player))
        }
    }

    private inner class StateDelegate<T>(private val key: String, private val initial: T) : ReadWriteProperty<Any?, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = (stateMap[key] as? T) ?: initial
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val old = stateMap[key]; stateMap[key] = value
            stateListeners[key]?.forEach { it(old, value) }; update()
        }
    }
}
