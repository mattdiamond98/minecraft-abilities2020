package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.tournament.ItemSmith;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public abstract class UltimateAbility implements Listener {

    private String name;
    protected UltimateManager manager;
    protected String permission=null;
    protected Loadout loadout;
    public UltimateAbility(String name) {
        this.name = name;


    }
    public UltimateAbility(String name, String permission) {
        this.name = name;

        this.permission=permission;

    }
    public boolean needsPermission(){
        return this.permission!=null;
    }
    public String getName() {
        return name;
    }

    public String getName(Player player) {
        return getName();
    }
    public String getPermission(){

        return this.permission;
    }
    public void activate(Player player) {}

}
