package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.structure.Bomb;
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
import org.bukkit.metadata.FixedMetadataValue;

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
        final Warzone zone=Warzone.getZoneByLocation(p);
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                    && e.getClickedBlock().getType().equals(Material.TNT) && ((Predicate<Block>) block -> {
                for (Bomb b : zone.getBombs()) {
                    if (b.getLocation().distance(block.getLocation()) <= 4) {
                        return false;
                    }
                }
                return true;
            }).test(e.getClickedBlock())){
                e.setCancelled(true);
                e.getClickedBlock().setType(Material.AIR);
                TNTPrimed tnt = (TNTPrimed) e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation().add(0, 1, 0), TNTPrimed.class);
                tnt.setFuseTicks(20);
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                Block block = p.getTargetBlock(transparent, 30);
                if (block.getType() == Material.TNT&& ((Predicate<Block>) blocky -> {
                    for (Bomb b : zone.getBombs()) {
                        if (b.getLocation().distance(blocky.getLocation()) <= 4) {
                            return false;
                        }
                    }
                    return true;
                }).test(block)) {
                    block.setType(Material.AIR);
                    Entity tnt = block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                    ((TNTPrimed)tnt).setFuseTicks(20);
                    tnt.setMetadata(MetadataKey.PLAYER, new FixedMetadataValue(CoronaCraft.instance, p));
                }
            }
        }
    }
}
