package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EarthSurge extends WizardStyle {

    public static final int MANA_COST = 3;
    public static final int COOLDOWN_ABILITY_TICKS = 1;

    public EarthSurge() {
        super("Earth Surge", new String[]{
                        "Earth Surge",
                        String.format("%d Mana", MANA_COST),
                        "",
                        "Left click with wand to cast"
                },
                new Ability("Earth Surge", Material.COCOA_BEANS) {});
        ability.getStyles().add(this);
    }

    @Override
    public int execute(final Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, true)) {
            CoronaCraft.setCooldown(p, getItem(), COOLDOWN_ABILITY_TICKS);
            p.setLevel(p.getLevel() - MANA_COST);
            Material material = Material.COBBLESTONE;
            Location startLoc = p.getLocation().add(new Vector(Math.random() * 1 - 0.5, -2, Math.random() * 1 - 0.5));
            for (int i = 0; startLoc.getBlock().getType().isSolid() && i < 4; i++) {
                startLoc = startLoc.add(0, 1, 0);
                if (startLoc.getBlock().getType().isSolid()) material = startLoc.getBlock().getType();
            }
            startLoc.getWorld().playEffect(startLoc, Effect.STEP_SOUND, material);
            startLoc = startLoc.add(0, 1.6, 0);
            final Item earth = p.getWorld().dropItem(startLoc, new ItemStack(material, 1));
            earth.setPickupDelay(100_000);
            earth.setVelocity(new Vector(0, 0.1, 0));
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                if (valid(earth, p)) {
                    final Team team = Team.getTeamByPlayerName(p.getName());
                    Vector velocity = p.getLocation().getDirection().multiply(1.75);
                    velocity = velocity.setY(velocity.getY() + 0.2);
                    earth.setVelocity(velocity);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!valid(earth, p)) {
                                earth.remove();
                                cancel();
                            } else {
                                earth.getWorld().playEffect(earth.getLocation(), Effect.STEP_SOUND, earth.getItemStack().getType());
                                for (Entity entity : earth.getNearbyEntities(0.9, 0.9, 0.9)) {
                                    if (entity instanceof Player) {
                                        Player target = (Player) entity;
                                        if (!target.equals(p) && !AbilityUtil.inSpawn(target)) {
                                            Team other = Team.getTeamByPlayerName(target.getName());
                                            if (other != null) {
                                                cancel();
                                                earth.remove();
                                                target.getWorld().playEffect(target.getEyeLocation(), Effect.STEP_SOUND, earth.getItemStack().getType());
                                                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PILLAGER_HURT, 0.4F, 0.2F * (float) Math.random() + 0.3F);
                                                if (!other.equals(team)) {
                                                    if (StoneWard.projectileDamage(target, earth)) {
                                                        target.damage(6, p);
                                                        target.setVelocity(earth.getVelocity().normalize().multiply(0.4).setY(0.2));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(CoronaCraft.instance, 1, 1);
                }
            }, 10);
        }
        return 0;
    }

    private boolean valid(Item earth, Player p) {
        if (p == null
                || earth == null
                || !p.isOnline()
                || earth.getTicksLived() > 300
                || earth.getLocation().getBlock().getType().isSolid()
                || earth.isOnGround() || earth.getVelocity().lengthSquared() < 0.0001) return false;
        Warzone zone1 = Warzone.getZoneByLocation(earth.getLocation());
        Warzone zone2 = Warzone.getZoneByPlayerName(p.getName());
        if (zone1 == null || !zone1.equals(zone2) || zone1.isReinitializing()) return false;
        return true;
    }
}
