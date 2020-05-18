package com.gmail.mattdiamond98.coronacraft.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class UltimateAbility implements Listener {

    private String name;

    public UltimateAbility(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void activate(Player player) {}
}
