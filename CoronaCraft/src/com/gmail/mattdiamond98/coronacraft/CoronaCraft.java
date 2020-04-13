package com.gmail.mattdiamond98.coronacraft;

import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.Detonator;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.Launcher;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.TNTGenerator;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.Rage;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.Waraxe;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.Schematic;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.Stockpile;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.SwordStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.Net;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.NinjaMovement;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShadowKnife;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShurikenBag;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.Longbow;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.Shortsword;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.Rally;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.event.PlayerEventListener;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CoronaCraft extends JavaPlugin {

    public static CoronaCraft instance;

    public static final int ABILITY_TICK_FREQ = 10;
    public static final int ABILITY_TICK_PER_SECOND = 20 / ABILITY_TICK_FREQ;

    private static final Map<Material, Ability> ABILITIES = new HashMap<>();

    // Player metadata for tracking which sub-ability players have selected
    private static final Map<AbilityKey, Integer> PLAYER_ABILITIES = new HashMap<>();
    // Player metadata for tracking which abilities are on cooldown
    private static final Map<AbilityKey, Integer> PLAYER_COOL_DOWNS = new HashMap<>();

    @Override
    public void onEnable(){
        instance = this;

        initializeAbilities(
                new TNTGenerator(),
                new ShurikenBag(),
                new ShadowKnife(),
                new NinjaMovement(),
                new SwordStyle(),
                new Rally(),
                new Longbow(),
                new Net(),
                new Launcher(),
                new Shortsword(),
                new Rage(),
                new Detonator(),
                new Schematic(),
                new Stockpile(),
                new Waraxe()
        );

        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (AbilityKey key : new HashSet<AbilityKey>(PLAYER_COOL_DOWNS.keySet())) {
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

    private void initializeAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            ability.initialize();
            getServer().getPluginManager().registerEvents(ability, this);
            ABILITIES.put(ability.getItem(), ability);
        }
    }

    public static final Map<Material, Ability> getAbilities() {
        return ABILITIES;
    }

    public static final Ability getAbility(Material item) {
        return ABILITIES.get(item);
    }

    public static final Map<AbilityKey, Integer> getPlayerCoolDowns() {
        return PLAYER_COOL_DOWNS;
    }

    public static final Map<AbilityKey, Integer> getPlayerAbilities() {
        return PLAYER_ABILITIES;
    }

    public static final boolean isOnCooldown(Player p, Material item) {
        return PLAYER_COOL_DOWNS.containsKey(new AbilityKey(p, item));
    }

    public static final int getCooldown(Player p, Material item) {
        if (!isOnCooldown(p, item)) return 0;
        else return PLAYER_COOL_DOWNS.get(new AbilityKey(p, item));
    }

    public static final void setCooldown(Player p, Material item, int coolDown) {
        PLAYER_COOL_DOWNS.put(new AbilityKey(p, item), coolDown);
    }

    @Override
    public void onDisable(){}
}
