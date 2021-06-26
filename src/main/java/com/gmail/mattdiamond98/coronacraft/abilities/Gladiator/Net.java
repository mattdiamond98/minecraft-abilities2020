package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class Net extends Ability {

    public Net() {
        super("Net", Material.FISHING_ROD);
    }

    @Override
    public void initialize() {
        styles.add(new Entangle());
        styles.add(new Enfeeble());
        styles.add(new Pluck());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerFishEvent(final PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.REEL_IN
            && event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY
            && event.getState() != PlayerFishEvent.State.IN_GROUND) return;
        final Player player = event.getPlayer();
        if (player == null) return;
        Warzone zone = Warzone.getZoneByPlayerName(player.getName());
        if (zone == null) return;
        if (AbilityUtil.notInSpawn(player)) {
            if (CoronaCraft.isOnCooldown(player, item)) {
                AbilityUtil.notifyAbilityOnCooldown(player, this);
            } else {
                CoronaCraft.setCooldown(player, item, getStyle(player).execute(player, event.getHook()));
            }
        } else {
            event.getHook().remove();
            event.setCancelled(true);
        }
    }
}
