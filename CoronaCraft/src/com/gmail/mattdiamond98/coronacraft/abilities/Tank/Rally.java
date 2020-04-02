package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;


public class Rally extends Ability {

    public Rally() {
        super("Rally", Material.SLIME_BALL);
    }

    @Override
    public void initialize() {
        styles.add(new TankUp());
        styles.add(new Accelerate());
        styles.add(new Lethargy());
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
                            CoronaCraft.getAbilities().get(item).getStyle(e.getPlayer()).execute(e.getPlayer()));
                } else {
                    AbilityUtil.notifyAbilityOnCooldown(e.getPlayer(), this);
                }
            } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }
}
