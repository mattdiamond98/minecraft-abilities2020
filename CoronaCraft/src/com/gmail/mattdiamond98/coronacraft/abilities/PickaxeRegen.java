package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class PickaxeRegen implements Listener {

    public static final Material PICKAXE = Material.WOODEN_PICKAXE;
    public static final int DAMAGE = 54;

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        if (e.getItem().getType() == PICKAXE) { // Need to change if we ever change the food
            if (!CoronaCraft.isOnCooldown(e.getPlayer(), PICKAXE)) {
                CoronaCraft.setCooldown(e.getPlayer(), PICKAXE, 10 * CoronaCraft.ABILITY_TICK_PER_SECOND);
            }
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickaxeTimerEnd(CoolDownEndEvent e) {
        if (e.getItem() == PICKAXE) {
            if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) != null && AbilityUtil.notInSpawn(e.getPlayer())) {
                if (!e.getPlayer().getInventory().contains(Material.IRON_PICKAXE)
                    && !e.getPlayer().getInventory().contains(Material.STONE_PICKAXE)) {
                    if (!e.getPlayer().getInventory().contains(PICKAXE)) {
                        ItemStack newPick = new ItemStack(Material.WOODEN_PICKAXE, 1);
                        if (!(newPick.getItemMeta() instanceof Damageable)) return;
                        Damageable meta = (Damageable) newPick.getItemMeta();
                        meta.setDamage(DAMAGE);
                        newPick.setItemMeta((ItemMeta) meta);
                        e.getPlayer().getInventory().addItem(newPick);
                    } else {
                        if (e.getPlayer().getInventory().getItemInOffHand().getType() == Material.WOODEN_PICKAXE) {
                            ItemStack itemStack = e.getPlayer().getInventory().getItemInOffHand();
                            Damageable meta = (Damageable) itemStack.getItemMeta();
                            meta.setDamage(DAMAGE);
                            itemStack.setItemMeta((ItemMeta) meta);
                            return;
                        }
                        for (ItemStack itemStack : e.getPlayer().getInventory()) {
                            if (itemStack != null && itemStack.getType() == Material.WOODEN_PICKAXE) {
                                Damageable meta = (Damageable) itemStack.getItemMeta();
                                meta.setDamage(DAMAGE);
                                itemStack.setItemMeta((ItemMeta) meta);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
