package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.EventExecutable;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PrimordialArrow extends ProjectileAbilityStyle {

    public static int ARROW_COST = 40;

    private static Random rand = new Random();

    public PrimordialArrow() {
        super("Primordial Arrow", new String[] {
                "Shoot an arrow by crouching that",
                "spawns a poisonous thicket where",
                "it lands.",
                "Cost: 40 Arrows"
        }, "coronacraft.ranger.primordialarrow", 823456);
    }

    @Override
    public int onShoot(final Projectile projectile) {
        if (!(projectile.getShooter() instanceof Player)) return 0;
        int arrowCount = AbilityUtil.getTotalCount((Player) projectile.getShooter(), Material.ARROW);
        if (arrowCount >= ARROW_COST - 1) {
            AbilityUtil.setStackCount((Player) projectile.getShooter(), Material.ARROW, arrowCount - ARROW_COST - 1);
            projectile.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, this));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (projectile.isDead() || projectile.isOnGround() || projectile.getTicksLived() > 1000) {
                        this.cancel();
                        return;
                    }
                    Location loc = projectile.getLocation();
                    Warzone zone = Warzone.getZoneByLocation(loc);
                    if (zone == null || zone.isReinitializing()) {
                        this.cancel();
                        projectile.remove();
                        return;
                    }
                    loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.SPRUCE_LEAVES);
                }
            }.runTaskTimer(CoronaCraft.instance, 2, 2);
        } else {
            AbilityUtil.notifyAbilityRequiresResources((Player) projectile.getShooter(), Material.ARROW, ARROW_COST);
        }
        return 0;
    }

    @Override
    public int execute(Player player, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        Projectile projectile = event.getEntity();
        Location loc = projectile.getLocation();
        if (projectile.isDead()) projectile.remove();
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(true)
                .with(FireworkEffect.Type.BURST).withColor(Color.GREEN).build();
        new InstantFirework(fireworkEffect, loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 0.5F);
        for (int i = 0; i < 16; i++) {
            final FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, Bukkit.createBlockData(Material.SPRUCE_LEAVES));
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector(rand.nextFloat() * 0.7 - 0.35, 0.3F + (rand.nextFloat() * 0.3), rand.nextFloat() * 0.7 - 0.35));

            fallingBlock.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, onLeafLand));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (fallingBlock.isDead() || fallingBlock.isOnGround() || fallingBlock.getTicksLived() > 40) {
                        fallingBlock.remove();
                        cancel();
                    }
                    fallingBlock.getWorld().spawnParticle(Particle.TOTEM, fallingBlock.getLocation(), 30, 0.2F, 0.2F, 0.2F, 0.1F);
                    final Location tempLoc = fallingBlock.getLocation();
                    if (Warzone.getZoneByLocation(tempLoc) != null) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                            Block block = tempLoc.getBlock();
                            if (block.getType() != Material.SPRUCE_LEAVES) {
                                block.setType(Material.SPRUCE_LEAVES);
                                tempLoc.getWorld().playEffect(tempLoc, Effect.STEP_SOUND, Material.SPRUCE_LEAVES);
                            }
                        }, 20);
                    }
                }
            }.runTaskTimer(CoronaCraft.instance, 2, 1);
        }
        return 0;
    }

    private final EventExecutable onLeafLand = blockEvent -> {
        if (blockEvent instanceof EntityChangeBlockEvent) {
            EntityChangeBlockEvent e = (EntityChangeBlockEvent) blockEvent;
            if (e.getEntityType() == EntityType.FALLING_BLOCK && e.getEntity().getTicksLived() <= 40) {
                if (e.getEntity().hasMetadata(MetadataKey.ON_HIT)) {
                    e.getEntity().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, Material.SPRUCE_LEAVES);
                    Block under = e.getBlock().getRelative(0, -1, 0);
                    for (Block block : new Block[]{
                            e.getBlock().getRelative(0, -1, 0),
                            e.getBlock().getRelative(0,-1,1),
                            e.getBlock().getRelative(0, -1, -1),
                            e.getBlock().getRelative(1, -1, 0),
                            e.getBlock().getRelative(-1, -1, 0),
                            e.getBlock().getRelative(1, -1, 1),
                            e.getBlock().getRelative(1, -1, -1),
                            e.getBlock().getRelative(-1, -1, 1),
                            e.getBlock().getRelative(-1, -1, -1),

                    }) {
                        if (AbilityUtil.validBlock(block) && !block.getType().isAir()) {
                            block.setType(Material.GRASS_BLOCK);
                        } else {
                            e.getEntity().remove();
                            e.setCancelled(true);
                            break;
                        }
                    }
                    for (Block block : new Block[]{
                            e.getBlock().getRelative(0,0,1),
                            e.getBlock().getRelative(0, 0, -1),
                            e.getBlock().getRelative(1, 0, 0),
                            e.getBlock().getRelative(-1, 0, 0),
                            e.getBlock().getRelative(1, 0, 1),
                            e.getBlock().getRelative(1, 0, -1),
                            e.getBlock().getRelative(-1, 0, 1),
                            e.getBlock().getRelative(-1, 0, -1),

                    }) {
                        if (Warzone.getZoneByLocation(block.getLocation()) != null) {
                            block.setType(Material.SWEET_BERRY_BUSH);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                if (block.getBlockData() instanceof  Ageable) {
                                    Ageable state = (Ageable) block.getBlockData();
                                    state.setAge(2);
                                    block.setBlockData(state);
                                }
                            }, 1);
                        } else {
                            e.getEntity().remove();
                            e.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
    };
}
