package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.tutorial.Tutorial;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.inWarzone;
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
        if ((e.getItemDrop().getItemStack().getType() == item) && inWarzone(e.getPlayer())) {
            e.setCancelled(true);
            if (CoronaCraft.isOnCooldown(e.getPlayer(), item)) {
                AbilityUtil.notifyAbilityChangeOnCooldown(e.getPlayer());
            } else {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
                if (!Tutorial.FIGHTER_CHANGE_STYLES.isCompleted(e.getPlayer())) {
                    Tutorial.FIGHTER_CHANGE_STYLES.setCompleted(e.getPlayer());
                }
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getDamager();
            Team team = Team.getTeamByPlayerName((e.getEntity()).getName());
            if (team == null) return;
            if (team.getPlayers().contains(p)) return;
            if (p.getInventory().getItemInMainHand().getType() == item && notInSpawn(p) && notInSpawn((Player) e.getEntity())) {
                getStyle(p).execute(p, e.getEntity(), e.getFinalDamage());
                CoronaCraft.setCooldown(p, item, 10 * CoronaCraft.ABILITY_TICK_PER_SECOND);
            }
        }
    }
}
