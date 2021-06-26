package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;

public class BarbedArrow extends ProjectileAbilityStyle implements Listener {

    public static int ARROW_COST = 16;
    private ArrayList<Player> barbedPlayers = new ArrayList<Player>();
    private double DAMAGE_MODIFIER = 1.15;

    public BarbedArrow() {
        super("Barbed Arrow", new String[] {
                "When crouching, fire an arrow that makes",
                "an enemy take +15% damage for 6 seconds.",
                "Cost: " + ARROW_COST + " Arrows"
        }, "coronacraft.ranger.barbedarrow", 0);
    }

    /***
     * @param p the RateablePlayer
     * @param args arg0 instanceof Arrow the projectile
     * @return cooldown (currently 0)
     */
    public int execute(Player p, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        Projectile arrow = event.getEntity();
        Location loc = arrow.getLocation();

        // Safety checks
        if (arrow.isDead()) arrow.remove();

        // Handle arrow effects
        // Add player to the barbedPlayer list so that damage can be modified in onHit
        Player playerHit = (Player) event.getHitEntity();
        if (playerHit == null) { return 0; }

        if (!barbedPlayers.contains(playerHit)) {
            barbedPlayers.add(playerHit);
            playerHit.sendMessage(ChatColor.RED + "You were hit by a barbed arrow, " +
                    "increasing all damage taken by 15%.");

            // Handle special particles
            new BukkitRunnable() {
                private int counter = 6;

                public void run() {
                    // Handle stopping the effect
                    if (playerHit.isDead() || !AbilityUtil.notInSpawn(playerHit)) {
                        barbedPlayers.remove(playerHit);
                        cancel();
                    } else if (counter-- <= 0) {
                        barbedPlayers.remove(playerHit);
                        playerHit.sendMessage(ChatColor.GREEN + "The barbed arrow has fallen off.");
                        cancel();
                    }

                    // Create yellow particles on barbed player
                    else {
                        playerHit.getWorld().spawnParticle(Particle.REDSTONE,
                                playerHit.getLocation().clone().add(0, 1, 0), 1, new Particle.DustOptions(
                                        Color.fromRGB(255, 255, 0), 5));
                    }
                }
            }.runTaskTimer(CoronaCraft.instance, 20, 20);
        }

        return 0;
    }

    @Override
    public int onShoot(Projectile projectile) {
        if (Longbow.makeSpecialArrow(projectile, this, ARROW_COST)) {
            // Create runnable
            BukkitRunnable run = new BukkitRunnable() {
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
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1,
                            new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1));
                }
            };
            run.runTaskTimer(CoronaCraft.instance, 2, 2);
        }
        return 0;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        // Determine whether player is currently barbed
        if (event.getEntity() instanceof Player) {
            if (barbedPlayers.contains((Player) event.getEntity())) {
                event.setDamage(event.getDamage() * DAMAGE_MODIFIER);
            }
        }
    }
}
