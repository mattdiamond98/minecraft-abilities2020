package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class Fireball extends WizardStyle {

    public static final int MANA_COST = 20;

    public static final int COOLDOWN_SECONDS = 8;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public Fireball() {
        super(
                "Fireball",
                new String[] {
                    "Fireball",
                    String.format("%d Mana", MANA_COST),
                    String.format("%ds Cooldown", COOLDOWN_SECONDS),
                    "",
                    "Crouch + left click with wand to cast"
                },
                new Ability("Fireball", Material.FIRE_CHARGE) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                    @EventHandler
                    public void onProjectileHit(ProjectileHitEvent e) {
                        if (e.getEntityType() == EntityType.FIREBALL && e.getEntity().getShooter() instanceof Player) {
                            e.getEntity().getWorld().createExplosion(
                                    e.getEntity().getLocation(), 2.2F, true, false, (Player) e.getEntity().getShooter()
                            );
                            e.getEntity().remove();
                        }
                    }
                    @EventHandler
                    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
                        if (e.getEntity() instanceof org.bukkit.entity.Fireball && ((org.bukkit.entity.Fireball) e.getEntity()).getShooter() instanceof Player
                        && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                            e.getEntity().getWorld().createExplosion(
                                    e.getEntity().getLocation(), 2.2F, true, false,
                                    (Player) ((org.bukkit.entity.Fireball) e.getEntity()).getShooter()
                            );
                            e.getEntity().remove();
                        }
                    }
                }
        );
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST)) {
            p.setLevel(p.getLevel() - MANA_COST);
            CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
            org.bukkit.entity.Fireball fireball = p.launchProjectile(org.bukkit.entity.Fireball.class);
            fireball.setIsIncendiary(true);
            fireball.setVelocity(fireball.getVelocity().multiply(2.0));
        }
        return 0;
    }

}
