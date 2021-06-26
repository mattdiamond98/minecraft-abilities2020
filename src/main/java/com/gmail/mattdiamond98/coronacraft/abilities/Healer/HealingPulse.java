package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.Accelerate;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.Lethargy;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.Regenerate;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.TankUp;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class HealingPulse extends Ability {
    public HealingPulse() {
        super("Healing Magic", Material.YELLOW_DYE);
    }



    @Override
    public void initialize() {
        styles.add(new IdyllicMend());
        styles.add(new CelestialCrusade());
        styles.add(new FairyFountain());

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
    @EventHandler
    public void OnProjectileLand(ProjectileHitEvent e){
        if(e.getEntity() instanceof ThrownPotion &&e.getEntity().hasMetadata(MetadataKey.REMOVES_DEBUFFS)){
            Entity et= e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.ARMOR_STAND);
            et.setInvulnerable(true);
            ((ArmorStand)et).setVisible(false);
            for(Entity ee:et.getNearbyEntities(5, 5, 5)){
                if(ee instanceof AreaEffectCloud){
                    ((AreaEffectCloud) ee).setRadius(3);
                    ((AreaEffectCloud) ee).setDuration(200);
                    ((AreaEffectCloud) ee).setDurationOnUse(0);
                    ((AreaEffectCloud) ee).setRadiusOnUse(0);
                }

            }
            BukkitRunnable br=new BukkitRunnable() {
                @Override
                public void run() {
                    for(Entity e:et.getNearbyEntities(3, 3, 3)){

                        if(e instanceof LivingEntity){
                            if(e instanceof Player && Loadout.getLoadout((Player)e).equals(Loadout.REAPER)){
                                continue;
                            }

                            for(int i=0; i<((LivingEntity)e).getActivePotionEffects().size(); i++){

                                if(AbilityUtil.debuffs.contains(((LivingEntity)e).getActivePotionEffects().toArray(new PotionEffect[((LivingEntity)e).getActivePotionEffects().size()])[i].getType())){

                                    ((LivingEntity) e).removePotionEffect(((LivingEntity)e).getActivePotionEffects().toArray(new PotionEffect[((LivingEntity)e).getActivePotionEffects().size()])[i].getType());
                                }
                            }
                        }
                    }
                }
            };
            br.runTaskTimer(CoronaCraft.instance, 0L, 1L);
            BukkitRunnable br2=new BukkitRunnable() {
                @Override
                public void run() {
                    br.cancel();
                    for(Entity e:et.getNearbyEntities(5, 5, 5)){
                        if(e instanceof AreaEffectCloud){
                            e.remove();
                        }

                    }
                   et.remove();

                }
            };
            br2.runTaskLater(CoronaCraft.instance, 200L);
        }
    }
}
