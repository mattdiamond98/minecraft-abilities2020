package com.gmail.mattdiamond98.coronacraft.tournament;

import com.tommytony.war.Warzone;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;


public class BattleSpectator implements Listener {
    public static HashMap<Player, Inventory> playerinventories= new HashMap<Player, Inventory>();
    public static boolean isInSpectator(Player p){
        return p.getWalkSpeed()==0.1999785F;

    }
    public static void setPlayerNotSpectator(Player p){
        if(isInSpectator(p)){
        p.getInventory().setContents(playerinventories.get(p).getContents());
        p.setInvulnerable(false);
        p.setWalkSpeed((float) 0.2);
        p.setFlying(false);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);

        }

    }
    public static void setPlayerSpectator(Player p){
        playerinventories.put(p, p.getInventory());
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        p.setInvulnerable(true);
        p.setWalkSpeed(0.1999785F);
        p.setFlying(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 5, false, false));
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0);
    }
    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        if(isInSpectator(e.getPlayer())){
            e.setCancelled(true);

        }

    }
    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent e){
        Player player=e.getPlayer();
        if(isInSpectator(player)){
            Warzone zone= Warzone.getZoneByLocation(e.getFrom());
            if(zone!=null){
                if(!zone.getVolume().contains(e.getTo())){
                    e.setCancelled(true);

                }

            }

        }

    }
}
