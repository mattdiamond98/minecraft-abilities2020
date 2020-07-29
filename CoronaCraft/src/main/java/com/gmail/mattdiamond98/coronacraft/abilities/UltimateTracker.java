package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class UltimateTracker {

    public static final double TIME_THRESHOLD = 30.0;
    public static final double THRESHOLD_EXTENSION = 10.0; // How much we extend the threshold per point behind
    public static final double TIME_INCREMENT = (5.0 / 60.0) * CoronaCraft.ABILITY_TICK_PER_SECOND; // How much we increment each tick
    public static final double PLAYER_KILL_REWARD = 10.0;
    public static final double PLAYER_ASSIST_REWARD = 5.0;
    public static final double PLAYER_SCORE_REWARD = 20.0;

    private static final Map<UUID, Loadout> currentLoadouts = new HashMap<>();
    private static final Map<UUID, Double> ultimateProgress = new HashMap<>();
    private static final Map<UUID, Double> gameTimeProgress = new HashMap<>(); // for tracking how much passive ultimate progress

    public static double getProgress(UUID player) {
        if (ultimateProgress.containsKey(player)) return ultimateProgress.get(player);
        return 0;
    }

    public static double getProgress(Player player) { return getProgress(player.getUniqueId()); }

    public static void setProgress(UUID player, double progress) {
        ultimateProgress.put(player, progress);
    }

    public static void setProgress(Player player, double progress) { setProgress(player.getUniqueId(), progress); }

    public static void incrementProgress(UUID player, double amount) { setProgress(player, Math.min(getProgress(player) + amount, 100.0)); }

    public static void incrementProgress(Player player, double amount) { incrementProgress(player.getUniqueId(), amount); }

    public static void removeProgress(UUID player) {
        ultimateProgress.remove(player);
        gameTimeProgress.remove(player);
        if (isUltimateActive(player)) {
            CoronaCraft.setCooldown(Bukkit.getPlayer(player), Material.NETHER_STAR, 0);
        }
    }


    public static void removeProgress(Player player) { removeProgress(player.getUniqueId()); }

    public static void setLoadout(UUID player, Loadout loadout) {
        if (loadout != null) {
            currentLoadouts.put(player, loadout);
        }
    }

    public static void setLoadout(Player player, Loadout loadout) { setLoadout(player.getUniqueId(), loadout); }

    public static Loadout getLoadout(UUID player) {
        return currentLoadouts.get(player);
    }

    public static Loadout getLoadout(Player player) { return getLoadout(player.getUniqueId()); }

    public static double getGameTimeProgress(UUID player) {
        if (gameTimeProgress.containsKey(player)) return gameTimeProgress.get(player);
        return 0;
    }

    public static double getGameTimeProgress(Player player) { return getGameTimeProgress(player.getUniqueId()); }

    public static void setGameTimeProgress(UUID player, double progress) { gameTimeProgress.put(player, progress); }

    public static void setGameTimeProgress(Player player, double progress) { setGameTimeProgress(player.getUniqueId(), progress); }

    public static double getGameTimeThreshold(Player player) {
        Team team = Team.getTeamByPlayerName(player.getName());
        if (team == null) return 0.0;
        Warzone zone = team.getZone();
        if (zone.getName().equalsIgnoreCase("CircusUltimatus")) return Integer.MAX_VALUE;
        List<Team> enemies = new ArrayList<>(zone.getTeams());
        enemies.remove(team);
        int scoreDifference = enemies.stream().map(Team::getPoints).max(Integer::compareTo).orElse(0) - team.getPoints();
        if (scoreDifference <= 0) return TIME_THRESHOLD;
        return TIME_THRESHOLD + (scoreDifference * THRESHOLD_EXTENSION);
    }

    /***
     * This is for ticking ultimate progress via time, limited to a certain threshold each game
     * @param player the player being incremented
     * @param amount the amount to increment
     */
    public static void incrementGameTimeProgress(Player player, double amount) {
        double newProgress = getProgress(player) + amount;
        if (Warzone.getZoneByPlayerName(player.getName()).getName().equals("CircusUltimatus")) {
            amount *= 10;
        }
        if (getGameTimeProgress(player) >= getGameTimeThreshold(player)) return;
        if (getGameTimeProgress(player) + amount >= getGameTimeThreshold(player)) {
            double difference = getGameTimeProgress(player) + amount - getGameTimeThreshold(player);
            setGameTimeProgress(player, getGameTimeProgress(player) + difference);
            incrementProgress(player, difference);
        } else {
            setGameTimeProgress(player, getGameTimeProgress(player) + amount);
            incrementProgress(player, amount);
        }
    }

    public static void incrementGameTimeProgress(Player player) {
        incrementGameTimeProgress(player, TIME_INCREMENT);
    }

    public static boolean isUltimateActive(UUID player) {
        return isUltimateActive(Bukkit.getPlayer(player));
    }

    public static boolean isUltimateActive(Player player) {
        return CoronaCraft.isOnCooldown(player, Material.NETHER_STAR);
    }

}
