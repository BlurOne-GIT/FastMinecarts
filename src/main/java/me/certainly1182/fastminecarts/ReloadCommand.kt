package me.certainly1182.fastminecarts

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class ReloadCommand(private val plugin: FastMinecarts) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty() || !args[0].equals("reload", ignoreCase = true))
            return false

        plugin.reloadConfig()
        plugin.loadConfig()
        sender.sendMessage("§aFastMinecarts configuration reloaded.")
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        return if (args.size == 1)
            listOf("reload").filter { it.startsWith(args[0], ignoreCase = true) }
        else
            listOf()
    }
}