package com.gmail.mattdiamond98.coronacraft;

import org.bukkit.Material;
import org.bukkit.event.Listener;

public abstract class Ability implements Listener {

    private String name;
    private Material item;

    public Ability(String name, Material item) {
        this.name = name;
        this.item = item;
    }

    public void initialize() {}

    public final String getName() {
        return name;
    }

    public final Material getItem() {
        return item;
    }

    @Override
    public String toString() {
        return String.format("%s %s", name, item.toString());
    }

    @Override
    public int hashCode() {
        return name.hashCode() * item.hashCode();
    }


}
