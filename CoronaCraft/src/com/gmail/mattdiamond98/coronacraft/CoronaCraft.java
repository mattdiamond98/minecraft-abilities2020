package com.gmail.mattdiamond98.coronacraft;

import com.gmail.mattdiamond98.coronacraft.abilities.*;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.event.PlayerEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class CoronaCraft extends JavaPlugin {

    public static CoronaCraft instance;

    public static final int ABILITY_TICK_FREQ = 10;
    public static final int ABILITY_TICK_PER_SECOND = 20 / ABILITY_TICK_FREQ;

    private static final Set<Ability> ABILITIES = new HashSet<>();

    // Player metadata for tracking which sub-ability players have selected
    private static final Map<CoolDownKey, Integer> PLAYER_ABILITIES = new HashMap<>();
    // Player metadata for tracking which abilities are on cooldown
    private static final Map<CoolDownKey, Integer> PLAYER_COOL_DOWNS = new HashMap<>();

    @Override
    public void onEnable(){
        instance = this;

        initializeAbilities(
                new TNTGenerator(),
                new ShurikenBag(),
                new NinjaLeap(),
                new SwordStyle()
        );

        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (CoolDownKey key : PLAYER_COOL_DOWNS.keySet()) {
                int new_time = PLAYER_COOL_DOWNS.get(key) - 1;
                if (new_time <= 0) {
                    PLAYER_COOL_DOWNS.remove(key);
                    CoolDownEndEvent coolDownEndEvent = new CoolDownEndEvent(key);
                    Bukkit.getPluginManager().callEvent(coolDownEndEvent);
                }
                else {
                    PLAYER_COOL_DOWNS.put(key, new_time);
                    CoolDownTickEvent coolDownTickEvent = new CoolDownTickEvent(key);
                    Bukkit.getPluginManager().callEvent(coolDownTickEvent);
                }
            }
        }, 0, 10); // Twice per second
    }

    private void initializeAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            ability.initialize();
            getServer().getPluginManager().registerEvents(ability, this);
            ABILITIES.add(ability);
        }
    }

    public static final Set<Ability> getAbilities() {
        return ABILITIES;
    }

    public static final Map<CoolDownKey, Integer> getPlayerCoolDowns() {
        return PLAYER_COOL_DOWNS;
    }

    public static final Map<CoolDownKey, Integer> getPlayerAbilities() {
        return PLAYER_ABILITIES;
    }

    public static final Set<Ability> getAbilities(Material item) {
        return ABILITIES.stream().filter((ability) -> ability.getItem().equals(item)).collect(Collectors.toSet());
    }

    @Override
    public void onDisable(){}
}
