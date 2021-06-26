package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.Bukkit.getServer;

public class Longbow extends Ability implements Listener {

    private static final int MAX_COUNT = 64;

    public Longbow() {
        super("Longbow", Material.BOW);
    }

    @Override
    public void initialize() {
        styles.add(new PoisonArrow());
        styles.add(new SlowingArrow());
        styles.add(new GravityArrow());
        styles.add(new PrimordialArrow());
        styles.add(new SlayingArrow());



        for (AbilityStyle style : styles) {
            if (!(style instanceof Listener)) { continue; }
            getServer().getPluginManager().registerEvents((Listener) style, CoronaCraft.instance);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) /*&& notInSpawn(e.getPlayer())*/) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
            if (!Tutorial.RANGER_CHANGE_STYLES.isCompleted(e.getPlayer())) {
                Tutorial.RANGER_CHANGE_STYLES.setCompleted(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (e.getProjectile() instanceof Arrow && e.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) e.getProjectile();
            Player p = (Player) e.getEntity();

            if (UltimateTracker.isUltimateActive(p)) {
                ArrowStorm.arrowDuplicate(p, arrow, e.getForce());
            }
            else if (p.isSneaking() && AbilityUtil.notInSpawn(p)) {
                if (getStyle(p) instanceof ProjectileAbilityStyle) {
                    ((ProjectileAbilityStyle) getStyle(p)).onShoot(arrow);
                } else {
                    getStyle(p).execute(p, arrow);
                }
                if (!Tutorial.RANGER_USE_STYLES.isCompleted(p)) {
                    Tutorial.RANGER_USE_STYLES.setCompleted(p);
                }
            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        if (e.getItem() != item) return;
        if (e.getTicksRemaining() % CoronaCraft.ABILITY_TICK_PER_SECOND != 0) return;
        if (AbilityUtil.getTotalCount(e.getPlayer(), Material.BOW) == 0) return;
        if (AbilityUtil.getTotalCount(e.getPlayer(), item) == 0) return;
        if (AbilityUtil.notInSpawn(e.getPlayer())) {
            int count = AbilityUtil.getTotalCount(e.getPlayer(), Material.ARROW);
            if (count++ < MAX_COUNT) {
                e.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        AbilityKey key = new AbilityKey(e.getPlayer(), item);
        if (AbilityUtil.inventoryContains(e.getPlayer(), Material.BOW)) {
            if (!CoronaCraft.isOnCooldown(e.getPlayer(), item))
                CoronaCraft.getPlayerCoolDowns().put(key, 10_000);
        } else if (CoronaCraft.getPlayerCoolDowns().containsKey(key)){
            CoronaCraft.getPlayerCoolDowns().remove(key);
        }
    }

    static void potionEffectArrow(Player p, Arrow arrow, PotionEffect effect, int cost) {
        int arrowCount = AbilityUtil.getTotalCount(p, Material.ARROW);
        if (arrowCount >= cost - 1) {
            AbilityUtil.setStackCount(p, Material.ARROW, arrowCount - cost - 1);
            arrow.addCustomEffect(effect, false);
        } else {
            AbilityUtil.notifyAbilityRequiresResources(p, Material.ARROW, cost);
        }
    }

    static boolean makeSpecialArrow(Projectile projectile, ProjectileAbilityStyle ability,
                                    int arrowCost) {
        // Safety checks
        if (!(projectile.getShooter() instanceof Player)) return false;

        // Determine if player has enough arrows
        // Use arrowCount-1 because this runs after the special arrow is fired
        int arrowCount = AbilityUtil.getTotalCount((Player) projectile.getShooter(), Material.ARROW);
        if (arrowCount >= arrowCost - 1) {
            AbilityUtil.setStackCount((Player) projectile.getShooter(), Material.ARROW, arrowCount - (arrowCost - 1));
            projectile.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, ability));
            projectile.setMetadata(MetadataKey.FIRED_FROM, new FixedMetadataValue(CoronaCraft.instance, projectile.getLocation()));
        }
        else {
            AbilityUtil.notifyAbilityRequiresResources((Player) projectile.getShooter(), Material.ARROW, arrowCost);
            return false;
        }

        return true;
    }

//    static void applyDistanceBonus(ProjectileHitEvent event, Arrow arrow) {
//        // Check if a player was hit
//        Player player = (Player) event.getHitEntity();
//        if (player == null) { return; }
//
//        if (arrow.hasMetadata(MetadataKey.FIRED_FROM)) {
//            Location firedFrom = (Location) arrow.getMetadata(MetadataKey.FIRED_FROM).get(0).value();
//            if (arrow.getLocation().distanceSquared(firedFrom) >= 35*35) {
//                arrow.setVelocity(arrow.getVelocity().multiply(1.5));
//                ((Player) arrow.getShooter()).sendMessage(ChatColor.GREEN + "Long shot!");
//            }
//        }
//    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        // Check that a player was hit by an arrow
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            // Determine if the arrow qualifies for a distance bonus
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.hasMetadata(MetadataKey.FIRED_FROM)) {
                Location firedFrom = (Location) arrow.getMetadata(MetadataKey.FIRED_FROM).get(0).value();
                if (arrow.getLocation().distanceSquared(firedFrom) >= 35*35) {
                    event.setDamage(event.getDamage() * 1.5);
                    ((Player) arrow.getShooter()).sendMessage(ChatColor.GREEN + "Long shot!");
                }
            }
        }
    }
}