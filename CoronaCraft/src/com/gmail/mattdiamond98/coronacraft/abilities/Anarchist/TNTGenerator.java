package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class TNTGenerator extends Ability {

    public static final int BASE_COOL_DOWN = 14; // 7 Seconds
    public static final int MAX_COUNT = 5;

    public TNTGenerator() {
        super("TNT Generator", Material.TNT_MINECART);
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), getItem());
    }

    @EventHandler
    public void onCoolDownEnd(CoolDownEndEvent e) {
        AbilityUtil.regenerateItemPassive(e.getPlayer(), e.getItem(),
                item, new ItemStack(Material.TNT, 1), MAX_COUNT, BASE_COOL_DOWN);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType().equals(Material.TNT)) {
            Player player = e.getPlayer();
            int total_count = AbilityUtil.getTotalCount(player, Material.TNT);
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
