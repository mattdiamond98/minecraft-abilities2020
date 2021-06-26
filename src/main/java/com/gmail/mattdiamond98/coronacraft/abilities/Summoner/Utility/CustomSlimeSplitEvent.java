package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomSlime;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomSlimeSplitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private CustomSlime slime;
    private boolean cancelled;
    private int Count;
    public CustomSlimeSplitEvent(CustomSlime s, int count){
    this.slime=s;
    this.Count=count;
    }
    public void setCount(int i){
        this.Count=i;
    }
    public int getCount(){
        return this.Count;
    }
    public CustomSlime getSlime(){
        return this.slime;

    }
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
