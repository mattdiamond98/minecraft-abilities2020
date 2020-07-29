package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Color;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomZombie extends EntityZombie implements CustomEntity {
    Team team;
    Player hi;
    public CustomZombie(Location loc, Team t, Player p){

        super(EntityTypes.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());
        this.team=t;
        this.hi=p;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Zombie - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Zombie nicezombie=(Zombie) this.getBukkitEntity();
        ItemStack helmet=t.getKind().getHat();

        nicezombie.getEquipment().setHelmet(helmet);

    }
    @Override
    public void initPathfinder(){
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this));
        this.targetSelector.a(1, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true, team));
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsRestriction(this, 2.0D));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.2D, false, 1, null));
        this.goalSelector.a(4, new PathfinderGoalRandomStroll(this, 1D));
        this.goalSelector.a(5, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));

    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
