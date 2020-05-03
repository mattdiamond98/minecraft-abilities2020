package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class SwordStyle extends Ability {

    public SwordStyle() {
        super("Sword Style", Material.DIAMOND_SWORD);
    }

    @Override
    public void initialize() {
        styles.add(new InfernalBlade());
        styles.add(new ConfusionBlade());
        styles.add(new SwiftBlade());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getDamager();
            Team team = Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName());
            if (team == null) return;
            if (team.getPlayers().contains(p)) return;
            if (p.getInventory().getItemInMainHand().getType() == item && notInSpawn(p) && notInSpawn((Player) e.getEntity())) {
                getStyle(p).execute(p, (LivingEntity) e.getEntity(), e.getFinalDamage());
            }
        }
    }
}
