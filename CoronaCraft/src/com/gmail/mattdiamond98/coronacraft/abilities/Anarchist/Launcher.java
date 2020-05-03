package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Launcher extends Ability {

    public Launcher() {
        super("Explosive Launcher", Material.IRON_HOE);
    }

    @Override
    public void initialize() {
        styles.add(new Grenade());
        styles.add(new Incendiary());
        styles.add(new Chemical());
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == item && notInSpawn(p)) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (!CoronaCraft.isOnCooldown(p, item)) {
                    int tntCount = AbilityUtil.getTotalCount(p, Material.TNT);
                    if (tntCount > 0) {
                        Egg egg = (Egg) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.EGG);
                        egg.setShooter(p);
                        egg.setMetadata(CoronaCraft.getAbilities().get(item).getStyle(p).getName(), new FixedMetadataValue(CoronaCraft.instance, true));
                        egg.setVelocity(p.getEyeLocation().getDirection().normalize());
                        CoronaCraft.setCooldown(p, item, 13 * CoronaCraft.ABILITY_TICK_PER_SECOND);
                        AbilityUtil.setStackCount(p, Material.TNT, tntCount - 1);
                        if (!CoronaCraft.isOnCooldown(p, Material.TNT_MINECART)) {
                            CoronaCraft.setCooldown(p, Material.TNT_MINECART, TNTGenerator.BASE_COOL_DOWN);
                        }
                    } else {
                        AbilityUtil.notifyAbilityRequiresResources(p, Material.TNT, 1);
                    }
                } else {
                    AbilityUtil.notifyAbilityOnCooldown(p, this);
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

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Egg) {
            Egg egg = (Egg) e.getEntity();
            if (egg.getShooter() instanceof Player && Warzone.getZoneByLocation(egg.getLocation()) != null) {
                if (e.getEntity().getTicksLived() > 5) {
                    Player p = (Player) egg.getShooter();
                    getStyle(p).execute(p, egg);
                }
            }
        }
    }

}
