package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Quiver extends Ability {

    private static final int MAX_COUNT = 64;

    public Quiver() {
        super("Quiver", Material.LEATHER);
    }

    @Override
    public void initialize() {
        styles.add(new SlowingArrow());
        styles.add(new PoisonArrow());
        styles.add(new GravityArrow());
        styles.add(new SlayingArrow());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && (e.getItem().getType() == item || e.getItem().getType() == Material.BOW) && notInSpawn(p)) {
            if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }

    @EventHandler
    public void onBowShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof Arrow && e.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) e.getEntity();
            Player p = (Player) arrow.getShooter();
            if (p.isSneaking() && AbilityUtil.notInSpawn(p)) {
                CoronaCraft.getAbilities().get(item).getStyle(p).execute(p, arrow);
            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
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
            p.sendMessage(ChatColor.RED + "That requires " + cost + " arrows!");
        }
    }
}