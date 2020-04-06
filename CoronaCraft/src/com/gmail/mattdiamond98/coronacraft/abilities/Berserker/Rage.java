package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Rage extends Ability {

    public Rage() {
        super("Rage", Material.RED_DYE);
    }

    @Override
    public void initialize() {
        styles.add(new Brawn());
        styles.add(new Fleetfoot());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                CoronaCraft.getAbilities().get(item).getStyle(e.getPlayer()).execute(e.getPlayer());
            } else if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }

}
