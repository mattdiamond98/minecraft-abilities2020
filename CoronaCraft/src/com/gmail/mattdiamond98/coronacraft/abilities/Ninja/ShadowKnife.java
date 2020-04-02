package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class ShadowKnife extends Ability {

    public ShadowKnife() {
        super("Shadow Knife", Material.SHEARS);
    }

    public void initialize() {
        styles.add(new ToxicEnd());
        styles.add(new WarpStrike());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS && notInSpawn(p)) {
                if (p.isSneaking()) {
                    CoronaCraft.getAbilities().get(item).getStyle(p).execute(p, e.getEntity());
                } else {
                    LivingEntity target = (LivingEntity) e.getEntity();
                    target.damage(2);
                }
            }
        }
    }

}
