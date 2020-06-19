package com.gmail.mattdiamond98.coronacraft.util;

import com.tommytony.war.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerInteraction {

    private static final Map<UUID, Map<UUID, Long>> HARM_MAP = new HashMap<>();
    private static final Map<UUID, Map<UUID, Long>> HELP_MAP = new HashMap<>();

    public static void playerHarm(UUID player, UUID harmer) {
        if (!HARM_MAP.containsKey(player)) HARM_MAP.put(player, new HashMap<>());
        HARM_MAP.get(player).put(harmer, System.currentTimeMillis());
    }

    public static void playerHarm(Player player, Player harmer) {
        playerHarm(player.getUniqueId(), harmer.getUniqueId());
    }

    public static void playerHelp(UUID player, UUID helper) {
        if (!HELP_MAP.containsKey(player)) HELP_MAP.put(player, new HashMap<>());
        HELP_MAP.get(player).put(helper, System.currentTimeMillis());
    }

    public static void playerHelp(Player player, Player harmer) {
        playerHelp(player.getUniqueId(), harmer.getUniqueId());
    }

    public static void clearHarm(UUID player) {
        HARM_MAP.remove(player);
    }

    public static void clearHarm(Player player) {
        clearHarm(player.getUniqueId());
    }

    public static void clearHelp(UUID player) {
        HELP_MAP.remove(player);
    }

    public static void clearHelp(Player player) {
        clearHelp(player.getUniqueId());
    }

    public static Set<Player> getRecentHarm(Player player, long after) {
        if (!HARM_MAP.containsKey(player.getUniqueId())) return new HashSet<>();
        return HARM_MAP.get(player.getUniqueId())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > after)
                .map(Map.Entry::getKey)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toSet());
    }

    public static Player getMostRecentHarm(Player player) {
        if (player == null) return null;
        if (!HARM_MAP.containsKey(player.getUniqueId()) || HARM_MAP.get(player.getUniqueId()).isEmpty()) return null;
        Optional<Map.Entry<UUID, Long>> entry = HARM_MAP.get(player.getUniqueId())
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));
        if (entry.isPresent()) {
            UUID id = entry.get().getKey();
            Player recent = Bukkit.getPlayer(id);
            if (recent == null || !recent.isOnline()
                    || Team.getTeamByPlayerName(recent.getName()) == null
                    || Team.getTeamByPlayerName(player.getName()).equals(Team.getTeamByPlayerName(recent.getName()))) {
                HARM_MAP.remove(id);
                return getMostRecentHarm(player);
            }
            return recent;
        }
        else return null;
    }

    public static Set<Player> getRecentHelp(Player player, long after) {
        return HELP_MAP.get(player.getUniqueId())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= after)
                .map(Map.Entry::getKey)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toSet());
    }

}
