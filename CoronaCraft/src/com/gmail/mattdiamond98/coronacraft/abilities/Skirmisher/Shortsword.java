package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

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
    public void onPlayerAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (Team.getTeamByPlayerName(e.getDamager().getName()) == null ||
                    Team.getTeamByPlayerName(e.getEntity().getName()) == null) return;
            if (Team.getTeamByPlayerName(e.getEntity().getName()).getPlayers().contains(e.getDamager())) return;
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD && notInSpawn(p)) {
                getStyle(p).execute(p, e);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }
}
