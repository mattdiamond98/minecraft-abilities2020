package com.gmail.mattdiamond98.coronacraft.cosmetics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Gadget implements Listener {
    protected String name;
    protected Material m;
    public Gadget(String name, Material m){
        this.name=name;
        this.m=m;
    }
    public abstract void runEffect(Player p);
    public String getName(){
        return this.name;
    }
    public Material getMaterial(){
        return this.m;
    }
}
