package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Waraxe extends Ability {

    public Waraxe() {
        super("Waraxe", Material.DIAMOND_AXE);
    }

    @Override
    public void initialize() {
        styles.add(new LifeThief());
        styles.add(new BloodSeeker());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(WarPlayerDeathEvent e) {
        if (e.getKiller() != null && e.getKiller() instanceof Player) {
            if (((Player) e.getKiller()).getInventory().getItemInMainHand().getType() == item) {
                getStyle((Player) e.getKiller()).execute((Player) e.getKiller());
            }
        }
    }

}
