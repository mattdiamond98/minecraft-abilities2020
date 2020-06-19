package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.PlayerInteraction;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class ShadowKnife extends Ability {

    public static final double DAMAGE = 2.0;

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
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player enemy = (Player) e.getEntity();
            Player p = (Player) e.getDamager();
            Team team1 = Team.getTeamByPlayerName(enemy.getName());
            Team team2 = Team.getTeamByPlayerName(p.getName());
            if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS
                    && team1 != null
                    && team2 != null
                    && !team1.equals(team2)
                    && notInSpawn(p)
                    && notInSpawn(enemy)) {
                if (p.isSneaking()) {
                    getStyle(p).execute(p, enemy);
                } else {
                    double newHealth = enemy.getHealth() - DAMAGE;
                    PlayerInteraction.playerHarm(enemy, p);
                    if (newHealth <= 0) {
                        enemy.damage(1000);
                        e.setCancelled(true);
                        enemy.setNoDamageTicks(0);
                    } else {
                        enemy.damage(1.0);
                        enemy.setHealth(newHealth);
                        enemy.setNoDamageTicks(0);
                    }
                }
            }
        }
    }

}
