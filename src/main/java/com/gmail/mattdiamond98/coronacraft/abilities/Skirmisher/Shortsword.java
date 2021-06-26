package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Shortsword extends Ability {

    public Shortsword() {
        super("Shortsword", Material.IRON_SWORD);
    }

    @Override
    public void initialize() {
        styles.add(new HunterBlade());
        styles.add(new SoulDrain());
        styles.add(new SleightOfHand());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerShoot(ProjectileLaunchEvent e){
        if(e.getEntityType().equals(EntityType.SPECTRAL_ARROW)&&e.getEntity().getShooter() instanceof Player){
        e.getEntity().setMetadata(MetadataKey.SKIRM_FIRED, new FixedMetadataValue(CoronaCraft.instance, (Player) e.getEntity().getShooter()));


        }


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnProjectileLand(EntityDamageByEntityEvent e){
        if(e.getDamager().hasMetadata(MetadataKey.SKIRM_FIRED)&&e.getEntity() instanceof LivingEntity){
         //   e.setDamage(0);
           // ((LivingEntity) e.getEntity()).damage(9);

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            if (Team.getTeamByPlayerName(e.getDamager().getName()) == null
                    || Team.getTeamByPlayerName(e.getEntity().getName()) == null
                    || Team.getTeamByPlayerName(e.getEntity().getName()).getPlayers().contains(e.getDamager())
                    || AbilityUtil.inSpawn((Player) e.getEntity())) return;
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD && AbilityUtil.notInSpawn(p)) {
                getStyle(p).execute(p, e);
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
    public void onProjectileShoot(ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof SpectralArrow && e.getEntity().getShooter() instanceof Player) {
            CoronaCraft.setCooldown((Player) e.getEntity().getShooter(), Material.SPECTRAL_ARROW, 1);
        }
    }

    // Arrow regenerate listener
    @EventHandler
    public void onCoolDownTick(CoolDownEndEvent e) {
        if (e.getItem() != Material.SPECTRAL_ARROW) return;
        if (AbilityUtil.getTotalCount(e.getPlayer(), Material.CROSSBOW) == 0) return;
        if (AbilityUtil.notInSpawn(e.getPlayer())) {
            e.getPlayer().getInventory().addItem(new ItemStack(Material.SPECTRAL_ARROW, 1));
        }
    }
}
