package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Predicate;

public class SplitterSilverfish extends EntitySilverfish implements CustomEntity {
    Team team;
    Player hi;
    public SplitterSilverfish(Location loc, Team t, Player p) {
        super(EntityTypes.SILVERFISH, ((CraftWorld)loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Splitter Silverfish - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            Silverfish nicezombie=(Silverfish) this.getBukkitEntity();
            nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()*1.5);
            this.team.addNonPlayerEntity(this.getBukkitEntity());
            SplitterSilverfish ss=this;
            BukkitRunnable b=new BukkitRunnable() {
                @Override
                public void run() {
                    if(ss.isAlive()&& UltimateTracker.isUltimateActive(ss.getPlayer().getUniqueId())){
                        SplitterSilverfish ss1=new SplitterSilverfish(ss.getBukkitEntity().getLocation(), Team.getNonPlayerEntityTeam(ss.getBukkitEntity()), ss.getPlayer());
                        SplitterSilverfish ss2=new SplitterSilverfish(ss.getBukkitEntity().getLocation(), Team.getNonPlayerEntityTeam(ss.getBukkitEntity()), ss.getPlayer());
                        ss.getWorld().addEntity(ss1);
                        ss.getWorld().addEntity(ss2);
                        ss.getBukkitEntity().remove();
                    }else{
                        this.cancel();
                    }
                }
            };b.runTaskTimer(CoronaCraft.instance, 80L, 100L);}else{
            this.setCustomName(new ChatComponentText("Splitter Silverfish"));
        }



    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1D));
        Predicate<EntityLiving> pred=new Predicate<EntityLiving>() {
            @Override
            public boolean test(EntityLiving entityLiving) {
                if(entityLiving.getBukkitEntity() instanceof Attributable){
                    return ((Attributable)entityLiving.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue()==0.8183471234;}
                return false;
            }
        };
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityZombieHusk>(this, EntityZombieHusk.class, 10, true, false, pred));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this)));
        this.targetSelector.a(2, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));
    }

    @Override
    public Player getPlayer() {
        return hi;
    }
}
