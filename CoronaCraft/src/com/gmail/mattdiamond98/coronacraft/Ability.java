package com.gmail.mattdiamond98.coronacraft;

import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class Ability implements Listener {

    protected String name;
    protected Material item;
    protected List<AbilityStyle> styles = new ArrayList<>();

    public Ability(String name, Material item) {
        this.name = name;
        this.item = item;
    }

    /***
     * Helper method, should be called on startup.
     * Useful for adding styles to list, setting up
     * recurring tasks, etc.
     */
    public void initialize() {}

    public final String getName() {
        return name;
    }

    public final Material getItem() {
        return item;
    }

    public final List<AbilityStyle> getStyles() { return styles; }

    public AbilityStyle getStyle(int position) {
        return styles.get(position);
    }

    public AbilityStyle getStyle(Player p) {
        return styles.get(CoronaCraft.getPlayerAbilities().getOrDefault(new AbilityKey(p, item), 0));
    }

    public int getStylePosition(AbilityStyle style) {
        return styles.indexOf(style);
    }

    public int getNextStylePosition(Player p) {
        int currentPosition = CoronaCraft.getPlayerAbilities().getOrDefault(new AbilityKey(p, item), 0);
        for (int nextPosition = (currentPosition + 1) % styles.size(); nextPosition != currentPosition;
             nextPosition = (nextPosition + 1) % styles.size()) {
            if (getStyle(nextPosition).canBeUsedBy(p)) return nextPosition;
        }
        return currentPosition;
    }

    public AbilityStyle getNextStyle(Player p) {
        return getStyle(getNextStylePosition(p));
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
