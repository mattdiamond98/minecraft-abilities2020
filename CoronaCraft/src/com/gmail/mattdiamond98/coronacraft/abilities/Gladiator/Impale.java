package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
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
                "your thrown spear, rooting",
                "them if they hit a wall"
        });
    }

    @Override
    public int execute(Player shooter, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            Player hit = (Player) event.getHitEntity();
            Team team = Team.getTeamByPlayerName(hit.getName());
            if (team != null) {
                if (!team.getPlayers().contains(shooter)) {
                    Vector direction = event.getEntity().getVelocity().setY(0).normalize().multiply(DISTANCE).setY(0.1);
                    hit.setVelocity(direction);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        hit.setVelocity(new Vector());
                    }, 5);
                    if (hit.getLocation().add(direction).getBlock().getType().isSolid()
                            || hit.getEyeLocation().add(direction).getBlock().getType().isSolid()) {
                        new PotionEffect(PotionEffectType.JUMP, 2 * 20, 128).apply(hit);
                        new PotionEffect(PotionEffectType.SLOW, 2 * 20, 7).apply(hit);
                        hit.sendMessage(ChatColor.RED + "You have been rooted to the ground.");
                        hit.getWorld().playEffect(hit.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                        hit.getWorld().playSound(hit.getLocation(), Sound.ENTITY_PILLAGER_HURT, 5, 1);
                    }
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
        return 6 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
