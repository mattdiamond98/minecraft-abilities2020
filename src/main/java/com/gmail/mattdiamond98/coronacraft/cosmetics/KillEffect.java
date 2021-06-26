package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.Location;

import java.util.ArrayList;

public abstract class KillEffect
{



    protected String name;
    public KillEffect(String name){
        this.name=name;
    }
    public abstract void runEffect(Location loc);
    public String getName(){
        return this.name;
    }
}
