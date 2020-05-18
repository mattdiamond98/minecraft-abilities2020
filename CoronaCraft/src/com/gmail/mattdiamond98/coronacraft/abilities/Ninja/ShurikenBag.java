package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class ShurikenBag extends Ability {

    public static final int BASE_COOL_DOWN = 4; // 2 Seconds
    public static final int MAX_COUNT = 4;

    public ShurikenBag() {
        super("Shuriken Bag", Material.BLACK_DYE);
    }

    @Override
    public void initialize() {
        styles.add(new PoisonShuriken());
        styles.add(new VoidShuriken());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Material m = e.getItemDrop().getItemStack().getType();
        if ((m == item || m == Material.SNOWBALL)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            if (Team.getTeamByPlayerName(((Player) e.getEntity()).getName()) == null) return;
            Player p = (Player) ((Snowball) e.getDamager()).getShooter();
            Player target = (Player) e.getEntity();
            if (AbilityUtil.inSpawn(p) || AbilityUtil.inSpawn(target)) return;
            getStyle(p).execute(p, target);
            target.setNoDamageTicks(0);
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), getItem());
    }

    @EventHandler
    public void onCoolDownEnd(CoolDownEndEvent e) {
        ItemStack given = new ItemStack(Material.SNOWBALL, 1);
        ItemMeta meta = given.getItemMeta();
        meta.setDisplayName("§eShuriken");
        given.setItemMeta(meta);
        int cooldown = BASE_COOL_DOWN;
        if (UltimateTracker.isUltimateActive(e.getPlayer())) cooldown /= 2;
        AbilityUtil.regenerateItemPassive(e.getPlayer(), e.getItem(),
                item, given, MAX_COUNT, cooldown);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getType().equals(EntityType.SNOWBALL) && e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            int total_count = AbilityUtil.getTotalCount(player, Material.SNOWBALL);
            if (total_count <= MAX_COUNT) {
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                AbilityKey key = new AbilityKey(player, getItem());
                if (!coolDowns.containsKey(key)) {
                    int cooldown = BASE_COOL_DOWN;
                    if (UltimateTracker.isUltimateActive(player)) cooldown /= 2;
                    coolDowns.put(key, cooldown);
                }
            }
        }
    }
}
