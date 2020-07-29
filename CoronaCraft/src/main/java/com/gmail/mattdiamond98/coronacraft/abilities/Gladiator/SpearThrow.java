package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class SpearThrow extends Ability {

    public SpearThrow() {
        super("Spear Throw", Material.TRIDENT);
    }

    @Override
    public void initialize() {
        styles.add(new Impale());
        styles.add(new LifeSwap());
        styles.add(new Fatalis());
        styles.add(new Lacerate());
    }

    @EventHandler
    public void onProjectileShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident && event.getEntity().getShooter() instanceof Player
                && AbilityUtil.notInSpawn((Player) event.getEntity().getShooter())) {
            Player shooter = (Player) event.getEntity().getShooter();
            AbilityStyle style = getStyle(shooter);
            if (style instanceof ProjectileAbilityStyle) {
                if (UltimateTracker.isUltimateActive(shooter)) {
                    CoronaCraft.setCooldown(shooter, item, StormGodsWrath.style.onShoot(event.getEntity()));
                } else {
                    CoronaCraft.setCooldown(shooter, item, ((ProjectileAbilityStyle) style).onShoot(event.getEntity()));
                }
            }
            ((Trident) event.getEntity()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCooldownEnd(CoolDownEndEvent event) {
        if (this.equals(event.getAbility())) {
            Player player = event.getPlayer();
            if (player != null && player.isOnline() && Warzone.getZoneByPlayerName(player.getName()) != null) {
                if (AbilityUtil.inventoryContains(event.getPlayer(), Material.FISHING_ROD) && !AbilityUtil.inventoryContains(player, item)) {
                    ItemStack trident = new ItemStack(item, 1);
                    if (UltimateTracker.isUltimateActive(player)) {
                        trident = StormGodsWrath.formatTrident(trident);
                    } else {
                        trident = AbilityUtil.formatItem(player, trident);
                    }
                    player.getInventory().addItem(trident);
                }
            }
        }
    }

}
