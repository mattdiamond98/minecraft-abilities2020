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

public class LifeSwap extends ProjectileAbilityStyle {

    public LifeSwap() {
        super("Life Swap", new String[]{
                "Switch life totals when",
                "you hit an enemy with more",
                "hit points than you."
        }, "coronacraft.gladiator.lifeswap", 0);
    }

    @Override
    public int execute(Player shooter, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            Player hit = (Player) event.getHitEntity();
            Team team = Team.getTeamByPlayerName(hit.getName());
            if (team != null) {
                if (!team.getPlayers().contains(shooter)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        if (hit.getHealth() > shooter.getHealth()) {
                            double tmp = hit.getHealth();
                            hit.setHealth(shooter.getHealth());
                            shooter.setHealth(tmp);
                            hit.getWorld().playEffect(hit.getLocation(), Effect.STEP_SOUND, Material.EMERALD_BLOCK);
                            shooter.getWorld().playEffect(shooter.getLocation(), Effect.STEP_SOUND, Material.EMERALD_BLOCK);
                            shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                            shooter.sendMessage(ChatColor.GREEN + "You swapped hit points with " + hit.getName() + ".");
                            hit.sendMessage(ChatColor.RED + shooter.getName() + " swapped hit points with you.");
                        }
                    }, 1);
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

}
