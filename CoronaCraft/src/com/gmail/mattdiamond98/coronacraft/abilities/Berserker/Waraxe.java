package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem() && e.getItem().getType() == item) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(WarPlayerDeathEvent e) {
        if (e.getKiller() != null && e.getKiller() instanceof Player) {
            if (((Player) e.getKiller()).getInventory().getItemInMainHand().getType() == item) {
                CoronaCraft.getAbilities().get(item).getStyle(e.getPlayer()).execute(e.getPlayer());
            }
        }
    }

}
