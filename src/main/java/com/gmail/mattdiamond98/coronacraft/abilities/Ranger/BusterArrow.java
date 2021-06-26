package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarzoneConfig;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BusterArrow extends ProjectileAbilityStyle {

    public static int ARROW_COST = 30;

    public BusterArrow() {
        super("Buster Arrow", new String[] {
                "When crouching, fire an arrow that",
                "explodes after hitting something.",
                "Cost: " + ARROW_COST + " Arrows"
        }, "coronacraft.ranger.busterarrow", 0);
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
        Warzone zone = Warzone.getZoneByLocation(arrow.getLocation().add(arrow.getVelocity()));
        boolean destroy = zone == null ||
                zone.getWarzoneConfig().contains(WarzoneConfig.UNBREAKABLE) &&
                        zone.getWarzoneConfig().getBoolean(WarzoneConfig.UNBREAKABLE);
        arrow.getWorld().createExplosion(arrow.getLocation(),4.0F, false, !destroy, null);
        arrow.remove();

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
                            new Particle.DustOptions(Color.fromRGB(255, 0, 0), 2));
                }
            };
            run.runTaskTimer(CoronaCraft.instance, 2, 2);
        }
        return 0;
    }
}
