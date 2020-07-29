package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.mysql.fabric.xmlrpc.base.Array;
import com.tommytony.war.Team;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftMonster;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;

public abstract class SummonerBiome extends AbilityStyle {
    private Entity[] Possiblecreatures;
    private ArrayList<Entity> Activecreatures;
    private String name;
    private Entity Boss;
    private String permission;
    private String[] description;
    private int maxcreatures=100;
    public ArrayList<Entity> getPlayerEntities(Player p){
        ArrayList<Entity> returnvalue=new ArrayList<Entity>();
        for(Entity e:Activecreatures){
        if(((CustomEntity) e).getPlayer().equals(p)){
            returnvalue.add(e);

        }
        }
        return returnvalue;
    }
    public SummonerBiome(Entity[] creations, String BiomeName, Entity boss, String permissiony, String[] descriptiony, int maxcreaturesy){
        super(BiomeName, descriptiony, permissiony, 0);

        this.Possiblecreatures=creations;
        this.name=BiomeName;
        this.Boss=boss;
        this.permission=permissiony;
        this.description=descriptiony;
        this.maxcreatures=maxcreaturesy;
    }

    @EventHandler
    public void OnEntityDeath(EntityDeathEvent e){

try{
    if(e.getEntity() instanceof  CustomEntity){
        Activecreatures.remove(e.getEntity());

    }}catch (NullPointerException es){


}

    }
public void SpawnBoss(WorldServer world, Player p){

        SpawnCreature(world, this.Boss, p);


}

    public void SpawnCreature(WorldServer world, Entity Creature, Player p){
        if(!(this.getPlayerEntities(p).size()>=maxcreatures)){
        world.addEntity(Creature);
        this.Activecreatures.add(Creature);}


    }

    public int CallAllCreatures(Player p){
        for(Entity e:getPlayerEntities(p)){


        }

return 30;
    }
    public int SpawnRandomCreature(WorldServer world, Team t, Location loc, Player p){
        if(!(this.getPlayerEntities(p).size()>=maxcreatures)){
        Random r=new Random();
        int random=r.nextInt(this.Possiblecreatures.length);
        Entity entity=getEntityByName(Possiblecreatures[random].getCustomName().toString().split(" ")[0], t, loc, p);
world.addEntity(entity);
this.Activecreatures.add(entity);}
     return (int) ((int)10*(Math.pow(2, this.getPlayerEntities(p).size())));
    }
    public abstract Entity getEntityByName(String name, Team t, Location loc, Player p);
    public Entity[] getCreatures(){

        return Possiblecreatures;
    }

}
