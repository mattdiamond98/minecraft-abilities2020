package com.gmail.mattdiamond98.coronacraft.cosmetics;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class UltimateActivateEffect {
    protected String name;
    public UltimateActivateEffect(String name){
        this.name=name;
    }
    public abstract void runEffect(Player p);
    public String getName(){
        return this.name;
    }
}
