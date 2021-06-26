package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.tommytony.war.War;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;


public abstract class SpecialRule implements Listener {
    public String name;
    public static ArrayList<SpecialRule> rules=new ArrayList<SpecialRule>();
    BukkitRunnable br=null;
    int brdelay=0;
    public SpecialRule(String name){
      this.name=name;


    }
    public SpecialRule(String name, BukkitRunnable br, int brdelay){
        this.name=name;
        this.br=br;
        this.brdelay=brdelay;
    }
    public void enable(PluginManager pm){
        rules.add(this);
        pm.registerEvents(this, CoronaCraft.instance);
        if(br!=null){
            br.runTaskTimer(CoronaCraft.instance, 0L, brdelay);
        }
    }


}
