package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility;

import net.minecraft.server.v1_16_R3.EntitySlime;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.RandomPositionGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;

import java.util.Random;

public class PathfinderGoalCustomSlimeRandomJump extends PathfinderGoal {
    private EntitySlime a;
    private final double f;
    private double c;
    private double d;
    private double e;


    public PathfinderGoalCustomSlimeRandomJump(EntitySlime a, double f) {
        this.a = a;
        this.f = f;

    }

    @Override
    public boolean a() {
        if(this.a.getGoalTarget()!=null){
            Bukkit.broadcastMessage("Target isnt null");
        return false;}
        Bukkit.broadcastMessage("Giant Slime a ran");
        return true;
    }

    public void c(){
        Bukkit.broadcastMessage("Giant Slime c ran");
        Random r=this.a.getRandom();
        double x=r.nextInt(20)-10;
        double z=r.nextInt(20)-10;
        Bukkit.broadcastMessage(String.valueOf(x));


        int y=this.a.getBukkitEntity().getLocation().getBlockY();
        Location loc=new Location(this.a.getBukkitEntity().getWorld(), this.a.getBukkitEntity().getLocation().getX()+x,y, this.a.getBukkitEntity().getLocation().getZ()+z);
        boolean while1ran=false;
        /*while(loc.getBlock().getType().equals(Material.AIR)){
            while1ran=true;
            y--;
            loc=new Location(this.a.getBukkitEntity().getWorld(), this.a.getBukkitEntity().getLocation().getX()+x,y, this.a.getBukkitEntity().getLocation().getZ()+z);
        }
        if(while1ran){
            y++;
        loc=loc.add(0, 1, 0);
        while(!loc.getBlock().getType().equals(Material.AIR)){
            y++;
            loc=loc.add(0, 1, 0);

        }}*/

        this.a.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), this.f);

        Bukkit.broadcastMessage("Giant Slime c finished");
        Bukkit.broadcastMessage(String.valueOf(loc.getX())+"r"+String.valueOf(loc.getZ())+"rr"+String.valueOf(loc.getY()));
    }
    public boolean b(){
        return !this.a.getNavigation().m();
    }
    public void d(){

    }
}
