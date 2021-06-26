package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UltimateEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private UltimateAbility ult;
    private Player p;
    public UltimateEndEvent(Player player, UltimateAbility ultimate){
        this.p=player;
        this.ult=ultimate;
    }
    public Player getPlayer(){
        return this.p;
    }

    public UltimateAbility getUlt() {
        return ult;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
