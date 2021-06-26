package com.gmail.mattdiamond98.coronacraft.util;

import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Achievement {
    static String permHeader="coronacraft.achievement.";
    String name;
    String permission;
    int pointvalue;
    String description;
    public Achievement(String name, String description, int pointvalue){
        this.name=name;
        this.permission=permHeader+name.replaceAll(" ", "").toLowerCase();
        this.pointvalue=pointvalue;
        this.description=description;
    }
    public Achievement(String name, String description, String permission, int pointvalue){
        this.name=name;
        this.permission=permHeader+permission.toLowerCase();
        this.pointvalue=pointvalue;
        this.description=description;
    }




}
