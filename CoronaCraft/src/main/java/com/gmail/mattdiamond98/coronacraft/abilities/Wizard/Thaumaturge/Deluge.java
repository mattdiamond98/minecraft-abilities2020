package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class Deluge extends WizardStyle {

    public static final int MANA_COST = 4;

    public static final int COOLDOWN_ABILITY_TICKS = 10;

    public Deluge() {
        super("Deluge", new String[]{
                        "Deluge",
                        String.format("%d Mana", MANA_COST),
                        "",
                        "Left click with wand to cast"
                },
                new Ability("Deluge", Material.POTION) {
                    @EventHandler
                    public void potionSplash(PotionSplashEvent event) {
                        if (event.getEntity().getShooter() instanceof Player && event.getEntity().getEffects().isEmpty()) {
                            event.setCancelled(true);
                            Player p = (Player) event.getEntity().getShooter();
                            Team team = Team.getTeamByPlayerName(p.getName());
                            event.getEntity().getWorld().getNearbyEntities(event.getEntity().getLocation().add(event.getEntity().getVelocity()),
                                    2.0, 2.5, 2.0).stream()
                                    .filter(entity -> entity instanceof Player)
                                    .map(entity -> (Player) entity)
                                    .filter(member -> Team.getTeamByPlayerName(member.getName()) != null)
                                    .peek(member -> member.setFireTicks(0))
                                    .filter(member -> !Team.getTeamByPlayerName(member.getName()).equals(team))
                                    .filter(member -> !AbilityUtil.inSpawn(member)).forEach(hit -> {
                                        hit.damage(6.0, p);
                                        hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                                        hit.getWorld().playSound(hit.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 0.4F, 0.4F);
                                        hit.getWorld().playEffect(hit.getEyeLocation(), Effect.STEP_SOUND, Material.WATER);
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                            hit.setVelocity(hit.getVelocity().add(event.getEntity().getLocation().getDirection().setY(0.5).normalize().multiply(0.3)));
                                        }, 2);
                                    }
                            );
                            Block block = event.getEntity().getLocation().getBlock();
                            Arrays.stream(BlockFace.values())
                                    .map(blockFace -> block.getRelative(blockFace))
                                    .filter(rel -> rel.getType() == Material.FIRE)
                                    .forEach(fire -> {
                                        fire.setType(Material.AIR);
                                        fire.getWorld().playEffect(fire.getLocation(), Effect.STEP_SOUND, Material.WATER);
                                        fire.getWorld().playSound(fire.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.3F, 0.3F);
                                    });

                        }
                    }
                });
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, true)) {
            p.setLevel(p.getLevel() - MANA_COST);
            CoronaCraft.setCooldown(p, getItem(), COOLDOWN_ABILITY_TICKS);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 0.5F, 0.5F);
            final ThrownPotion projectile = p.launchProjectile(ThrownPotion.class);
            ItemStack item = projectile.getItem();
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setColor(Color.BLUE);
            item.setItemMeta(meta);
            projectile.setItem(item);
            p.getWorld().playEffect(p.getEyeLocation(), Effect.STEP_SOUND, Material.WATER);
            projectile.setVelocity(projectile.getVelocity().add(p.getLocation().getDirection().normalize().multiply(2)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    projectile.getWorld().spawnParticle(Particle.WATER_SPLASH, projectile.getLocation(), 10, 1, 1, 1);
                    projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 0.2F, 0.25F);
                    if (projectile.isDead()) {
                        cancel();
                    }
                }
            }.runTaskTimer(CoronaCraft.instance, 1, 2);

        }
        return 0;
    }

}
