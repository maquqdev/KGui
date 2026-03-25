package club.skidware.kgui

import club.skidware.kgui.chest.ChestGui
import club.skidware.kgui.chest.ChestGuiBuilder
import club.skidware.kgui.core.BaseGui
import club.skidware.kgui.input.AnvilGui
import club.skidware.kgui.input.AnvilGuiBuilder
import club.skidware.kgui.input.SignGui
import club.skidware.kgui.input.SignGuiBuilder
import club.skidware.kgui.listener.GuiListener
import club.skidware.kgui.merchant.MerchantGui
import club.skidware.kgui.merchant.MerchantGuiBuilder
import club.skidware.kgui.navigation.Navigator
import club.skidware.kgui.paginated.PaginatedGui
import club.skidware.kgui.paginated.PaginatedGuiBuilder
import club.skidware.kgui.scrollable.ScrollableGui
import club.skidware.kgui.scrollable.ScrollableGuiBuilder
import club.skidware.kgui.small.DispenserGui
import club.skidware.kgui.small.DispenserGuiBuilder
import club.skidware.kgui.small.HopperGui
import club.skidware.kgui.small.HopperGuiBuilder
import club.skidware.kgui.stateful.StatefulGui
import club.skidware.kgui.stateful.StatefulGuiBuilder
import club.skidware.kgui.template.GuiTemplate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main entry point and factory for every GUI type in the KGui library.
 *
 * Call [setup] once during plugin startup to register the internal event
 * listener. Afterwards, use the factory methods ([chest], [paginated],
 * [scrollable], etc.) to create GUI instances via the DSL.
 *
 * Also provides convenience wrappers for [Navigator]-based history traversal
 * and a global [GuiTemplate] registry.
 *
 * @since 1.0.0
 * @see BaseGui
 * @see Navigator
 * @see GuiTemplate
 */
object KGui {

    private var initialized = false
    private val templates: MutableMap<String, GuiTemplate> = mutableMapOf()

    /**
     * Registers the KGui event listener with the Bukkit plugin manager.
     *
     * Safe to call multiple times; only the first invocation has an effect.
     * All factory methods call this automatically if it hasn't been invoked yet.
     *
     * @param plugin the owning plugin used for event registration
     */
    fun setup(plugin: JavaPlugin) {
        if (initialized) return
        Bukkit.getPluginManager().registerEvents(GuiListener(), plugin)
        initialized = true
    }

    /**
     * Creates a standard chest GUI.
     *
     * @param plugin the owning plugin
     * @param title the inventory title (MiniMessage format)
     * @param rows the number of rows (1-6, default 3)
     * @param block DSL configuration lambda
     * @return the constructed [ChestGui]
     */
    fun chest(plugin: JavaPlugin, title: String, rows: Int = 3, block: ChestGuiBuilder.() -> Unit): ChestGui {
        ensureSetup(plugin); return ChestGuiBuilder(plugin, title, rows).apply(block).build()
    }

    /**
     * Creates a paginated GUI that splits content across multiple pages.
     *
     * @param plugin the owning plugin
     * @param title the inventory title (MiniMessage format)
     * @param rows the number of rows (default 6)
     * @param block DSL configuration lambda
     * @return the constructed [PaginatedGui]
     */
    fun paginated(plugin: JavaPlugin, title: String, rows: Int = 6, block: PaginatedGuiBuilder.() -> Unit): PaginatedGui {
        ensureSetup(plugin); return PaginatedGuiBuilder(plugin, title, rows).apply(block).build()
    }

    /**
     * Creates a scrollable GUI that supports vertical content scrolling.
     *
     * @param plugin the owning plugin
     * @param title the inventory title (MiniMessage format)
     * @param rows the number of rows (default 6)
     * @param block DSL configuration lambda
     * @return the constructed [ScrollableGui]
     */
    fun scrollable(plugin: JavaPlugin, title: String, rows: Int = 6, block: ScrollableGuiBuilder.() -> Unit): ScrollableGui {
        ensureSetup(plugin); return ScrollableGuiBuilder(plugin, title, rows).apply(block).build()
    }

    /**
     * Creates a stateful GUI whose content reacts to state changes.
     *
     * @param plugin the owning plugin
     * @param title the inventory title (MiniMessage format)
     * @param rows the number of rows (default 3)
     * @param block DSL configuration lambda
     * @return the constructed [StatefulGui]
     */
    fun stateful(plugin: JavaPlugin, title: String, rows: Int = 3, block: StatefulGuiBuilder.() -> Unit): StatefulGui {
        ensureSetup(plugin); return StatefulGuiBuilder(plugin, title, rows).apply(block).build()
    }

    /**
     * Creates an anvil-based input GUI for capturing text input from the player.
     *
     * @param plugin the owning plugin
     * @param title the anvil window title (default "Input")
     * @param block DSL configuration lambda
     * @return the constructed [AnvilGui]
     */
    fun anvil(plugin: JavaPlugin, title: String = "Input", block: AnvilGuiBuilder.() -> Unit): AnvilGui {
        ensureSetup(plugin); return AnvilGuiBuilder(plugin, title).apply(block).build()
    }

    /**
     * Creates a sign-based input GUI for capturing text input via a sign editor.
     *
     * @param plugin the owning plugin
     * @param block DSL configuration lambda
     * @return the constructed [SignGui]
     */
    fun sign(plugin: JavaPlugin, block: SignGuiBuilder.() -> Unit): SignGui {
        ensureSetup(plugin); return SignGuiBuilder(plugin).apply(block).build()
    }

    /**
     * Creates a villager-style merchant trading GUI.
     *
     * @param plugin the owning plugin
     * @param title the merchant window title
     * @param block DSL configuration lambda
     * @return the constructed [MerchantGui]
     */
    fun merchant(plugin: JavaPlugin, title: String, block: MerchantGuiBuilder.() -> Unit): MerchantGui {
        ensureSetup(plugin); return MerchantGuiBuilder(plugin, title).apply(block).build()
    }

    /**
     * Creates a 5-slot hopper GUI.
     *
     * @param plugin the owning plugin
     * @param title the hopper window title
     * @param block DSL configuration lambda
     * @return the constructed [HopperGui]
     */
    fun hopper(plugin: JavaPlugin, title: String, block: HopperGuiBuilder.() -> Unit): HopperGui {
        ensureSetup(plugin); return HopperGuiBuilder(plugin, title).apply(block).build()
    }

    /**
     * Creates a 9-slot dispenser/dropper GUI.
     *
     * @param plugin the owning plugin
     * @param title the dispenser window title
     * @param block DSL configuration lambda
     * @return the constructed [DispenserGui]
     */
    fun dispenser(plugin: JavaPlugin, title: String, block: DispenserGuiBuilder.() -> Unit): DispenserGui {
        ensureSetup(plugin); return DispenserGuiBuilder(plugin, title).apply(block).build()
    }

    /**
     * Registers a reusable [GuiTemplate] in the global template registry.
     *
     * Templates define border/fill materials, navigation buttons, and fixed
     * slots that can be applied to multiple GUIs for consistent styling.
     *
     * @param name unique identifier for the template
     * @param block DSL configuration lambda
     * @return the constructed and registered template
     * @see GuiTemplate
     */
    fun template(name: String, block: GuiTemplate.() -> Unit): GuiTemplate {
        val template = GuiTemplate(name).apply(block); templates[name] = template; return template
    }

    /**
     * Retrieves a previously registered template by name.
     *
     * @param name the template identifier
     * @return the template, or null if no template with that name exists
     */
    fun getTemplate(name: String): GuiTemplate? = templates[name]

    /**
     * Opens a GUI for the player and pushes the current GUI onto the navigation stack.
     *
     * @param player the player to navigate
     * @param gui the destination GUI
     * @see Navigator.navigate
     */
    fun navigate(player: Player, gui: BaseGui) = Navigator.navigate(player, gui)

    /**
     * Returns the player to the previous GUI in their navigation history.
     *
     * @param player the player to navigate back
     * @return true if a previous GUI existed and was opened
     * @see Navigator.back
     */
    fun back(player: Player) = Navigator.back(player)

    /**
     * Clears the entire navigation history for a player.
     *
     * @param player the player whose history should be cleared
     * @see Navigator.clear
     */
    fun clearHistory(player: Player) = Navigator.clear(player)

    /** Syntactic sugar allowing `3.rows` to read naturally in DSL contexts. */
    val Int.rows: Int get() = this

    /** Lazily initialises KGui if [setup] hasn't been called yet. */
    private fun ensureSetup(plugin: JavaPlugin) { if (!initialized) setup(plugin) }
}
