package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class StoneWard extends WizardStyle {

    public static final int MANA_COST = 25;
    public static final int COOLDOWN_SECONDS = 20;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final Map<UUID, WardData> WARD_DATA_MAP = new HashMap<>();

    private class WardData {
        Item[] item;
        int loop = 0;
        private WardData(Item... item) {
            this.item = item;
            for (Item i : this.item) {
                i.setPickupDelay(100_000);
                i.setGravity(false);
                i.setVelocity(new Vector(0, 0.1, 0));
            }
        }
    }

    public StoneWard() {
        super("Stone Ward", new String[]{
                        "Stone Ward",
                        String.format("%d Mana", MANA_COST),
                        "",
                        "Right click with wand to cast"
                },
                new Ability("Stone Ward", Material.FIREWORK_STAR) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                    @EventHandler(priority = EventPriority.HIGHEST)
                    public void onProjectileHit(EntityDamageByEntityEvent e) {
                        if (WARD_DATA_MAP.isEmpty()) {
                            return;
                        }
                        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && e.getDamager() instanceof Projectile) {
                            if (projectileDamage((Player) e.getEntity(), e.getDamager())) {
                                e.setCancelled(true);
                            }
                        }
                    }
                });
        ability.getStyles().add(this);
    }

    public static boolean projectileDamage(Player hit, Entity damager) {
        if (!WARD_DATA_MAP.containsKey(hit.getUniqueId())) return true;
        Vector diff = hit.getLocation().toVector().subtract(damager.getLocation().toVector()).setY(0);
        if (diff.getX() == 0 && diff.getZ() == 0) {
            return true;
        }
        double angle = Math.atan2(diff.getZ(), diff.getX());
        if (angle < 0) angle += Math.PI;
        WardData ward = WARD_DATA_MAP.get(hit.getUniqueId());
        double add = Math.toRadians(ward.loop);
        for (int i = 0; i < 3; i++) {
            Item stone = ward.item[i];
            if (stone != null) {
                double total = add + i * Math.toRadians(120);
                while (total > 2 * Math.PI) total -= Math.PI;
                double difference = Math.min(
                        Math.abs(total - angle),
                        angle > total
                                ? Math.abs((total + 2 * Math.PI) - angle)
                                : Math.abs(total - (angle + 2 * Math.PI))
                );

                if (difference < Math.PI / 2.0) {
                    stone.remove();
                    ward.item[i] = null;
                    stone.getWorld().playEffect(stone.getLocation(), Effect.STEP_SOUND, stone.getItemStack().getType());
                    stone.getWorld().playSound(stone.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.8F, 0.5F);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, true)) {
            p.setLevel(p.getLevel() - MANA_COST);
            CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
            if (WARD_DATA_MAP.containsKey(p.getUniqueId())) {
                for (Item item : WARD_DATA_MAP.get(p.getUniqueId()).item) {
                    if (item != null && !item.isDead()) item.remove();
                }
            }
            WARD_DATA_MAP.put(
                    p.getUniqueId(),
                    new WardData(
                            p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.STONE)),
                            p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.STONE)),
                            p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.STONE))
                    )
            );
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!p.isOnline()
                            || !WARD_DATA_MAP.containsKey(p.getUniqueId())
                            || Warzone.getZoneByPlayerName(p.getName()) == null
                            || Warzone.getZoneByPlayerName(p.getName()).isReinitializing()
                            || AbilityUtil.inSpawn(p)
                            || Arrays.stream(WARD_DATA_MAP.get(p.getUniqueId()).item).allMatch(Objects::isNull)) {
                        cancel();
                        WARD_DATA_MAP.remove(p.getUniqueId());
                    } else {
                        WardData ward = WARD_DATA_MAP.get(p.getUniqueId());
                        double angle = Math.toRadians(++ward.loop);
                        for (int i = 0; i < 3; i++) {
                            Item stone = ward.item[i];
                            if (stone != null) {
                                double total = angle + i * Math.toRadians(120);
                                double y = p.getEyeLocation().getY() + Math.sin(total) * 0.5;
                                double x = p.getEyeLocation().getX() + Math.cos(total) * 2;
                                double z = p.getEyeLocation().getZ() + Math.sin(total) * 2;
                                stone.teleport(new Location(stone.getWorld(), x, y, z));
                            }
                        }
                    }
                }
            }.runTaskTimer(CoronaCraft.instance, 1, 1);
        }
        return 0;
    }

}
