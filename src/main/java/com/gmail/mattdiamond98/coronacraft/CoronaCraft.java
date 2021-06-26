package com.gmail.mattdiamond98.coronacraft;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules.*;
import com.gmail.mattdiamond98.coronacraft.abilities.*;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.Detonator;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.Launcher;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.TNTGenerator;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.Rage;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.Waraxe;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.Schematic;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.Stockpile;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.Rush;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.SwordStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.Net;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.SpearThrow;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.HealingPulse;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.HolySword;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.PotionGenerator;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.NinjaMovement;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShadowKnife;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShurikenBag;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.Longbow;
import com.gmail.mattdiamond98.coronacraft.abilities.Reaper.*;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.Shortsword;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.Trap;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.*;

import com.gmail.mattdiamond98.coronacraft.abilities.Tank.Rally;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Wand;
import com.gmail.mattdiamond98.coronacraft.command.CoronaCommand;
import com.gmail.mattdiamond98.coronacraft.cosmetics.CosmeticManager;
import com.gmail.mattdiamond98.coronacraft.cosmetics.Gadget;
import com.gmail.mattdiamond98.coronacraft.cosmetics.GadgetType;
import com.gmail.mattdiamond98.coronacraft.data.PlayerData;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoronaCraftTickEvent;
import com.gmail.mattdiamond98.coronacraft.event.PlayerEventListener;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class CoronaCraft extends JavaPlugin {

    public static CoronaCraft instance;

    private static final Logger log = Logger.getLogger("Minecraft");
    private static ProtocolManager protocolManager;
    private static Economy econ = null;
    private static Permission perms = null;
    public static Location randomLoc;
    public static final int ABILITY_TICK_FREQ = 10;
    public static final int ABILITY_TICK_PER_SECOND = 20 / ABILITY_TICK_FREQ;

    private static final Map<Material, Ability> ABILITIES = new HashMap<>();

    private static final Map<AbilityKey, Integer> PLAYER_ABILITIES = new HashMap<>();
    private static final Map<AbilityKey, Integer> PLAYER_COOL_DOWNS = new HashMap<>();
    private static final Map<PlayerTimerKey, Integer> PLAYER_TASK_MAP = new HashMap<>();

    private double coinMultiplier = 1.0;

    @Override
    public void onEnable(){
        instance = this;
        Leaderboard.initialize();
       // Bukkit.getPluginCommand("adventcalendar").setExecutor(new CoronaCommand());
        //Achievements.initialize();
        randomLoc=new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        protocolManager = ProtocolLibrary.getProtocolManager();
        getDataFolder().mkdir();
        setupPermissions();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeAbilities(
                new TNTGenerator(),
                new ShurikenBag(),
                new ShadowKnife(),
                new NinjaMovement(),
                new SwordStyle(),
                new Trap(),
                new Rally(),
                new Longbow(),
                new Net(),
                new SpearThrow(),
                new Launcher(),
                new Shortsword(),
                new Rage(),
                new Detonator(),
                new Schematic(),
                new Stockpile(),
                new Waraxe(),
                new Rush(),
                new Wand(),
                new Staff(),
                new Call(),
                new Biome(),
                new PotionGenerator(),
                new HolySword(),
                new HealingPulse(),
                new Scythe(),
                new Rose()
        );
        PluginManager pm=getServer().getPluginManager();
        pm.registerEvents(new PlayerEventListener(), this);
        pm.registerEvents(new Tutorial(), this);
        pm.registerEvents(new FoodRegen(), this);
        pm.registerEvents(new PickaxeRegen(), this);
        pm.registerEvents(new UltimateListener(), this);
        pm.registerEvents(new Leaderboard(), this);
        pm.registerEvents(new CosmeticManager(), this);
        pm.registerEvents(new AchievementManager(), this);
        for(Gadget g: GadgetType.ALL_TYPES){
            pm.registerEvents(g, this);
        }
        /*for (Loadout loadout : Loadout.values()) {
            if (loadout.getUltimate() != null)
                pm.registerEvents(loadout.getUltimate(), this);
        }*/
        for(UltimateAbility ua: AbilityUtil.ALL_ULTS){
            pm.registerEvents(ua, this);
        }

        //Initialize special rules
     //   new ManaSteal().enable(pm);
       // new WizardsOnlySpecialRule().enable(pm);

        Tutorial.initTutorial();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getPluginManager().callEvent(new CoronaCraftTickEvent());
            if (PLAYER_COOL_DOWNS.isEmpty()) return;
            for (AbilityKey key : new HashSet<>(PLAYER_COOL_DOWNS.keySet())) {
                if (key == null || !PLAYER_COOL_DOWNS.containsKey(key)) return;
                int new_time = PLAYER_COOL_DOWNS.get(key) - 1;
                if (new_time <= 0) {
                    PLAYER_COOL_DOWNS.remove(key);
                    CoolDownEndEvent coolDownEndEvent = new CoolDownEndEvent(key);
                    Bukkit.getPluginManager().callEvent(coolDownEndEvent);
                }
                else {
                    PLAYER_COOL_DOWNS.put(key, new_time);
                    CoolDownTickEvent coolDownTickEvent = new CoolDownTickEvent(key, new_time);
                    Bukkit.getPluginManager().callEvent(coolDownTickEvent);
                }
            }
        }, 0, 10); // Twice per second
    }

    public void initializeAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            ability.initialize();
            getServer().getPluginManager().registerEvents(ability, this);
            ABILITIES.put(ability.getItem(), ability);
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Map<Material, Ability> getAbilities() {
        return ABILITIES;
    }

    public static Ability getAbility(Material item) {
        return ABILITIES.get(item);
    }

    public static Map<AbilityKey, Integer> getPlayerCoolDowns() {
        return PLAYER_COOL_DOWNS;
    }

    public static Map<AbilityKey, Integer> getPlayerAbilities() {
        return PLAYER_ABILITIES;
    }

    public static boolean isOnCooldown(Player p, Material item) {
        if (p == null || item == null || !p.isOnline()) return false;
        return PLAYER_COOL_DOWNS.containsKey(new AbilityKey(p, item));
    }

    public static int getCooldown(Player p, Material item) {
        if (!isOnCooldown(p, item)) return 0;
        else return PLAYER_COOL_DOWNS.get(new AbilityKey(p, item));
    }

    public static void setCooldown(Player p, Material item, int coolDown) {
        if (coolDown <= 0) {
            if (isOnCooldown(p, item)) {
                AbilityKey key = new AbilityKey(p, item);
                if (PLAYER_COOL_DOWNS.remove(key) != null) {
                    CoolDownEndEvent coolDownEndEvent = new CoolDownEndEvent(key);
                    Bukkit.getPluginManager().callEvent(coolDownEndEvent);
                }
            }
        } else {
            PLAYER_COOL_DOWNS.put(new AbilityKey(p, item), coolDown);
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static ProtocolManager getProtocolManager() { return protocolManager; }

    @Override
    public void onDisable() {
        PlayerData.persistAll();
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        //Leaderboard.saveData();
        //Achievements.saveData();

    }
    public static void addPlayerTimer(PlayerTimerKey ptk, int taskId) {
        PLAYER_TASK_MAP.put(ptk, taskId);
    }

    public static void addPlayerTimer(Player p, PlayerTimerKey.PlayerTimerType playerTimer, int taskId) {
        addPlayerTimer(new PlayerTimerKey(p, playerTimer), taskId);
    }

    public static void removePlayerTimer(PlayerTimerKey ptk) {
        if (PLAYER_TASK_MAP.containsKey(ptk))
            PLAYER_TASK_MAP.remove(ptk);
    }

    public static void removePlayerTimer(Player p, PlayerTimerKey.PlayerTimerType playerTimer) {
        removePlayerTimer(new PlayerTimerKey(p, playerTimer));
    }

    public static int getTaskId(PlayerTimerKey ptk) {
        if (!PLAYER_TASK_MAP.containsKey(ptk)) return -1;
        return PLAYER_TASK_MAP.get(ptk);
    }

    public static int getTaskId(Player p, PlayerTimerKey.PlayerTimerType playerTimer) {
        return getTaskId(new PlayerTimerKey(p, playerTimer));
    }



    public File getPlayerDataFolder() {
        File dataFolder = this.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File playerDataFolder = new File(dataFolder.getAbsolutePath() + "/users");
        if (!playerDataFolder.exists()) playerDataFolder.mkdir();
        return playerDataFolder;
    }

}
