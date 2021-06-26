package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Impale extends ProjectileAbilityStyle {

    public static final double DISTANCE = 5.0;

    public Impale() {
        super("Impale", new String[]{
                "Knock back enemies hit by",
                "your thrown spear, stunning",
                "them if they hit a wall"
        }, 0);
    }

    @Override
    public int execute(Player shooter, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player&&!((Player) event.getHitEntity()).isBlocking()) {
            Player hit = (Player) event.getHitEntity();
            Team team = Team.getTeamByPlayerName(hit.getName());
            if (team != null) {
                if (!team.getPlayers().contains(shooter)) {
                    Vector direction = hit.getLocation().toVector().subtract(shooter.getLocation().toVector()).normalize().multiply(DISTANCE * 2);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        hit.setVelocity(new Vector());
                    }, 5);
                    if (rayTrace(hit.getLocation(), direction, 5) || rayTrace(hit.getEyeLocation(), direction, 5)) {
                        AbilityUtil.applyStunEffect(hit, 3 * 20);
                        hit.getWorld().playEffect(hit.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                        hit.getWorld().playSound(hit.getLocation(), Sound.ENTITY_PILLAGER_HURT, 1, 1);
                    }
                    hit.setVelocity(direction.setY(0.1));
                }
            }
        } else {
            event.getEntity().remove();
        }
        return 0;
    }

    @Override
    public int onShoot(Projectile projectile) {
        projectile.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, this));
        return 5 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }

    private boolean rayTrace(Location start, Vector direction, int iter) {
        direction = direction.clone().normalize();
        Location loc = start.add(direction);
        for (int i = 1; i < iter; i++) {
            Material type = loc.getBlock().getType();
            if (type.isSolid()) return true;
            start.add(direction);
        }
        return false;
    }
}
