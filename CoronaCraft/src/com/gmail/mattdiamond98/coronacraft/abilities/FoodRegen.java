package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class FoodRegen implements Listener {

    public static final Material FOOD = Material.COOKED_BEEF;

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == FOOD) { // Need to change if we ever change the food
            if (!CoronaCraft.isOnCooldown(e.getPlayer(), FOOD)) {
                CoronaCraft.setCooldown(e.getPlayer(), FOOD, 5 * CoronaCraft.ABILITY_TICK_PER_SECOND);
            }
        }
    }

    @EventHandler
    public void onFoodTimerEnd(CoolDownEndEvent e) {
        if (e.getItem() == FOOD) {
            if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) != null && AbilityUtil.notInSpawn(e.getPlayer())) {
                int newCount = AbilityUtil.getTotalCount(e.getPlayer(), FOOD) + 1;
                if (newCount <= 8) {
                    e.getPlayer().getInventory().addItem(new ItemStack(FOOD, 1));
                }
                if (newCount < 8) {
                    CoronaCraft.setCooldown(e.getPlayer(), FOOD, 5 * CoronaCraft.ABILITY_TICK_PER_SECOND);
                }
            }
        }
    }

}
