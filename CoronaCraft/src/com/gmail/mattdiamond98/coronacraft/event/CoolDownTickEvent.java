package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.CoolDownKey;
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

    public CoolDownTickEvent(Player player, Material item) {
        this.player = player;
        this.item = item;
    }

    public CoolDownTickEvent(CoolDownKey key) {
        this(key.getPlayer(), key.getItem());
    }

    public Player getPlayer() {
        return player;
    }

    public Material getItem() {
        return item;
    }

    public CoolDownKey getCoolDownKey() {
        return new CoolDownKey(player, item);
    }

    // TODO: get specific ability

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
