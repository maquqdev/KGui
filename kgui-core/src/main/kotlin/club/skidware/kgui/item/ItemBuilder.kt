package club.skidware.kgui.item

import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

/**
 * Fluent builder for constructing Bukkit [ItemStack] instances.
 *
 * Supports MiniMessage-formatted text, enchantments, skull textures, glow
 * effects, and all standard item metadata. All setter methods return `this`
 * for chaining. Call [build] to produce the final [ItemStack].
 *
 * ```kotlin
 * val item = ItemBuilder(Material.DIAMOND_SWORD)
 *     .name("<gold>Excalibur")
 *     .lore("<gray>A legendary blade")
 *     .glow()
 *     .build()
 * ```
 *
 * @param material the initial material type
 * @since 1.0.0
 * @see item
 */
class ItemBuilder(private var material: Material) {

    private var amount: Int = 1
    private var displayName: Component? = null
    private var loreLines: MutableList<Component> = mutableListOf()
    private var enchantments: MutableMap<Enchantment, Int> = mutableMapOf()
    private var itemFlags: MutableSet<ItemFlag> = mutableSetOf()
    private var customModelData: Int? = null
    private var unbreakable: Boolean = false
    private var glowing: Boolean = false
    private var damage: Int = 0
    private var skullOwner: OfflinePlayer? = null
    private var skullTexture: String? = null

    companion object {
        private val miniMessage = MiniMessage.miniMessage()

        /**
         * Creates a new [ItemBuilder] for the given material.
         *
         * @param material the material type
         * @return a fresh builder instance
         */
        fun of(material: Material): ItemBuilder = ItemBuilder(material)

        /**
         * Creates a player head builder with the skin of the given player.
         *
         * @param owner the player whose skin to use
         * @return a builder pre-configured as [Material.PLAYER_HEAD]
         */
        fun skull(owner: OfflinePlayer): ItemBuilder = ItemBuilder(Material.PLAYER_HEAD).skullOwner(owner)

        /**
         * Creates a player head builder with a custom base64 skin texture.
         *
         * @param texture the base64-encoded skin texture value
         * @return a builder pre-configured as [Material.PLAYER_HEAD]
         */
        fun skull(texture: String): ItemBuilder = ItemBuilder(Material.PLAYER_HEAD).skullTexture(texture)
    }

    /** Changes the material type. */
    fun material(material: Material) = apply { this.material = material }

    /**
     * Sets the stack size, clamped to 1..64.
     *
     * @param amount desired stack size
     */
    fun amount(amount: Int) = apply { this.amount = amount.coerceIn(1, 64) }

    /**
     * Sets the display name using a MiniMessage-formatted string.
     *
     * @param text the MiniMessage text (e.g. `"<gold>Sword"`)
     */
    fun name(text: String) = apply { this.displayName = miniMessage.deserialize(text) }

    /**
     * Sets the display name from a pre-built Adventure [Component].
     *
     * @param component the display name component
     */
    fun name(component: Component) = apply { this.displayName = component }

    /**
     * Replaces all lore lines with the given MiniMessage-formatted strings.
     *
     * @param lines the lore lines
     */
    fun lore(vararg lines: String) = apply {
        this.loreLines = lines.map { miniMessage.deserialize(it) }.toMutableList()
    }

    /**
     * Replaces all lore lines with the given MiniMessage-formatted strings.
     *
     * @param lines the lore lines as a list
     */
    fun lore(lines: List<String>) = apply {
        this.loreLines = lines.map { miniMessage.deserialize(it) }.toMutableList()
    }

    /**
     * Replaces all lore lines with pre-built Adventure [Component]s.
     *
     * @param lines the lore components
     */
    fun loreComponents(lines: List<Component>) = apply {
        this.loreLines = lines.toMutableList()
    }

    /**
     * Appends a single MiniMessage-formatted lore line.
     *
     * @param line the lore line to add
     */
    fun addLore(line: String) = apply { this.loreLines.add(miniMessage.deserialize(line)) }

    /**
     * Appends a single Adventure [Component] lore line.
     *
     * @param component the lore component to add
     */
    fun addLore(component: Component) = apply { this.loreLines.add(component) }

    /**
     * Adds an enchantment with the specified level.
     *
     * @param enchantment the enchantment to apply
     * @param level the enchantment level (defaults to 1)
     */
    fun enchant(enchantment: Enchantment, level: Int = 1) = apply {
        this.enchantments[enchantment] = level
    }

    /** Removes all previously added enchantments. */
    fun clearEnchantments() = apply { this.enchantments.clear() }

    /**
     * Adds item flags to hide specific tooltip sections.
     *
     * @param flags the flags to add
     */
    fun flag(vararg flags: ItemFlag) = apply { this.itemFlags.addAll(flags) }

    /** Adds all available [ItemFlag]s, hiding every tooltip section. */
    fun hideAllFlags() = apply { this.itemFlags.addAll(ItemFlag.entries) }

    /**
     * Sets the custom model data for resource pack models.
     *
     * @param data the custom model data integer
     */
    fun customModelData(data: Int) = apply { this.customModelData = data }

    /**
     * Marks the item as unbreakable.
     *
     * @param value true to make unbreakable (default), false to allow breaking
     */
    fun unbreakable(value: Boolean = true) = apply { this.unbreakable = value }

    /**
     * Adds a visual enchantment glow without a real enchantment effect.
     *
     * Internally applies [Enchantment.UNBREAKING] with [ItemFlag.HIDE_ENCHANTS]
     * when no other enchantments are present.
     *
     * @param value true to enable glow (default), false to disable
     */
    fun glow(value: Boolean = true) = apply { this.glowing = value }

    /**
     * Sets the item's durability damage.
     *
     * @param damage the damage value (0 = undamaged)
     */
    fun damage(damage: Int) = apply { this.damage = damage }

    /**
     * Sets the skull owner for a player head, automatically switching the material.
     *
     * @param player the player whose skin to display
     */
    fun skullOwner(player: OfflinePlayer) = apply {
        this.material = Material.PLAYER_HEAD
        this.skullOwner = player
    }

    /**
     * Sets a custom base64 skin texture for a player head.
     *
     * @param base64 the base64-encoded texture value
     */
    fun skullTexture(base64: String) = apply {
        this.material = Material.PLAYER_HEAD
        this.skullTexture = base64
    }

    /**
     * Constructs the final [ItemStack] with all configured properties applied.
     *
     * @return the fully configured item stack
     */
    fun build(): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta ?: return item

        displayName?.let { meta.displayName(it) }

        if (loreLines.isNotEmpty()) {
            meta.lore(loreLines)
        }

        enchantments.forEach { (ench, level) ->
            meta.addEnchant(ench, level, true)
        }

        if (glowing && enchantments.isEmpty()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        itemFlags.forEach { meta.addItemFlags(it) }

        customModelData?.let { meta.setCustomModelData(it) }

        meta.isUnbreakable = unbreakable

        if (meta is Damageable && damage > 0) {
            meta.damage = damage
        }

        if (meta is SkullMeta) {
            skullOwner?.let { meta.owningPlayer = it }
            skullTexture?.let { texture ->
                val profile = Bukkit.createProfile(UUID.randomUUID())
                profile.setProperty(ProfileProperty("textures", texture))
                meta.playerProfile = profile
            }
        }

        item.itemMeta = meta
        return item
    }

    /**
     * Creates a deep copy of this builder, allowing independent modification
     * without affecting the original.
     *
     * @return a new [ItemBuilder] with the same configuration
     */
    fun clone(): ItemBuilder {
        val copy = ItemBuilder(material)
        copy.amount = amount
        copy.displayName = displayName
        copy.loreLines = loreLines.toMutableList()
        copy.enchantments = enchantments.toMutableMap()
        copy.itemFlags = itemFlags.toMutableSet()
        copy.customModelData = customModelData
        copy.unbreakable = unbreakable
        copy.glowing = glowing
        copy.damage = damage
        copy.skullOwner = skullOwner
        copy.skullTexture = skullTexture
        return copy
    }
}

/**
 * Top-level DSL function for creating an [ItemStack] with a concise builder syntax.
 *
 * ```kotlin
 * val sword = item(Material.DIAMOND_SWORD) {
 *     name("<gold>Excalibur")
 *     lore("<gray>Legendary weapon")
 * }
 * ```
 *
 * @param material the base material type
 * @param block optional configuration lambda
 * @return the constructed [ItemStack]
 * @since 1.0.0
 * @see ItemBuilder
 */
fun item(material: Material, block: ItemBuilder.() -> Unit = {}): ItemStack {
    return ItemBuilder(material).apply(block).build()
}
