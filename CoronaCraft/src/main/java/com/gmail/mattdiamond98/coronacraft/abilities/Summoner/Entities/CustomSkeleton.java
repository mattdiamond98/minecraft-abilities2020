package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class CustomSkeleton extends EntitySkeleton implements CustomEntity {
    Team team;
    Player hi;
    public CustomSkeleton(Location loc, Team t, Player p) {
        super(EntityTypes.SKELETON, ((CraftWorld) loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Skeleton - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Skeleton nicezombie=(Skeleton) this.getBukkitEntity();
        ItemStack helmet=t.getKind().getHat();

        nicezombie.getEquipment().setHelmet(helmet);
    }

    @Override
    protected void initPathfinder() {

        this.goalSelector.a(0, new PathfinderGoalHurtByTarget(this));
        this.targetSelector.a(0, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true, team));
        this.goalSelector.a(1, new PathfinderGoalMoveTowardsRestriction(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalMoveThroughVillage(this, 0.2D, false, 1, null));
        this.goalSelector.a(3, new PathfinderGoalRandomStrollLand(this, 1D));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
