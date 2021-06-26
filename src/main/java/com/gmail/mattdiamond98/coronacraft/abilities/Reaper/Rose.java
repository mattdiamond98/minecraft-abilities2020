package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class Rose extends Ability {

    public Rose() {
        super("Wither Rose", Material.WITHER_ROSE);
    }

    @Override
    public void initialize() {
        styles.add(new GraveOmen());
        styles.add(new GraspOfTheDamned());

        for (AbilityStyle style : styles) {
            if (!(style instanceof Listener)) { continue; }
            getServer().getPluginManager().registerEvents((Listener) style, CoronaCraft.instance);
        }
    }

    /**
     * Handles switching rose abilities.  Empties scythe fuel whenever an ability is switched.
     * @param e The PlayerDropItemEvent event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        // Make sure the correct item was dropped
        if ((e.getItemDrop().getItemStack().getType() == item)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    /**
     * Handles triggering rose abilities.
     * @param event The PlayerInteractEvent event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Check that it was a right click
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(
                Action.RIGHT_CLICK_BLOCK)) {
            // Check that player has the correct item
            if (event.hasItem() && event.getItem().getType() == item) {
                // Check that the player is not currently on cooldown
                if (!CoronaCraft.isOnCooldown(player, item)) {

                    RoseStyle style = (RoseStyle) getStyle(player);
                    if(!Scythe.consumeDurability(event.getPlayer(), style)){
                        event.setCancelled(true);
                        return;
                    }
                    CoronaCraft.setCooldown(player, item, style.cooldownSeconds *
                            CoronaCraft.ABILITY_TICK_PER_SECOND);


                    style.execute(player, event);
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }
}
