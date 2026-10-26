package org.Cry_Legende.sHAreneTP;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SHAreneTP extends JavaPlugin {
    private final List<Location> arenaLocations = new ArrayList<>();
    private FileConfiguration config;

    private void loadLocations() {
        this.config = this.getConfig();
        arenaLocations.clear();
        if (config.isConfigurationSection("locations")) {
            for (String key : config.getConfigurationSection("locations").getKeys(false)) {
                if (config.get("locations." + key) instanceof Location loc) {
                    arenaLocations.add(loc);
                }
            }
        }
    }


    private void saveLocations() {
        // getConfig() et non le champ config : celui-ci est null tant que loadLocations() n'a pas tourné
        FileConfiguration cfg = this.getConfig();
        int index = 0;
        for (Location loc : arenaLocations) {
            cfg.set("locations." + index, loc);
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
                    Sound tpSound = sound(getConfig().getString("tpSound"));
                    if (tpSound != null) {
                        target.playSound(tpLocation, tpSound, 1.0F, 1.0F);
                    } else {
                        getLogger().warning("Son 'tpSound' invalide dans config.yml : aucun son joué.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void addArenaLocation(Location loc) {
        // Liste chargée à la demande : sans ça, un ajout juste après le démarrage écrasait le point n°0
        if (arenaLocations.isEmpty()) {
            loadLocations();
        }
        arenaLocations.add(loc);
        saveLocations();
    }

    private Location getRandomLocation() {
        return arenaLocations.get(new Random().nextInt(arenaLocations.size()));
    }

    /** Son lu en config : ancien nom (ENTITY_PLAYER_LEVELUP) ou clé (minecraft:entity.player.levelup). */
    static Sound sound(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim();
        if (s.indexOf(':') >= 0 || s.indexOf('.') >= 0) {
            NamespacedKey key = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return key == null ? null : Registry.SOUND_EVENT.get(key);
        }
        try {
            return (Sound) Sound.class.getField(s.toUpperCase(Locale.ROOT)).get(null);
        } catch (ReflectiveOperationException | ClassCastException e) {
            return null;
        }
    }
}

