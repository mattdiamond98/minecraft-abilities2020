package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

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
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS && notInSpawn(p)) {
                if (p.isSneaking()) {
                    getStyle(p).execute(p, e.getEntity());
                } else {
                    LivingEntity target = (LivingEntity) e.getEntity();
                    target.damage(2);
                }
            }
        }
    }

}
