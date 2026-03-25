package club.skidware.kgui.merchant

import club.skidware.kgui.dsl.GuiDslMarker
import club.skidware.kgui.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * DSL builder for defining a single merchant trade recipe.
 *
 * Call [result] once and [ingredient] once or twice (first call sets the
 * primary ingredient, second sets the optional secondary ingredient).
 *
 * ```kotlin
 * trade {
 *     result(Material.DIAMOND) { amount(3) }
 *     ingredient(Material.EMERALD) { amount(10) }
 *     maxUses = 5
 *     experienceReward = true
 * }
 * ```
 *
 * @since 1.0.0
 * @see MerchantGuiBuilder
 */
@GuiDslMarker
class TradeBuilder {
    /** The item the player receives from the trade. */
    var result: ItemStack? = null

    /** The required first ingredient. */
    var ingredient1: ItemStack? = null

    /** The optional second ingredient. */
    var ingredient2: ItemStack? = null

    /** Maximum number of times this trade can be used before locking. */
    var maxUses: Int = Int.MAX_VALUE

    /** Whether completing this trade grants experience orbs. */
    var experienceReward: Boolean = false

    /**
     * Sets the trade result item.
     *
     * @param material result item material
     * @param block optional item customization
     */
    fun result(material: Material, block: ItemBuilder.() -> Unit = {}) { result = ItemBuilder(material).apply(block).build() }

    /**
     * Adds an ingredient to the trade. First call sets [ingredient1], second sets [ingredient2].
     *
     * @param material ingredient item material
     * @param block optional item customization
     */
    fun ingredient(material: Material, block: ItemBuilder.() -> Unit = {}) {
        if (ingredient1 == null) ingredient1 = ItemBuilder(material).apply(block).build()
        else ingredient2 = ItemBuilder(material).apply(block).build()
    }
}
