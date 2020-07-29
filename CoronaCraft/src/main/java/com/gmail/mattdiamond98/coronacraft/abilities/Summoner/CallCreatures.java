package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityLiving;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class CallCreatures extends Ability {
    public CallCreatures() {
        super("Call creatures", Material.BONE);
    }
    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem() && e.getItem().getType() == item) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityKey key = new AbilityKey(e.getPlayer(), item);
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                if (!coolDowns.containsKey(key)) {
                    coolDowns.put(key,
                            5);

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
