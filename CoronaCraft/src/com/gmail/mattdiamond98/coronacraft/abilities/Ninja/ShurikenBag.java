package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
                if (Team.getTeamByPlayerName(((Player) e.getDamager()).getName()) == null ||
                        Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName()) == null) return;

                Player p = (Player) ((Snowball) e.getDamager()).getShooter();
                Player target = (Player) e.getEntity();

            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), getItem());
    }

    @EventHandler
    public void onCoolDownEnd(CoolDownEndEvent e) {
        Player player = e.getPlayer();
        if (e.getItem().equals(getItem()) && player.getInventory().contains(getItem())) {
            ItemStack given = new ItemStack(Material.SNOWBALL, 1);
            ItemMeta meta = given.getItemMeta();
            meta.setDisplayName("§eShuriken");
            given.setItemMeta(meta);
            int total_count = AbilityUtil.getTotalCount(player, given.getType());
            if (total_count++ < MAX_COUNT) {
                player.getInventory().addItem(given);
            }
            if (total_count < MAX_COUNT) {
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                coolDowns.put(new AbilityKey(player, getItem()), BASE_COOL_DOWN);
            }
        }
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
                    coolDowns.put(key, BASE_COOL_DOWN);
                }
            }
        }
    }
}
