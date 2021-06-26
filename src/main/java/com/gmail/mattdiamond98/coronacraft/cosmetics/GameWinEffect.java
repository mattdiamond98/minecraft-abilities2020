package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.tommytony.war.Warzone;
import org.bukkit.Location;

public abstract class GameWinEffect {
    protected String name;
    protected int yieldTicks;
    public GameWinEffect(String name, int yieldTicks){
        this.name=name;
        this.yieldTicks=yieldTicks;
    }
    public abstract void runEffect(Warzone w);
    public String getName(){
        return this.name;
    }

    public int getYieldTicks() {
        return yieldTicks;
    }
}
