package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Longbow extends Ability {

    private static final int MAX_COUNT = 64;

    public Longbow() {
        super("Longbow", Material.BOW);
    }

    @Override
    public void initialize() {
        styles.add(new SlowingArrow());
        styles.add(new PoisonArrow());
        styles.add(new GravityArrow());
        styles.add(new SlayingArrow());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBowShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) e.getEntity();
            Player p = (Player) arrow.getShooter();
            if (p.isSneaking() && AbilityUtil.notInSpawn(p)) {
                getStyle(p).execute(p, arrow);
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
        if (e.getPlayer().getInventory().contains(Material.BOW)) {
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
}