package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;

import org.bukkit.Location;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;

public class FireWither extends EntityWither implements CustomEntity {
    Player hi;
    public FireWither(Location loc, Player p, Team t, float power) {
        super(EntityTypes.WITHER, ((CraftWorld)loc.getWorld()).getHandle());
        this.hi=p;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Fire Wither - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            Wither nicezombie=(Wither) this.getBukkitEntity();

            t.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Fire Wither"));
        }
        final EntityWither ew=this;
        BukkitRunnable b=new BukkitRunnable() {
            @Override
            public void run() {
                if(ew.getGoalTarget()!=null){
                    ((Wither)ew.getBukkitEntity()).launchProjectile(LargeFireball.class, ew.getBukkitEntity().getLocation().toVector().add(ew.getGoalTarget().getBukkitEntity().getLocation().toVector()));
                }
            }
        };

        b.runTaskTimer(CoronaCraft.instance, 20L, 30L);


    }

    @Override
    protected void initPathfinder() {

        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));

        this.targetSelector.a(0, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, false));


    }

    @Override
    public Player getPlayer() {
        return this.hi;
    }


}
