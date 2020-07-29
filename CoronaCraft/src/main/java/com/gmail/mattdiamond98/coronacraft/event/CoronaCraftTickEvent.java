package com.gmail.mattdiamond98.coronacraft.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/***
 * Functional event for ticking events not tied to abilities
 */
public class CoronaCraftTickEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
