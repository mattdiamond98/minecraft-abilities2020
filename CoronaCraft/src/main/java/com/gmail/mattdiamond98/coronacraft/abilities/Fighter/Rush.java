package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.DurationAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.inWarzone;

public class Rush extends Ability {

    public static final Set<UUID> RUSHING_PLAYERS = new HashSet<>();

    public Rush() { super("Rush", Material.RABBIT_HIDE); }

    @Override
    public void initialize() {
        styles.add(new BullRush());
        styles.add(new TigerPounce());
    }

    @EventHandler
    public void onCooldownEnd(CoolDownEndEvent e) {
        if (e.getItem().equals(item)) {
            if (RUSHING_PLAYERS.contains(e.getPlayer().getUniqueId())) {
                RUSHING_PLAYERS.remove(e.getPlayer().getUniqueId());
                if (getStyle(e.getPlayer()) instanceof BullRush) {
                    CoronaCraft.setCooldown(e.getPlayer(), item, BullRush.COOLDOWN);
                    e.getPlayer().removePotionEffect(PotionEffectType.SPEED);
                }
                if (getStyle(e.getPlayer()) instanceof TigerPounce) {
                    CoronaCraft.setCooldown(e.getPlayer(), item, TigerPounce.COOLDOWN);
                    e.getPlayer().removePotionEffect(PotionEffectType.JUMP);
                }
            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        if (!RUSHING_PLAYERS.contains(e.getPlayer().getUniqueId())) {
            AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player entity = (Player) e.getEntity();
            if (AbilityUtil.notInSpawn(damager) && AbilityUtil.notInSpawn(entity)) {
                if (RUSHING_PLAYERS.contains(damager.getUniqueId()) && getStyle(damager) instanceof BullRush) {
                    CoronaCraft.setCooldown(damager, item, 0);
                    damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_PANDA_BITE, 0.8F, 0.2F);
                    Bukkit.getScheduler().runTaskLater(CoronaCraft.instance, () -> {
                        entity.setVelocity(new Vector(0, 0.9, 0));
                    }, 1);
                }
                if (RUSHING_PLAYERS.contains(damager.getUniqueId()) && getStyle(damager) instanceof TigerPounce) {
                    CoronaCraft.setCooldown(damager, item, 0);
                    if (damager.getFallDistance() > 0.0F
                            && !damager.isOnGround()
                            && !damager.hasPotionEffect(PotionEffectType.BLINDNESS)
                            && !damager.isSprinting()
                            && damager.getVelocity().getY() < 0) {
                        CoronaCraft.setCooldown(damager, item, 0);
                        damager.setFallDistance(0.0F);
                        damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 0.8F, 0.5F);
                        damager.getWorld().playEffect(damager.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                        e.setDamage(e.getDamage() + 1.0); // True damage
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem() && e.getItem().getType() == item) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityKey key = new AbilityKey(e.getPlayer(), item);
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                if (!coolDowns.containsKey(key)) {
                    coolDowns.put(key, getStyle(e.getPlayer()).execute(e.getPlayer()));
                } else {
                    AbilityUtil.notifyAbilityOnCooldown(e.getPlayer(), this);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && inWarzone(e.getPlayer())) {
            e.setCancelled(true);
            if (!RUSHING_PLAYERS.contains(e.getPlayer().getUniqueId())) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }
}
