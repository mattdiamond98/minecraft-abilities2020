package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Shortsword extends Ability {

    public Shortsword() {
        super("Shortsword", Material.IRON_SWORD);
    }

    @Override
    public void initialize() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (Team.getTeamByPlayerName(((Player)e.getDamager()).getName()) == null ||
                    Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName()) == null) return;
            if (Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName()).getPlayers().contains((Player)e.getDamager())) return;
            // SwordStyle sword has 25% chance to confuse enemy
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD && notInSpawn(p)) {
                // Skirmisher sword deals +50% damage to highlighted enemies
                if (((LivingEntity) e.getEntity()).getPotionEffect(PotionEffectType.GLOWING) != null) {
                    e.setDamage(e.getDamage() * 1.5);
                    e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
                }
            }
        }
    }
}
