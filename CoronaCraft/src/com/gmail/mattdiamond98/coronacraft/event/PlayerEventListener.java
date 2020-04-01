package com.gmail.mattdiamond98.coronacraft.event;

import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

// TODO: Need to move most of this logic to seperate ability classes
public class PlayerEventListener implements Listener {


    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isOccluding).negate()).collect(Collectors.toSet());
    static {
        transparent.remove(Material.TNT);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName()).getPlayers().contains((Player)e.getDamager())) return;
            // Fighter sword has 25% chance to confuse enemy
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD && notInSpawn(p)) {
                // Skirmisher sword deals +50% damage to highlighted enemies
                if (((LivingEntity) e.getEntity()).getPotionEffect(PotionEffectType.GLOWING) != null) {
                    e.setDamage(e.getDamage() * 1.5);
                    e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                }
            }
            if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS && notInSpawn(p)) {
                LivingEntity target = (LivingEntity) e.getEntity();
                if (p.isSneaking()) {
                    // Assassinate, deal damage equal to poison stack
                    if (target.getPotionEffect(PotionEffectType.POISON) == null) return;
                    int duration = target.getPotionEffect(PotionEffectType.POISON).getDuration();
                    target.removePotionEffect(PotionEffectType.POISON);
                    p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.SLIME_BLOCK);
                    target.damage(duration / 20);
                } else {
                    target.damage(2);
                }
            }
        }
        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Player p = (Player) ((Snowball) e.getDamager()).getShooter();
            Player target = (Player) e.getEntity();
            if (Team.getTeamByPlayerName(target.getName()).getPlayers().contains(p)) return;
            target.damage(2, p);
            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.SLIME_BLOCK);
            if (target.getPotionEffect(PotionEffectType.POISON) == null) {
                new PotionEffect(PotionEffectType.POISON, 4 * 20, 0).apply(target);
            } else {
                int duration = target.getPotionEffect(PotionEffectType.POISON).getDuration();
                new PotionEffect(PotionEffectType.POISON, duration + 4 * 20, 0).apply(target);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == Material.FLINT_AND_STEEL && !e.hasBlock() && notInSpawn(p)) {
            Block block = p.getTargetBlock(transparent, 30);
            if (block.getType() == Material.TNT) {
                block.setType(Material.AIR);
                Entity tnt = block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                ((TNTPrimed)tnt).setFuseTicks(20);
            }
        }
        if (e.hasItem() && e.getItem().getType() == Material.RED_DYE && notInSpawn(p)) {
            p.damage(4.0);
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6 * 20, 1).apply(p);
            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerHitFishingRod(final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            final Player caught = (Player) event.getCaught();
            if (caught.getPotionEffect(PotionEffectType.SLOW) == null) {
                new PotionEffect(PotionEffectType.WEAKNESS, 6 * 20, 0).apply(caught);
                new PotionEffect(PotionEffectType.SLOW, 6 * 20, 3).apply(caught);
                caught.getLocation().getWorld().playEffect(caught.getLocation(), Effect.STEP_SOUND, Material.WHITE_WOOL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryInteract(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(((Player) e.getWhoClicked()).getName()) != null) {
                if (!((Player) e.getWhoClicked()).getGameMode().equals(GameMode.CREATIVE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(((Player) e.getWhoClicked()).getName()) != null) {
                if (!((Player) e.getWhoClicked()).getGameMode().equals(GameMode.CREATIVE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) != null) {
            e.setCancelled(true);
        }
    }

}
