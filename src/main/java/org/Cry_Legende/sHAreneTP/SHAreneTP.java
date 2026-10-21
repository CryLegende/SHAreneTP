package org.Cry_Legende.sHAreneTP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SHAreneTP extends JavaPlugin {
    private final List<Location> arenaLocations = new ArrayList<>();
    private FileConfiguration config;

    private void loadLocations() {
        this.config = this.getConfig();
        arenaLocations.clear();
        if (config.isConfigurationSection("locations")) {
            for (String key : config.getConfigurationSection("locations").getKeys(false)) {
                Location loc = (Location) config.get("locations." + key);
                arenaLocations.add(loc);
            }
        }
    }


    private void saveLocations() {
        int index = 0;
        for (Location loc : arenaLocations) {
            config.set("locations." + index, loc);
            index++;
        }
        this.saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shatpadd") && sender instanceof Player) {
            Player player = (Player) sender;
            addArenaLocation(player.getLocation());
            player.sendMessage("Location added!");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("shatp") && args.length > 0) {
            if (arenaLocations.isEmpty()) {
                loadLocations();
            }
            if (!arenaLocations.isEmpty()) {
                Player target = Bukkit.getServer().getPlayer(args[0]);
                if (target != null) {
                    Location tpLocation = getRandomLocation();
                    target.teleport(tpLocation);
                    target.playSound(tpLocation, Sound.valueOf(config.getString("tpSound")), 1.0F, 1.0F);
                    return true;
                }
            }
        }
        return false;
    }

    private void addArenaLocation(Location loc) {
        arenaLocations.add(loc);
        saveLocations();
    }

    private Location getRandomLocation() {
        return arenaLocations.get(new Random().nextInt(arenaLocations.size()));
    }
}

