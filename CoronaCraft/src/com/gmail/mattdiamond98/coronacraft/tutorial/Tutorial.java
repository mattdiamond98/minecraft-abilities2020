package com.gmail.mattdiamond98.coronacraft.tutorial;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerJoinEvent;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Tutorial implements Listener {

    public static final String PERMISSION_CHANGE_LOADOUTS = "coronacraft.tutorial.toggleloadouts";

    public static final String PERMISSION_FIGHTER_CHANGE_STYLES = "coronacraft.tutorial.fighter.changestyles";

    public static final String PERMISSION_RANGER_SHOOT_STYLE = "coronacraft.tutorial.ranger.shootstyle";
    public static final String PERMISSION_RANGER_CHANGE_STYLES = "coronacraft.tutorial.ranger.changestyles";

    @EventHandler
    public void onPlayerJoin(WarPlayerJoinEvent e) {
        if (!e.getPlayer().hasPermission(PERMISSION_CHANGE_LOADOUTS)) {
            e.getPlayer().sendTitle("Tutorial", "Crouch to change loadouts.", 1, 6, 1);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        if (AbilityUtil.inSpawn(e.getPlayer()) && !e.getPlayer().hasPermission(PERMISSION_CHANGE_LOADOUTS)) {
            e.getPlayer().addAttachment(CoronaCraft.instance, PERMISSION_CHANGE_LOADOUTS, true);
        }
    }

//    // TODO: need to come up with a true tutorial class with objectives and a better way to track it. Need to be able to continue tutorial from wherever left off.
//
//    @EventHandler
//    public void onPlayerLeave(WarPlayerLeaveSpawnEvent e) {
//        if (!e.getPlayer().hasPermission(PERMISSION_FIGHTER_CHANGE_STYLES) && e.getPlayer().getInventory().contains(Material.DIAMOND_SWORD)) {
//
//        }
//        else if (!e.getPlayer().hasPermission(PERMISSION_RANGER_SHOOT_STYLE) && e.getPlayer().getInventory().contains(Material.BOW)) {
//
//        }
//        else if (!e.getPlayer().hasPermission("") && e.getPlayer().getInventory().contains(Material.SLIME_BALL)) {
//
//        }
//    }

//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent e) {
//        Location from = e.getFrom();
//        Location to = e.getTo();
//
//        if (Tutorial.FULL_VOLUME.contains(e.getTo())) {
//            if (!Tutorial.FULL_VOLUME.contains(e.getFrom())) {
//                e.getPlayer().sendTitle(ChatColor.GOLD + "Tutorial", ChatColor.GREEN + "Learn about Corona CTF", 20, 4 * 20, 20);
//            }
//        }
//    }

}
