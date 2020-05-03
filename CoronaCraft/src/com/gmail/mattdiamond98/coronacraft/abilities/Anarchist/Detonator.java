package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Detonator extends Ability {

    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isOccluding).negate()).collect(Collectors.toSet());
    static {
        transparent.remove(Material.TNT);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            // If we add more types of detonators, can cycle
            e.setCancelled(true);
        }
    }

    public Detonator() {
        super("Detonator", Material.FLINT_AND_STEEL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && e.getClickedBlock().getType().equals(Material.TNT)){
                e.setCancelled(true);
                e.getClickedBlock().setType(Material.AIR);
                TNTPrimed tnt = (TNTPrimed) e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation().add(0, 1, 0), TNTPrimed.class);
                tnt.setFuseTicks(20);
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                Block block = p.getTargetBlock(transparent, 30);
                if (block.getType() == Material.TNT) {
                    block.setType(Material.AIR);
                    Entity tnt = block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                    ((TNTPrimed)tnt).setFuseTicks(20);
                }
            }
        }
    }
}
