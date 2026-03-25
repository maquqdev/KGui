package club.skidware.kgui.example

import club.skidware.kgui.KGui
import club.skidware.kgui.example.gui.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {
        KGui.setup(this)
        logger.info("KGui Example Plugin enabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command!")
            return true
        }

        when (label.lowercase()) {
            "kgui" -> {
                when (args.getOrNull(0)?.lowercase()) {
                    "chest" -> ChestExample.open(this, sender)
                    "paginated" -> PaginatedExample.open(this, sender)
                    "scrollable" -> ScrollableExample.open(this, sender)
                    "stateful" -> StatefulExample.open(this, sender)
                    "anvil" -> AnvilExample.open(this, sender)
                    "hopper" -> HopperExample.open(this, sender)
                    "merchant" -> MerchantExample.open(this, sender)
                    "pattern" -> PatternExample.open(this, sender)
                    "template" -> TemplateExample.open(this, sender)
                    else -> {
                        sender.sendMessage("Usage: /kgui <chest|paginated|scrollable|stateful|anvil|hopper|merchant|pattern|template>")
                    }
                }
            }
        }
        return true
    }
}
