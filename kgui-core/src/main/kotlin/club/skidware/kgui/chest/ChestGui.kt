package club.skidware.kgui.chest

import club.skidware.kgui.core.BaseGui
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

/**
 * Standard chest-based inventory GUI supporting 1-6 rows.
 *
 * The simplest GUI type, suitable for static menus and interactive panels.
 * Use [ChestGuiBuilder] via [club.skidware.kgui.KGui.chest] for DSL-based construction.
 *
 * @param plugin owning plugin instance for event registration
 * @param title inventory title rendered as an Adventure [Component]
 * @param rows number of rows (clamped to 1-6)
 * @since 1.0.0
 * @see ChestGuiBuilder
 */
class ChestGui(
    plugin: JavaPlugin,
    title: Component,
    rows: Int = 3
) : BaseGui(plugin, title, rows.coerceIn(1, 6))
