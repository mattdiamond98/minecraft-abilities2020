package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.PathfinderGoalSpiderHelpTeam;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.PathfinderGoalSpiderMeleeAttacke;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomSpider extends EntitySpider implements CustomEntity {
    Team team;
    Player hi;
    public CustomSpider(Location loc, Team t, Player p) {
        super(EntityTypes.SPIDER, ((CraftWorld)loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Spider - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Spider nicezombie=(Spider) this.getBukkitEntity();



    }
    @Override
    public void initPathfinder(){
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(2, new PathfinderGoalSpiderMeleeAttacke(this));

        this.goalSelector.a(3, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(1, new PathfinderGoalSpiderHelpTeam<EntityLiving>(this, EntityLiving.class, this.team));



    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
