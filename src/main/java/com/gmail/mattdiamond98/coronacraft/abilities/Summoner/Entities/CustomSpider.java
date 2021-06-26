package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalSpiderMeleeAttacke;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

public class CustomSpider extends EntitySpider implements CustomEntity {
    Team team;
    Player hi;
    public CustomSpider(Location loc, Team t, Player p, float power) {
        super(EntityTypes.SPIDER, ((CraftWorld)loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;

        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Spider - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Spider nicezombie=(Spider) this.getBukkitEntity();
            nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()*1.5);
        this.team.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Spider"));
        }

    }
    @Override
    public void initPathfinder(){
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(2, new PathfinderGoalSpiderMeleeAttacke(this));

        this.goalSelector.a(3, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
       /*Predicate<EntityLiving> pred=new Predicate<EntityLiving>() {
            @Override
            public boolean test(EntityLiving entityLiving) {
                if(entityLiving.getBukkitEntity() instanceof Attributable){
                    return ((Attributable)entityLiving.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue()==0.8183471234;}
                return false;
            }
        };*/
       // this.targetSelector.a(0 , new PathfinderGoalNearestAttackableTarget<EntityZombieHusk>(this, EntityZombieHusk.class, 10, true, false, pred));
        this.targetSelector.a(0, new PathfinderGoalHurtByTarget(this));
        this.targetSelector.a(1, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));



    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
