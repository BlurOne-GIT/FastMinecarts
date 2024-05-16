package me.certainly1182.fastminecarts

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Minecart
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.plugin.java.JavaPlugin

class FastMinecarts : JavaPlugin(), Listener {
    private var defaultMaxSpeed = 0.4
    private var _blockMaxSpeeds = mutableMapOf<Material, Double>()

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
        _blockMaxSpeeds.clear()
        for (key in blockConfig.getKeys(false)) {
            val material = Material.getMaterial(key) ?: continue
            _blockMaxSpeeds[material] = blockConfig.getDouble(key)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onVehicleMove(event: VehicleMoveEvent) {
        if (event.vehicle !is Minecart) return

        val minecart = event.vehicle as Minecart
        if (minecart.isEmpty) return
        if (minecart.passengers.first() !is Player) return

        val railBlock = event.vehicle.location.block
        if (!Tag.RAILS.isTagged(railBlock.type)) return

        val blockBelow = railBlock.getRelative(0, -1, 0)
        minecart.maxSpeed = _blockMaxSpeeds[blockBelow.type] ?: defaultMaxSpeed
    }

    @EventHandler(ignoreCancelled = true)
    fun onVehicleExit(event: VehicleExitEvent) {
        if (event.vehicle !is Minecart) return
        if (event.exited !is Player) return

        val minecart = event.vehicle as Minecart
        if (minecart.maxSpeed > defaultMaxSpeed)
            minecart.maxSpeed = defaultMaxSpeed
    }
}