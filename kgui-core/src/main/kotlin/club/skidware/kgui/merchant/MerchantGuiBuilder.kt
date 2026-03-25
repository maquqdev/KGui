package club.skidware.kgui.merchant

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

/**
 * DSL builder for constructing a [MerchantGui] with villager-style trades.
 *
 * Trades can be defined inline with [ItemStack] arguments or via the
 * [TradeBuilder] DSL for a more readable approach.
 *
 * ```kotlin
 * KGui.merchant(plugin, "<gold>Blacksmith") {
 *     trade {
 *         result(Material.DIAMOND_SWORD) { name("<red>Fire Sword") }
 *         ingredient(Material.IRON_INGOT) { amount(32) }
 *         ingredient(Material.BLAZE_POWDER) { amount(4) }
 *     }
 *     onTrade { player, index -> player.sendMessage("Trade #$index complete!") }
 * }
 * ```
 *
 * @param plugin owning plugin instance
 * @param title MiniMessage-formatted merchant title
 * @since 1.0.0
 * @see MerchantGui
 * @see TradeBuilder
 */
@GuiDslMarker
class MerchantGuiBuilder(private val plugin: JavaPlugin, private val title: String) {
    private val gui = MerchantGui(plugin, MiniMessage.miniMessage().deserialize(title))
    private var onTrade: ((Player, Int) -> Unit)? = null

    /**
     * Adds a trade using pre-built [ItemStack] arguments.
     *
     * @param result the item the player receives
     * @param ingredient1 required first ingredient
     * @param ingredient2 optional second ingredient
     * @param maxUses maximum number of uses
     * @param experienceReward whether the trade grants experience
     */
    fun trade(result: ItemStack, ingredient1: ItemStack, ingredient2: ItemStack? = null,
              maxUses: Int = Int.MAX_VALUE, experienceReward: Boolean = false) {
        gui.addTrade(result, ingredient1, ingredient2, maxUses, experienceReward)
    }

    /**
     * Adds a trade using the [TradeBuilder] DSL.
     *
     * @param block DSL configuration for the trade
     * @throws IllegalStateException if result or first ingredient is not set
     * @see TradeBuilder
     */
    fun trade(block: TradeBuilder.() -> Unit) {
        val b = TradeBuilder().apply(block)
        gui.addTrade(b.result ?: error("Trade must have result"), b.ingredient1 ?: error("Trade must have ingredient"), b.ingredient2, b.maxUses, b.experienceReward)
    }

    /**
     * Registers a callback invoked when a player completes any trade.
     *
     * @param handler receives the player and the zero-based trade index
     */
    fun onTrade(handler: (Player, Int) -> Unit) { this.onTrade = handler }

    internal fun build(): MerchantGui { gui.onTrade = onTrade; return gui }
}
