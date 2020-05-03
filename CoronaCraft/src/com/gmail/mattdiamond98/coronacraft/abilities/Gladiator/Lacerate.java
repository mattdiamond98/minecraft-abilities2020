package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Lacerate extends ProjectileAbilityStyle {

    public static final int BLEED_DURATION = 4; //How many cycles
    public static final int BLEED_TICK_TIME = 20;

    public Lacerate() {
        super("Lacerate", new String[]{
                "Bleed your enemies, dealing",
                "damage whenever they move for",
                "a short time after being hit."
        }, "coronacraft.gladiator.impale");
    }

    @Override
    public int execute(Player shooter, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            Player hit = (Player) event.getHitEntity();
            Team team = Team.getTeamByPlayerName(hit.getName());
            if (team != null) {
                if (!team.getPlayers().contains(shooter)) {
                    hit.sendMessage(ChatColor.RED + "You are bleeding! Moving without crouching will hurt you.");
                    new BukkitRunnable() {
                        private int counter = BLEED_DURATION;
                        public void run() {
                            if (hit.isDead() || !AbilityUtil.notInSpawn(hit)) {
                                cancel();
                            } else if (counter-- <= 0) {
                                cancel();
                                hit.sendMessage(ChatColor.GREEN + "You have stopped bleeding.");
                            } else if (!hit.isSneaking() && hit.getVelocity().lengthSquared() > 0.001) {
                                hit.getWorld().playEffect(hit.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                                hit.getWorld().playSound(hit.getEyeLocation(), Sound.ENTITY_PLAYER_HURT, 3, 1);
                                hit.damage(2, shooter);
                            }
                        }
                    }.runTaskTimer(CoronaCraft.instance, BLEED_TICK_TIME, BLEED_TICK_TIME);
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
