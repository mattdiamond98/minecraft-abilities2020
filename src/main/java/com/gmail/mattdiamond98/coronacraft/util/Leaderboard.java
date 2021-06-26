package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarScoreCapEvent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Leaderboard implements Serializable, Listener {
    //private static transient final long serialVersionUID = 3455595407807914611L;
    private static String folderPath;
    private static String fileType;
    private static int autosaveInterval = 30 * 60;
    //private static Achievements achievementTracker;

    private static Map<UUID, Integer> killRecordsAll = new HashMap<>();
    private static Map<UUID, Integer> assistRecordsAll = new HashMap<>();
    private static Map<UUID, Integer> deathRecordsAll = new HashMap<>();
    private static Map<UUID, Integer> captureRecordsAll = new HashMap<>();
    private static Map<UUID, Integer> gamesPlayedRecordsAll = new HashMap<>();
    private static Map<UUID, Integer> gamesWonRecordsAll = new HashMap<>();

    private static int monthNum = -1;
    private static Map<UUID, Integer> killRecordsMonth = new HashMap<>();
    private static Map<UUID, Integer> assistRecordsMonth = new HashMap<>();
    private static Map<UUID, Integer> deathRecordsMonth = new HashMap<>();
    private static Map<UUID, Integer> captureRecordsMonth = new HashMap<>();
    private static Map<UUID, Integer> gamesPlayedRecordsMonth = new HashMap<>();
    private static Map<UUID, Integer> gamesWonRecordsMonth = new HashMap<>();




    public Leaderboard() {}

    public static void initialize() {
        loadData();

        // Create runnable to save regularly
        BukkitRunnable run = new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
                updateSigns();
            }
        };
        run.runTaskTimer(CoronaCraft.instance, 20 * autosaveInterval, 20 * autosaveInterval);
    }

    /**
     * Updates all leaderboard signs.
     */
    public static void updateSigns() {
        // Initialize variables
        World world = Bukkit.getServer().getWorld("world");
        Location headerLocation = new Location(world, 114, 23, 239);
        Location monthLocation = new Location(world, 114, 22, 239);
        Location allTimeLocation = new Location(world, 114, 21, 239);
        BlockVector increment = new BlockVector(-1, 0, 0);

        // Create header signs
        BlockState state = world.getBlockAt(headerLocation).getState();
        if (!(state instanceof Sign)) { return; }
        Sign sign = (Sign) state;
        LocalDateTime date = LocalDateTime.now(ZoneId.of("America/New_York")); // EST IS BEST TIMEZONE
        String formattedDate = date.format(DateTimeFormatter.ofPattern("hh:mma"));
        String[] newText = new String[]{"§a§lLEADERBOARD", "§l--------------------", "Last Updated at",
                formattedDate + " EST"};
        updateSignText(sign, newText);

        state = world.getBlockAt(monthLocation).getState();
        if (!(state instanceof Sign)) { return; }
        sign = (Sign) state;
        newText = new String[]{"§l------------>", "§6§lCURRENT MONTH", "§6§lRECORDS", "§l------------>"};
        updateSignText(sign, newText);

        state = world.getBlockAt(allTimeLocation).getState();
        if (!(state instanceof Sign)) { return; }
        sign = (Sign) state;
        newText = new String[]{"§l------------>", "§6§lALL-TIME", "§6§lRECORDS", "§l------------>"};
        updateSignText(sign, newText);

        // Iterate through connected signs to display specific stats
        headerLocation.add(increment);
        monthLocation.add(increment);
        allTimeLocation.add(increment);
        while (world.getBlockAt(headerLocation).getState() instanceof Sign) {
            // Figure out what stat should be displayed in the current column
            state = world.getBlockAt(headerLocation).getState();
            sign = (Sign) state;
            switch (ChatColor.stripColor(sign.getLine(2)).trim().toLowerCase()) {
                case "kill":
                case "kills":
                    newText = new String[]{"", "§lTotal", "§lKills", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(killRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(killRecordsAll));
                    }
                    break;
                case "assist":
                case "assists":
                    newText = new String[]{"", "§lTotal", "§lAssists", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(assistRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(assistRecordsAll));
                    }
                    break;
                case "death":
                case "deaths":
                    newText = new String[]{"", "§lTotal", "§lDeaths", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(deathRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(deathRecordsAll));
                    }
                    break;
                case "capture":
                case "captures":
                    newText = new String[]{"", "§lTotal", "§lCaptures", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(captureRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(captureRecordsAll));
                    }
                    break;
                case "win":
                case "wins":
                    newText = new String[]{"", "§lTotal", "§lWins", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(gamesWonRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(gamesWonRecordsAll));
                    }
                    break;
                case "play":
                case "plays":
                case "games played":
                    newText = new String[]{"", "§lTotal", "§lGames Played", ""};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(gamesPlayedRecordsMonth));
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, getTopPlayerStrings(gamesPlayedRecordsAll));
                    }
                    break;

                default:
                    newText = new String[]{"Unrecognized", "keyword.", "Please enter", "something else."};
                    updateSignText(sign, newText);

                    state = world.getBlockAt(monthLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, newText);
                    }
                    state = world.getBlockAt(allTimeLocation).getState();
                    if (state instanceof Sign) {
                        sign = (Sign) state;
                        updateSignText(sign, newText);
                    }
            }

            // Prepare for next iteration
            headerLocation.add(increment);
            monthLocation.add(increment);
            allTimeLocation.add(increment);
        }
    }

    /**
     * Gets properly-formatted strings for the top players in a given database
     * @param database The Map that contains all related records
     * @return A String array with four non-null elements with no length limit.
     */
    private static String[] getTopPlayerStrings(Map<UUID, Integer> database) {
        // Sort all UUIDs in the database based on their value
        LinkedList<Map.Entry<UUID, Integer>> sortedList = new LinkedList<>(database.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<UUID, Integer>>() {
            public int compare(Map.Entry<UUID, Integer> thing1, Map.Entry<UUID, Integer> thing2) {
                return thing2.getValue().compareTo(thing1.getValue());
            }
        });

        // Get the top four UUIDs in the database
        UUID[] uuids = new UUID[4];
        for (int i = 0; i < 4 && i < sortedList.size(); i++) {
            uuids[i] = sortedList.get(i).getKey();
        }

        // Create the string array to return
        String[] returnArray = new String[4];
        for (int i = 0; i < uuids.length; i++) {
            UUID uuid = uuids[i];
            if (uuid == null) {
                returnArray[i] = "§8§l--------------";
            }
            else {
                returnArray[i] = database.get(uuid).toString() + ": " + Bukkit.getOfflinePlayer(uuid).getName();
            }
        }

        return returnArray;
    }

    /**
     * Changes the text on a specified sign to match the text in the String array.
     * Automatically reduces the length of lines to 16 characters, not including formatting codes
     * @param sign The sign to update
     * @param newText The String array that includes the new text
     */
    private static void updateSignText(Sign sign, String[] newText) {
        int maxLength = 16;
        for (int i = 0; i < 4; i++) {
            int codeCount = newText[i].length() - newText[i].replace("§", "").length();
            if (newText[i].length() > maxLength + codeCount*2) {
                sign.setLine(i, newText[i].substring(0, maxLength + codeCount*2));
            }
            else {
                sign.setLine(i, newText[i]);
            }
        }
        sign.update();
    }

    /**
     * Saves all data in the folder specified in the loadData method
     * @return Whether the save was successful
     */
    public static boolean saveData() {
        System.out.println("Saving leaderboard data...");
        try {
            // Save all-time data
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(
                    new FileOutputStream(folderPath + "killRecordsAll" + fileType)));
            out.writeObject(killRecordsAll);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "assistRecordsAll" + fileType)));
            out.writeObject(assistRecordsAll);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "deathRecordsAll" + fileType)));
            out.writeObject(deathRecordsAll);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "captureRecordsAll" + fileType)));
            out.writeObject(captureRecordsAll);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "gamesPlayedRecordsAll" + fileType)));
            out.writeObject(gamesPlayedRecordsAll);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "gamesWonRecordsAll" + fileType)));
            out.writeObject(gamesWonRecordsAll);
            out.close();

            // Determine if month records should be reset
            LocalDate date = LocalDate.now(ZoneId.of("America/New_York")); // EST IS BEST TIMEZONE
            if (monthNum != date.getMonthValue()) {
                monthNum = date.getMonthValue();
                killRecordsMonth = new HashMap<>();
                assistRecordsMonth = new HashMap<>();
                deathRecordsMonth = new HashMap<>();
                captureRecordsMonth = new HashMap<>();
                gamesPlayedRecordsMonth = new HashMap<>();
                gamesWonRecordsMonth = new HashMap<>();
            }

            // Save month data
            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "currentMonthNum" + fileType)));
            out.writeObject(monthNum);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "killRecordsMonth" + fileType)));
            out.writeObject(killRecordsMonth);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "assistRecordsMonth" + fileType)));
            out.writeObject(assistRecordsMonth);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "deathRecordsMonth" + fileType)));
            out.writeObject(deathRecordsMonth);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "captureRecordsMonth" + fileType)));
            out.writeObject(captureRecordsMonth);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "gamesPlayedRecordsMonth" + fileType)));
            out.writeObject(gamesPlayedRecordsMonth);
            out.close();

            out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
                    folderPath + "gamesWonRecordsMonth" + fileType)));
            out.writeObject(gamesWonRecordsMonth);
            out.close();

            //Bukkit.broadcastMessage(ChatColor.AQUA + "Finished saving leaderboard data.");
            System.out.println("Finished saving leaderboard data.");

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(ChatColor.AQUA + "ERROR: Failed to save leaderboard data - report this to " +
                    "@the_oshawott on discord");
            return false;
        }
    }

    /**
     * Loads all leaderboard data
     * @return Whether loading was successful
     */
    private static boolean loadData() {
        //achievementTracker = new Achievements();
        System.out.println("Loading leaderboard data...");

        try {
            // Get file path and names
            folderPath = CoronaCraft.instance.getDataFolder().getAbsolutePath() + "/leaderboard/";
            fileType = ".txt";

            // Load all-time data
            BukkitObjectInputStream in;
            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "killRecordsAll" + fileType)));
                killRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("killRecordsAll.txt not found");
            }

            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "assistRecordsAll" + fileType )));
                assistRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("assistRecordsAll.txt not found");
            }

            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "deathRecordsAll" + fileType )));
                deathRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("deathRecordsAll.txt not found");
            }

            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "captureRecordsAll" + fileType )));
                captureRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("captureRecordsAll.txt not found");
            }

            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "gamesPlayedRecordsAll" + fileType )));
                gamesPlayedRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("gamesPlayedRecordsAll.txt not found");
            }

            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "gamesWonRecordsAll" + fileType )));
                gamesWonRecordsAll = (Map<UUID, Integer>) in.readObject();
                in.close();
            }
            catch (FileNotFoundException e) {
                System.out.println("gamesWonRecordsAll.txt not found");
            }

            // Determine if month data should be loaded
            try {
                in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                        folderPath + "currentMonthNum" + fileType )));
                monthNum = (int) in.readObject();
                in.close();
                LocalDate date = LocalDate.now(ZoneId.of("America/New_York")); // EST IS BEST TIMEZONE
                if (monthNum == date.getMonthValue()) {
                    // Load month data
                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "killRecordsMonth" + fileType)));
                        killRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("killRecordsMonth.txt not found");
                    }

                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "assistRecordsMonth" + fileType )));
                        assistRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("assistRecordsMonth.txt not found");
                    }

                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "deathRecordsMonth" + fileType )));
                        deathRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("deathRecordsMonth.txt not found");
                    }

                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "captureRecordsMonth" + fileType )));
                        captureRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("captureRecordsMonth.txt not found");
                    }

                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "gamesPlayedRecordsMonth" + fileType )));
                        gamesPlayedRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("gamesPlayedRecordsMonth.txt not found");
                    }

                    try {
                        in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(
                                folderPath + "gamesWonRecordsMonth" + fileType )));
                        gamesWonRecordsMonth = (Map<UUID, Integer>) in.readObject();
                        in.close();
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("gamesWonRecordsMonth.txt not found");
                    }
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("monthNum.txt not found");
            }

            System.out.println("Leaderboard data loaded successfully");
            updateSigns();
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void addKill(Player player) {
        // Add all-time record
        if (!killRecordsAll.containsKey(player.getUniqueId())) {
            killRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            killRecordsAll.replace(player.getUniqueId(), killRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!killRecordsMonth.containsKey(player.getUniqueId())) {
            killRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            killRecordsMonth.replace(player.getUniqueId(), killRecordsMonth.get(
                    player.getUniqueId()) + 1);
        }

        // Check achievements

    }

    public static void addAssist(Player player) {
        // Add all-time record
        if (!assistRecordsAll.containsKey(player.getUniqueId())) {
            assistRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            assistRecordsAll.replace(player.getUniqueId(), assistRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!assistRecordsMonth.containsKey(player.getUniqueId())) {
            assistRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            assistRecordsMonth.replace(player.getUniqueId(), assistRecordsMonth.get(player.getUniqueId()) + 1);
        }

        // Check achievements

    }

    public static void addDeath(Player player) {
        // Add all-time record
        if (!deathRecordsAll.containsKey(player.getUniqueId())) {
            deathRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            deathRecordsAll.replace(player.getUniqueId(), deathRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!deathRecordsMonth.containsKey(player.getUniqueId())) {
            deathRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            deathRecordsMonth.replace(player.getUniqueId(), deathRecordsMonth.get(player.getUniqueId()) + 1);
        }

        // Check achievements


    }

    public static void addCapture(Player player) {
        // Add all-time record
        if (!captureRecordsAll.containsKey(player.getUniqueId())) {
            captureRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            captureRecordsAll.replace(player.getUniqueId(), captureRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!captureRecordsMonth.containsKey(player.getUniqueId())) {
            captureRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            captureRecordsMonth.replace(player.getUniqueId(), captureRecordsMonth.get(player.getUniqueId()) + 1);
        }


    }

    public static void addGamePlayed(Player player) {
        // Add all-time record
        if (!gamesPlayedRecordsAll.containsKey(player.getUniqueId())) {
            gamesPlayedRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            gamesPlayedRecordsAll.replace(player.getUniqueId(), gamesPlayedRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!gamesPlayedRecordsMonth.containsKey(player.getUniqueId())) {
            gamesPlayedRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            gamesPlayedRecordsMonth.replace(player.getUniqueId(), gamesPlayedRecordsMonth.get(player.getUniqueId()) + 1);
        }

        // Check achievements

    }

    public static void addGameWon(Player player) {
        // Add all-time record
        if (!gamesWonRecordsAll.containsKey(player.getUniqueId())) {
            gamesWonRecordsAll.put(player.getUniqueId(), 1);
        }
        else {
            gamesWonRecordsAll.replace(player.getUniqueId(), gamesWonRecordsAll.get(player.getUniqueId()) + 1);
        }

        // Add month record
        if (!gamesWonRecordsMonth.containsKey(player.getUniqueId())) {
            gamesWonRecordsMonth.put(player.getUniqueId(), 1);
        }
        else {
            gamesWonRecordsMonth.replace(player.getUniqueId(), gamesWonRecordsMonth.get(player.getUniqueId()) + 1);
        }

        // Check achievements

    }

    public static int getKills(Player player) {
        if (!killRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return killRecordsAll.get(player.getUniqueId());
    }

    public static int getAssists(Player player) {
        if (!assistRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return assistRecordsAll.get(player.getUniqueId());
    }

    public static int getDeaths(Player player) {
        if (!deathRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return deathRecordsAll.get(player.getUniqueId());
    }

    public static int getCaptures(Player player) {
        if (!captureRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return captureRecordsAll.get(player.getUniqueId());
    }

    public static int getGamesPlayed(Player player) {
        if (!gamesPlayedRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return gamesPlayedRecordsAll.get(player.getUniqueId());
    }

    public static int getGamesWon(Player player) {
        if (!gamesWonRecordsAll.containsKey(player.getUniqueId())) { return -1; }
        return gamesWonRecordsAll.get(player.getUniqueId());
    }

    @EventHandler
    public void onGameWon(WarScoreCapEvent event) {
        for (Team team : event.getWinningTeams()) {
            for (Player player : team.getPlayers()) {
                addGameWon(player);
                if(!player.hasPermission("coronacraft.secondfloorleaderboard")&& getGamesWon(player)>=25&&getKills(player)>=200&&getCaptures(player)>=20){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+player.getName()+" permission set coronacraft.secondfloorleaderboard true");
                }

                // Check achievements

            }
        }
        for (Team team : event.getWarzone().getTeams()) {
            for (Player player : team.getPlayers()) {
                addGamePlayed(player);
            }
        }

        updateSigns();
    }
}