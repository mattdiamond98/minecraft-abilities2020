package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/***
 * Event thrown that abilities can listen to
 * if they require constant timing.
 */
public class CoolDownTickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private Material item;
    private int ticksRemaining;

    public CoolDownTickEvent(Player player, Material item, int ticksRemaining) {
        this.player = player;
        this.item = item;
        this.ticksRemaining = ticksRemaining;
    }

    public CoolDownTickEvent(AbilityKey key, int ticksRemaining) {
        this(key.getPlayer(), key.getItem(), ticksRemaining);
    }

    public Player getPlayer() {
        return player;
    }

    public Material getItem() {
        return item;
    }

    public int getTicksRemaining() { return ticksRemaining; }

    public AbilityKey getCoolDownKey() {
        return new AbilityKey(player, item);
    }

    public Ability getAbility() { return CoronaCraft.getAbility(item); }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
