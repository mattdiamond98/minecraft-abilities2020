package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class ShurikenBag extends Ability {

    public static final int BASE_COOL_DOWN = 4; // 2 Seconds
    public static final int MAX_COUNT = 4;

    public ShurikenBag() {
        super("Shuriken Bag", Material.BLACK_DYE);
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
                Map<CoolDownKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                coolDowns.put(new CoolDownKey(player, getItem()), BASE_COOL_DOWN);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getType().equals(EntityType.SNOWBALL) && e.getEntity().getShooter() instanceof Player) {
            Player player = (Player) e.getEntity().getShooter();
            int total_count = AbilityUtil.getTotalCount(player, Material.SNOWBALL);
            if (total_count <= MAX_COUNT) {
                Map<CoolDownKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                CoolDownKey key = new CoolDownKey(player, getItem());
                if (!coolDowns.containsKey(key)) {
                    coolDowns.put(key, BASE_COOL_DOWN);
                }
            }
        }
    }
}
