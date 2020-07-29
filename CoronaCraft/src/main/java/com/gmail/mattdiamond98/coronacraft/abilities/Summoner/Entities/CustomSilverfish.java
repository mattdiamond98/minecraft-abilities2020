package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class CustomSilverfish extends EntitySilverfish implements CustomEntity {
    Team team;
    Player hi;
    public CustomSilverfish(Location loc, Team t, Player p) {
        super(EntityTypes.SILVERFISH, ((CraftWorld)loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Silverfish - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Silverfish nicezombie=(Silverfish) this.getBukkitEntity();



    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1D));
        this.targetSelector.a(0, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[0]));
        this.targetSelector.a(1, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true, this.team));
    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
