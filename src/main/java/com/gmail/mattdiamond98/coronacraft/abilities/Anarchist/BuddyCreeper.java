package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;


public class BuddyCreeper extends EntityCreeper implements CustomEntity {
    protected Player p;
    protected Team t;
    protected Location loc;
    public BuddyCreeper(Location loc, Player p, Team t) {
        super(EntityTypes.CREEPER, ((CraftWorld)loc.getWorld()).getHandle());
        this.p=p;
        this.t=t;
        this.loc=loc;

        this.setPosition(loc.getX(),loc.getY(), loc.getZ());
        Creeper buddy=(Creeper) this.getBukkitEntity();
        buddy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( buddy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()*1.75);
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Creeper - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            t.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Creeper"));
        }
    }

    @Override
    public void initPathfinder(){
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSwell(this));
        this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(4, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));
    }

    @Override
    public Player getPlayer() {
        return this.p;
    }
}
