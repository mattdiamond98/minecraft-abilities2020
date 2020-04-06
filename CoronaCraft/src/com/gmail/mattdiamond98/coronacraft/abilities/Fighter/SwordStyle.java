package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.hasItem() && e.getItem().getType() == item) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player p = (Player) e.getDamager();
            Team team = Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName());
            if (team == null) return;
            if (team.getPlayers().contains(p)) return;
            if (p.getInventory().getItemInMainHand().getType() == item && notInSpawn(p)) {
                CoronaCraft.getAbilities().get(item).getStyle(p).execute(p, (LivingEntity) e.getEntity(), e.getFinalDamage());
            }
        }
    }
}
