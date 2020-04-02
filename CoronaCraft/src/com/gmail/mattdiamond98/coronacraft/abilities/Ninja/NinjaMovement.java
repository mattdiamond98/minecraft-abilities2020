package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class NinjaMovement extends Ability {

    public NinjaMovement() {
        super("Ninja Movement", Material.STONE_HOE);
    }

    public void initialize() {
        styles.add(new Leap());
        styles.add(new Blink());
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == Material.STONE_HOE && notInSpawn(p)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                AbilityKey key = new AbilityKey(p, getItem());
                if (!coolDowns.containsKey(key)) {
                    int coolDown = CoronaCraft.getAbilities().get(item).getStyle(e.getPlayer()).execute(e.getPlayer());
                    if (coolDown > 0) coolDowns.put(key, coolDown);
                } else {
                    AbilityUtil.notifyAbilityOnCooldown(p, this);
                }
            } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }


}
