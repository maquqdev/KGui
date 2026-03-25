package club.skidware.kgui.merchant

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.plugin.java.JavaPlugin

/**
 * Villager-style trading GUI backed by Bukkit's [org.bukkit.inventory.Merchant] API.
 *
 * Supports up to two ingredients per trade and optional experience rewards.
 * Unlike chest-based GUIs, this uses the native merchant interface and does
 * not extend [club.skidware.kgui.core.BaseGui].
 *
 * Use [MerchantGuiBuilder] via [club.skidware.kgui.KGui.merchant] for DSL-based construction.
 *
 * @param plugin owning plugin instance
 * @param title merchant window title rendered as an Adventure [Component]
 * @since 1.0.0
 * @see MerchantGuiBuilder
 * @see TradeBuilder
 */
class MerchantGui(val plugin: JavaPlugin, val title: Component) {
    private val recipes: MutableList<MerchantRecipe> = mutableListOf()

    /** Called when a player completes a trade, receiving the zero-based trade index. */
    var onTrade: ((Player, Int) -> Unit)? = null

    /**
     * Adds a trade offer with the given result and ingredients.
     *
     * @param result the item the player receives
     * @param ingredient1 required first ingredient
     * @param ingredient2 optional second ingredient
     * @param maxUses maximum number of times this trade can be used
     * @param experienceReward whether completing the trade grants experience
     * @param villagerExperience experience granted to the "villager"
     * @param priceMultiplier demand-based price scaling factor
     * @return this instance for chaining
     */
    fun addTrade(result: ItemStack, ingredient1: ItemStack, ingredient2: ItemStack? = null,
                 maxUses: Int = Int.MAX_VALUE, experienceReward: Boolean = false,
                 villagerExperience: Int = 0, priceMultiplier: Float = 0f) = apply {
        val recipe = MerchantRecipe(result, 0, maxUses, experienceReward, villagerExperience, priceMultiplier)
        recipe.addIngredient(ingredient1)
        ingredient2?.let { recipe.addIngredient(it) }
        recipes.add(recipe)
    }

    /**
     * Adds a pre-built [MerchantRecipe] directly.
     *
     * @param recipe the recipe to add
     * @return this instance for chaining
     */
    fun addRecipe(recipe: MerchantRecipe) = apply { recipes.add(recipe) }

    /**
     * Removes all trade offers.
     *
     * @return this instance for chaining
     */
    fun clearTrades() = apply { recipes.clear() }

    /** Opens the merchant trading interface for the given [player]. */
    fun open(player: Player) {
        val merchant = Bukkit.createMerchant(title)
        merchant.recipes = recipes
        player.openMerchant(merchant, true)
    }
}
