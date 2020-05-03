package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CoolDownEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private Material item;

    public CoolDownEndEvent(Player player, Material item) {
        this.player = player;
        this.item = item;
    }

    public CoolDownEndEvent(AbilityKey key) {
        this(key.getPlayer(), key.getItem());
    }

    public Player getPlayer() {
        return player;
    }

    public Material getItem() {
        return item;
    }

    public Ability getAbility() { return CoronaCraft.getAbility(item); }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}