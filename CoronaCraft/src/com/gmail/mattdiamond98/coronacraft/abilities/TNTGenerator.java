package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TNTGenerator extends Ability {

    public static final int BASE_COOL_DOWN = 8; // 4 Seconds
    public static final int MAX_COUNT = 3;

    public TNTGenerator() {
        super("TNT Generator", Material.TNT_MINECART);
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), getItem());
    }

    @EventHandler
    public void onCoolDownEnd(CoolDownEndEvent e) {
        Player player = e.getPlayer();
        if (e.getItem().equals(getItem()) && player.getInventory().contains(getItem())) {
            ItemStack item = new ItemStack(Material.TNT, 1);
            int total_count = AbilityUtil.getTotalCount(player, item.getType());
            if (total_count++ < MAX_COUNT) {
                player.getInventory().addItem(item);
            }
            if (total_count < MAX_COUNT) {
                Map<CoolDownKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                coolDowns.put(new CoolDownKey(player, getItem()), BASE_COOL_DOWN);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType().equals(Material.TNT)) {
            Player player = e.getPlayer();
            int total_count = AbilityUtil.getTotalCount(player, Material.TNT);
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
