package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerTutorialCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player p;
    public PlayerTutorialCompleteEvent(Player player){
        this.p=player;

    }
    public Player getPlayer(){
        return this.p;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
