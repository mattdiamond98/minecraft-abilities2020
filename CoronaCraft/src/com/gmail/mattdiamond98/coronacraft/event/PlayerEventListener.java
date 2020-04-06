package com.gmail.mattdiamond98.coronacraft.event;

import com.tommytony.war.Warzone;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryInteract(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(((Player) e.getWhoClicked()).getName()) != null) {
                if (!((Player) e.getWhoClicked()).getGameMode().equals(GameMode.CREATIVE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(((Player) e.getWhoClicked()).getName()) != null) {
                if (!((Player) e.getWhoClicked()).getGameMode().equals(GameMode.CREATIVE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow || e.getEntity() instanceof SpectralArrow)
            e.getEntity().remove();
    }

}
