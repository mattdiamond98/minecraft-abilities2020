package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class Call extends Ability {
    public Call() {
        super("Strategic Maneuver", Material.BONE);
    }

    @Override
    public void initialize() {
        styles.add(new CallCreatures());
        styles.add(new TargetPlayerCustom());
    }


    @EventHandler
    public void OnEntityInteract(PlayerInteractAtEntityEvent e){
        if(getStyle(e.getPlayer())!=null&&getStyle(e.getPlayer()).getName().contains("Follow")&&e.getRightClicked() instanceof Player){
            AbilityKey key = new AbilityKey(e.getPlayer(), item);
            Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
            if (!coolDowns.containsKey(key)) {
                coolDowns.put(key,
                        getStyle(e.getPlayer()).execute(e.getPlayer(), e.getRightClicked()));

            } else {
                AbilityUtil.notifyAbilityOnCooldown(e.getPlayer(), this);
            }
        }
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem() && e.getItem().getType() == item&&getStyle(e.getPlayer())!=null&&getStyle(e.getPlayer()).getName().contains("Call")) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityKey key = new AbilityKey(e.getPlayer(), item);
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                if (!coolDowns.containsKey(key)) {
                    coolDowns.put(key,
                            getStyle(e.getPlayer()).execute(e.getPlayer()));

                } else {
                    AbilityUtil.notifyAbilityOnCooldown(e.getPlayer(), this);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);

        }
    }
}
