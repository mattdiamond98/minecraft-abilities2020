package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.mysql.fabric.xmlrpc.base.Array;
import com.tommytony.war.Team;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.*;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class SummonerBiome extends AbilityStyle implements Listener {
    private Entity[] Possiblecreatures;
    private ArrayList<Entity> Activecreatures= new ArrayList<Entity>();
    private String name;
    private Entity Boss;
    private String permission;
    private String[] description;
    private int maxcreatures=100;
    public ArrayList<Entity> getPlayerEntities(Player p){
        ArrayList<Entity> returnvalue=new ArrayList<Entity>();
        for(Entity e:Activecreatures){

        CustomEntity ce=(CustomEntity) e;
        Player player=ce.getPlayer();

        if(player.equals(p)){
            returnvalue.add(e);

        }
        }
        return returnvalue;
    }

    public SummonerBiome(Entity[] creations, String BiomeName, Entity boss, String permissiony, String[] descriptiony, int maxcreaturesy){
        super(BiomeName, descriptiony, 0);

        this.Possiblecreatures=creations;
        this.name=BiomeName;
        this.Boss=boss;
        this.permission=permissiony;
        this.description=descriptiony;
        this.maxcreatures=maxcreaturesy;
    }
    public void removeEntity(Entity e){
        this.Activecreatures.remove(e);
    }
public ArrayList<Entity> getActivecreatures(){
        return this.Activecreatures;
}
public static SummonerBiome[] getAllBiomes(){
        return new SummonerBiome[]{new CaveBiome()};
}
public static SummonerBiome getPlayerBiome(Player p){
        if(Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getItem(4)).getItemMeta()).getDisplayName().toLowerCase().contains("biome")){
            switch(ChatColor.stripColor(p.getInventory().getItem(4).getItemMeta().getDisplayName()).toLowerCase().split(" ")[0]){
                case "cave":
                    return new CaveBiome();
            }


        }
        return null;
}


public void SpawnBoss(WorldServer world, Player p, Location loc, Team t){

        SpawnCreature(world, this.getEntityByName(this.Boss.getCustomName().toString(), t, loc, p), p);


}

    public void SpawnCreature(WorldServer world, Entity Creature, Player p){
        if(!(this.getPlayerEntities(p).size()>=this.maxcreatures)){
         Bukkit.broadcastMessage("55"+String.valueOf(Creature==null));
        world.addEntity(Creature, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.Activecreatures.add(Creature);}


    }

    public int CallAllCreatures(Player p, Location loc){
        BukkitRunnable b=new BukkitRunnable() {
            @Override
            public void run() {



        for(Entity et:getPlayerEntities(p)){

          EntityInsentient ei= (EntityInsentient) et;

            ei.getNavigation().a(loc.getBlockX(), p.getLocation().getBlockY(), loc.getBlockZ(), ((CraftLivingEntity)et.getBukkitEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getDefaultValue()*1.5);


        }}};
        b.runTaskTimer(CoronaCraft.instance, 1L, 1L);
        BukkitRunnable b2=new BukkitRunnable() {
            @Override
            public void run() {
                b.cancel();
            }
        };
        b2.runTaskLater(CoronaCraft.instance, 200L);
      /* org.bukkit.entity.Entity e= p.getWorld().spawnEntity(loc, EntityType.HUSK);
       e.setInvulnerable(true);
      //  ((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 25, false, false));
        ((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100, false, false));
        ((LivingEntity)e).setAI(false);
        ((LivingEntity)e).setCollidable(false);
        ((LivingEntity)e).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.8183471234);
        for(Entity et:getPlayerEntities(p)){
            ((EntityInsentient)et).setGoalTarget((EntityLiving)( (CraftCreature)e).getHandle());
        }
        BukkitRunnable r1=new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("Removing Husk");
                e.remove();

            }

        };

        r1.runTaskLater(CoronaCraft.instance, 200L);*/

return 30;
    }
    public int SpawnRandomCreature(WorldServer world, Team t, Location loc, Player p){

        if(p==null){
            Bukkit.broadcastMessage("Player is null");
        }
       if(!(this.getPlayerEntities(p).size()>=maxcreatures)){
        Bukkit.broadcastMessage("Current Amount of entities for player "+p.getName() +"is:"+String.valueOf(this.getPlayerEntities(p).size())+" And max creatures is "+String.valueOf(maxcreatures));


        Random r=new Random();
        int random=r.nextInt(this.Possiblecreatures.length);

        Entity es=Possiblecreatures[random];

        String string=Possiblecreatures[random].getCustomName().toString();
        Entity entity=getEntityByName(string, t, loc, p);
world.addEntity(entity);

this.Activecreatures.add(entity);}
     return (int) ((int)10*(Math.pow(2, this.getPlayerEntities(p).size())));
    }
    public abstract Entity getEntityByName(String name, Team t, Location loc, Player p);
    public Entity[] getCreatures(){

        return Possiblecreatures;
    }

}
