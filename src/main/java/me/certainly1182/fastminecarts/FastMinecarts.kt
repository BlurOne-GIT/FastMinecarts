package me.certainly1182.fastminecarts

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Minecart
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.plugin.java.JavaPlugin

class FastMinecarts : JavaPlugin(), Listener {
    private var defaultMaxSpeed = 0.4
    private val blockMaxSpeeds = mutableMapOf<Material, Double>()

    override fun onEnable() {
        saveDefaultConfig()
        loadConfig()
        server.pluginManager.registerEvents(this, this)
        val reloadCommand = ReloadCommand(this)
        val command = getCommand("FastMinecarts") ?: return
        command.setExecutor(reloadCommand)
        command.tabCompleter = reloadCommand
    }

    internal fun loadConfig() {
        defaultMaxSpeed = config.getDouble("default-speed", 0.4)
        val blockConfig = config.getConfigurationSection("blocks") ?: return
        blockMaxSpeeds.clear()
        for (key in blockConfig.getKeys(false)) {
            val material = Material.getMaterial(key) ?: continue
            blockMaxSpeeds[material] = blockConfig.getDouble(key)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onVehicleMove(event: VehicleMoveEvent) {
        if (event.vehicle !is Minecart) return

        val minecart = event.vehicle as Minecart

        val railBlock = event.vehicle.location.block
        if (!Tag.RAILS.isTagged(railBlock.type))
        {
            if (minecart.isOnGround)
                minecart.maxSpeed = defaultMaxSpeed
            return
        }

        val blockBelow = railBlock.getRelative(0, -1, 0)
        minecart.maxSpeed = blockMaxSpeeds[blockBelow.type] ?: defaultMaxSpeed
    }
}