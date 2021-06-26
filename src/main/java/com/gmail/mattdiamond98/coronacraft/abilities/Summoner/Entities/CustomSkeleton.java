package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Predicate;


public class CustomSkeleton extends EntitySkeleton implements CustomEntity {
    Team team;
    Player hi;
    public CustomSkeleton(Location loc, Team t, Player p, float power) {
        super(EntityTypes.SKELETON, ((CraftWorld) loc.getWorld()).getHandle());
        this.hi=p;
        this.team=t;
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
        this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Skeleton - "+t.getName()+" Team"));
        this.setCustomNameVisible(true);
        Skeleton nicezombie=(Skeleton) this.getBukkitEntity();
        ItemStack helmet=t.getKind().getHat();
            ItemMeta im=helmet.getItemMeta();
            im.setUnbreakable(true);
            helmet.setItemMeta(im);

        nicezombie.getEquipment().setHelmet(helmet);
        nicezombie.getEquipment().setItemInMainHand(new ItemStack(Material.BOW, 1));
            nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()*1.5);
        this.team.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Skeleton"));
        }
    }

    @Override
    protected void initPathfinder() {

        this.goalSelector.a(0, new PathfinderGoalHurtByTarget(this));
        Predicate<EntityLiving> pred=new Predicate<EntityLiving>() {
            @Override
            public boolean test(EntityLiving entityLiving) {
                if(entityLiving.getBukkitEntity() instanceof Attributable){
                return ((Attributable)entityLiving.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue()==0.8183471234;}
                return false;
            }
        };
        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<EntityZombieHusk>(this, EntityZombieHusk.class, 10, true, false, pred));
        this.targetSelector.a(1, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));
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
