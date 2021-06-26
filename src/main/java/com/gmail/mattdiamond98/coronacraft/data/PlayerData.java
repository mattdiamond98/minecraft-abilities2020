package com.gmail.mattdiamond98.coronacraft.data;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import de.gesundkrank.jskills.RateablePlayer;
import de.gesundkrank.jskills.Rating;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    private static final Map<UUID, FileConfiguration> DATA_MAP = new ConcurrentHashMap<>();

    public static File getFile(UUID player) {
        return new File(CoronaCraft.instance.getPlayerDataFolder(), player.toString());
    }

    public static FileConfiguration getData(Player player) {
        return getData(player.getUniqueId());
    }

    public static FileConfiguration getData(UUID player) {
        if (DATA_MAP.containsKey(player)) {
            return DATA_MAP.get(player);
        }
        File file = getFile(player);
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            DATA_MAP.put(player, config);
            Bukkit.getLogger().info("Loaded existing configuration for " + player);
            return config;
        }
        else {
            Bukkit.getLogger().info("Created new configuration for " + player);
            FileConfiguration config = new YamlConfiguration();
            DATA_MAP.put(player, config);
            return config;
        }
    }

    public static void preLoadPlayer(UUID player) {
        Bukkit.getScheduler().runTaskAsynchronously(CoronaCraft.instance, () -> getData(player));
    }

    public static Rating getRating(Player player) {
        return getRating(player.getUniqueId());
    }

    public static Rating getRating(UUID player) {
        FileConfiguration data = getData(player);
        if (!data.contains(PlayerDataPath.RATING.getPath())) {
            return new Rating(25.0, 8.333333);
        }
        double mean = data.contains(PlayerDataPath.RATING_MEAN.getPath()) ? data.getDouble(PlayerDataPath.RATING_MEAN.getPath()) : 25.0;
        double std = data.contains(PlayerDataPath.RATING_STANDARD_DEVIATION.getPath())
                ? data.getDouble(PlayerDataPath.RATING_STANDARD_DEVIATION.getPath()) : 8.333333;
        double conservativeMultiplier = data.contains(PlayerDataPath.RATING_CONSERVATIVE_MULTIPLIER.getPath())
                ? data.getDouble(PlayerDataPath.RATING_CONSERVATIVE_MULTIPLIER.getPath()) : 3;
        return new Rating(mean, std, conservativeMultiplier);
    }

    public static Rating getRating(RateablePlayer<UUID> player) {
        return getRating(player.getId());
    }

    public static void updateRating(UUID player, Rating rating) {
        FileConfiguration config = getData(player);
        config.set(PlayerDataPath.RATING_MEAN.getPath(), rating.getMean());
        config.set(PlayerDataPath.RATING_STANDARD_DEVIATION.getPath(), rating.getStandardDeviation());
        config.set(PlayerDataPath.RATING_CONSERVATIVE_MULTIPLIER.getPath(), rating.getConservativeStandardDeviationMultiplier());
        Bukkit.getLogger().info("Updated " + player + " to " + rating);
        DATA_MAP.put(player, config);
    }

    public static void persist(final UUID player) {
        if (DATA_MAP.containsKey(player)) {
            FileConfiguration data = DATA_MAP.remove(player);
            try {
                data.save(getFile(player));
                Bukkit.getLogger().info("Persisted data file for " + player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().warning("Could not find data to persist for " + player);
        }
    }

    public static void persistAll() {
        for (UUID player : new HashSet<>(DATA_MAP.keySet())) {
            persist(player);
        }
    }

    public static void asyncPersist(final UUID player) {
        Bukkit.getScheduler().runTaskAsynchronously(CoronaCraft.instance, () -> {
            persist(player);
        });
    }

}
