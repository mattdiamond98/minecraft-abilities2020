package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Achievements implements Listener {
    private static transient final long serialVersionUID = 4966211451999538092L;
    private static String folderPath;
    private static String fileType;
    private static int autosaveInterval = 20 * 60;
    private static HashMap<UUID, HashMap<String, Boolean>> data = new HashMap<UUID, HashMap<String,
            Boolean>>();
    private static String[] achievementNames = new String[]{
            "First Kill", "Ten Kills", "One Hundred Kills", "Two Hundred Kills",
            "First Assist", "Ten Assists", "One Hundred Assists", "Two Hundred Assists",
            "First Death", "Ten Deaths", "One Hundred Deaths", "Two Hundred Deaths",
            "First Capture", "Ten Captures", "One Hundred Captures", "Two Hundred Captures",
            "First Win", "Ten Wins", "One Hundred Wins", "Two Hundred Wins",
            "First Game", "Ten Games", "One Hundred Games", "Two Hundred Games",
            "Guaranteed Win"};
    private static HashMap<UUID, HashMap<String, String>> bankedAchievements = new HashMap<UUID, HashMap<
            String, String>>();

    public Achievements() {}

    public static void initialize() {
        loadData();

        // Create runnable to save regularly
        BukkitRunnable run = new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        };
        run.runTaskTimer(CoronaCraft.instance, 20 * autosaveInterval, 20 * autosaveInterval);
    }

    public static boolean saveData() {
        System.out.println("Saving achievements data...");
        try {
            // Save achievements data
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new
                    FileOutputStream(folderPath + "data" + fileType)));
            out.writeObject(data);
            out.close();

            System.out.println("Achievements data saved successfully");
            return true;
        }
        catch (IOException e) {
            System.out.println(e.getStackTrace());
            Bukkit.broadcastMessage(ChatColor.AQUA + "ERROR: Failed to save achievements data - report this" +
                    " to @Developer on discord immediately");
            return false;
        }
    }

    private static boolean loadData() {
        System.out.println("Loading achievements data...");
        try {
            // Get file path and names
            folderPath = CoronaCraft.instance.getDataFolder().getAbsolutePath() + "/achievements/";
            fileType = ".txt";

            // Load data
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                    folderPath + "data" + fileType)));
            data = (HashMap<UUID, HashMap<String, Boolean>>) in.readObject();
            in.close();

            System.out.println("Achievements data loaded successfully");
            return true;
        }
        catch (ClassNotFoundException | IOException e) {
            System.out.println("data.txt not found");
            return false;
        }
    }

    private static void broadcastAchievement(Player player, String achievementName, String
            achievementDescription) {
        TextComponent message = new TextComponent(player.getName() + " has completed the achievement ");
        TextComponent interactiveBit = new TextComponent("[" + achievementName + "]");
        interactiveBit.setColor(ChatColor.GREEN.asBungee());
        interactiveBit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                achievementDescription).create()));
        message.addExtra(interactiveBit);
        Bukkit.getServer().spigot().broadcast(message);
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        // Add new player to data
        UUID uuid = event.getPlayer().getUniqueId();
        if (!data.containsKey(uuid)) {
            data.put(uuid, new HashMap<String, Boolean>());
        }

        // Check that the player's hashmap contains all achievement trackers
        HashMap<String, Boolean> playerData = data.get(uuid);
        for (String name : achievementNames) {
            if (!playerData.containsKey(name)) {
                playerData.put(name, false);
            }
        }
    }

    public static void checkAchievements() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkLeaderboardAchievements(player);
            checkBankedAchievements(player);
        }
    }

    private static void checkBankedAchievements(Player player) {
        // Check whether player has banked special achievements
        if (bankedAchievements.containsKey(player.getUniqueId())) {
            HashMap<String, String> achievementInfo = bankedAchievements.get(player.getUniqueId());
            if (achievementInfo == null || achievementInfo.size() == 0) { return; }

            // Award all banked achievements
            for (Map.Entry pair : achievementInfo.entrySet()) {
                broadcastAchievement(player, (String) pair.getKey(), (String) pair.getValue());
            }

            // Remove the player from the banked achievements list
            bankedAchievements.remove(player.getUniqueId());
        }
    }

    private static void checkLeaderboardAchievements(Player player) {
        HashMap<String, Boolean> playerData = data.get(player.getUniqueId());

        // Check kill achievements
        int count = Leaderboard.getKills(player);
        String name = "First Kill";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Kill a player");
        }
        name = "Ten Kills";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Kill ten players");
        }
        name = "One Hundred Kills";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Kill one hundred players");
        }
        name = "Two Hundred Kills";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Kill two hundred players");
        }

        // Check assist achievements
        count = Leaderboard.getAssists(player);
        name = "First Assist";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Assist with a kill");
        }
        name = "Ten Assists";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Assist with ten kills");
        }
        name = "One Hundred Assists";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Assist with one hundred kills");
        }
        name = "Two Hundred Assists";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Assist with two hundred kills");
        }

        // Check death achievements
        count = Leaderboard.getDeaths(player);
        name = "First Death";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Die");
        }
        name = "Ten Deaths";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Die ten times");
        }
        name = "One Hundred Deaths";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Die one hundred times");
        }
        name = "Two Hundred Deaths";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Die two hundred times");
        }

        // Check capture achievements
        count = Leaderboard.getCaptures(player);
        name = "First Capture";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Capture a flag");
        }
        name = "Ten Captures";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Capture ten flags");
        }
        name = "One Hundred Captures";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Capture one hundred flags");
        }
        name = "Two Hundred Captures";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Capture two hundred flags");
        }

        // Check win achievements
        count = Leaderboard.getGamesWon(player);
        name = "First Win";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Win a game");
        }
        name = "Ten Wins";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Win ten games");
        }
        name = "One Hundred Wins";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Win one hundred games");
        }
        name = "Two Hundred Wins";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Win two hundred games");
        }

        // Check play achievements
        count = Leaderboard.getGamesPlayed(player);
        name = "First Game";
        if (!playerData.get(name) && count >= 1) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Play a game");
        }
        name = "Ten Games";
        if (!playerData.get(name) && count >= 10) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Play ten games");
        }
        name = "One Hundred Games";
        if (!playerData.get(name) && count >= 100) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Play one hundred games");
        }
        name = "Two Hundred Games";
        if (!playerData.get(name) && count >= 200) {
            playerData.replace(name, true);
            broadcastAchievement(player, name, "Play two hundred games");
        }
    }

//    public static void checkKillAchievements(Player player, int numKills) {
//        HashMap<String, Boolean> playerData = data.get(player.getUniqueId());
//
//        // Handle "First Kill"
//        String name = "First Kill";
//        if (!playerData.get(name) && numKills > 1) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Kill a player");
//        }
//        // Handle "Ten Kills"
//        name = "Ten Kills";
//        if (!playerData.get(name) && numKills > 10) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Kill ten players");
//        }
//        // Handle "One Hundred Kills"
//        name = "One Hundred Kills";
//        if (!playerData.get(name) && numKills > 100) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Kill one hundred players");
//        }
//        // Handle "Two Hundred Kills"
//        name = "Two Hundred Kills";
//        if (!playerData.get(name) && numKills > 200) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Kill two hundred players");
//        }
//    }
//
//    public static void checkGameWonAchievements(Player player, int numWins) {
//        HashMap<String, Boolean> playerData = data.get(player.getUniqueId());
//
//        // Handle "First Win"
//        String name = "First Win";
//        if (!playerData.get(name) && numWins > 1) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Win a game");
//        }
//        // Handle "Ten Wins"
//        name = "Ten Wins";
//        if (!playerData.get(name) && numWins > 10) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Win ten games");
//        }
//        // Handle "One Hundred Wins"
//        name = "One Hundred Wins";
//        if (!playerData.get(name) && numWins > 100) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Win one hundred games");
//        }
//        // Handle "Two Hundred Wins"
//        name = "Two Hundred Wins";
//        if (!playerData.get(name) && numWins > 200) {
//            playerData.replace(name, true);
//            broadcastAchievement(player, name, "Win two hundred games");
//        }
//    }
//
//    public static void checkGameWonAchievements(Team team) {
//        String name = "Guaranteed Win";
//        for (Player currentPlayer : team.getPlayers()) {
//            // Handle "Guaranteed Win"
//            if (currentPlayer.getName().toLowerCase() == "arvein") {
//                // Add achievement to all players on arvein's team
//                for (Player player : team.getPlayers()) {
//                    HashMap<String, Boolean> playerData = data.get(player.getUniqueId());
//                    if (!playerData.get(name)) {
//                        playerData.replace(name, true);
//                        broadcastAchievement(player, name, "Win a game with Arvein on your team");
//                    }
//                }
//                break;
//            }
//        }
//    }
}
