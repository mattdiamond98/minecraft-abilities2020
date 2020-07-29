package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.event.CoronaCraftTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.PlayerInteraction;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;


import java.util.Set;

public class UltimateListener implements Listener {

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        Player victim = e.getVictim();
        Player killer = PlayerInteraction.getMostRecentHarm(victim);
        Set<Player> assists = PlayerInteraction.getRecentHarm(victim, System.currentTimeMillis() - 10_000);
        if (killer != null) {
            assists.remove(killer);
            assists.removeAll(Team.getTeamByPlayerName(victim.getName()).getPlayers());
            killer.sendMessage(ChatColor.GREEN + "+1 Kill");
            UltimateTracker.incrementProgress(killer, UltimateTracker.PLAYER_KILL_REWARD);
            for (Player assist : assists) {
                assist.sendMessage(ChatColor.GREEN + "+1 Assist");
                UltimateTracker.incrementProgress(assist, UltimateTracker.PLAYER_ASSIST_REWARD);
            }
        }
        PlayerInteraction.clearHarm(victim);
    }



    @EventHandler
    public void onPlayerJoin(WarPlayerJoinEvent e) {
        UltimateTracker.removeProgress(e.getPlayer());
    }

    @EventHandler
    public void onPlayerScore(WarPlayerScoreEvent e) {
        UltimateTracker.incrementProgress(e.getPlayer(), UltimateTracker.PLAYER_SCORE_REWARD);
    }

    @EventHandler
    public void onPluginTick(CoronaCraftTickEvent e) {
        for (Warzone zone : War.war.getActiveWarzones()) {
            for (Player player : zone.getPlayers()) {
                if (player.isOnline())
                    if (AbilityUtil.notInSpawn(player) && !zone.isFlagThief(player)) {
                        UltimateTracker.incrementGameTimeProgress(player);
                        float percent = (float) (UltimateTracker.getProgress(player) / 100.0);
                        if (percent < 0.0F) percent = 0.0F;
                        if (percent > 1.0F) percent = 1.0F;
                        player.setExp(percent);
                        if (percent > 0.9999F) {
                            if (!player.getInventory().contains(Material.NETHER_STAR) && !UltimateTracker.isUltimateActive(player) && player.getInventory().firstEmpty() != -1) {
                                UltimateAbility ultimate = UltimateTracker.getLoadout(player).getUltimate();
                                player.sendMessage(ChatColor.GREEN + "Ultimate ready: "
                                        + ChatColor.AQUA + ultimate.getName());
                                player.getInventory().addItem(AbilityUtil.formatUltimate(ultimate));
                            }
                        }
                    } else {
                        float percent = (float) (UltimateTracker.getProgress(player) / 100.0);
                        if (percent < 0.0F) percent = 0.0F;
                        if (percent > 1.0F) percent = 1.0F;
                        player.setExp(percent);
                }
            }
        }
    }

    @EventHandler
    public void onGameEnd(WarBattleWinEvent e) {
        for (Player player : e.getZone().getPlayers()) {
            UltimateTracker.setGameTimeProgress(player, 0);
        }
    }

    @EventHandler
    public void onPlayerLeave(WarPlayerLeaveEvent e) {
        Player player = Bukkit.getPlayer(e.getQuitter());
        UltimateTracker.removeProgress(player);
        if (player.isOnline()) {
            player.setExp(0.0F);
        }
    }

    @EventHandler
    public void onPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e) {
        Loadout loadout = Loadout.getLoadout(e.getPlayer());
        if (loadout == null) {
            Bukkit.getLogger().warning(e.getPlayer().getName() + " left spawn, could not parse loadout.");
        } else {
            Loadout previous = UltimateTracker.getLoadout(e.getPlayer());
            if (previous == null) {
                UltimateTracker.setLoadout(e.getPlayer(), loadout);
            }
            else if (previous != loadout) {
                UltimateTracker.removeProgress(e.getPlayer());
                UltimateTracker.setLoadout(e.getPlayer(), loadout);
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Progress toward your ultimate has been reset.");
            }
        }
    }

    // TODO: scoring gives reward

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Warzone zone = Warzone.getZoneByPlayerName(e.getPlayer().getName());
        if (zone != null) {
            if (e.hasItem() && e.getItem() != null && e.getItem().getType() == Material.NETHER_STAR
                    && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                    && AbilityUtil.notInSpawn(e.getPlayer())) {
                // Trigger ultimate
                Loadout loadout = Loadout.getLoadout(e.getPlayer());
                if (loadout == null) {
                    Bukkit.getLogger().warning("Could not parse loadout for " + e.getPlayer());
                } else {
                    loadout.getUltimate().activate(e.getPlayer());
                }
            }
        }
    }

    public static void sendUltimateMessage(Player player) {
        Warzone zone = Warzone.getZoneByPlayerName(player.getName());
        UltimateAbility ultimate = Loadout.getLoadout(player).getUltimate();
        if (zone == null || ultimate == null) return;
        for (Player msg : zone.getPlayers()) {
            msg.sendMessage(ChatColor.YELLOW + player.getName() + " has activated " + ChatColor.GREEN + ultimate.getName(player));
        }
        if (player.getInventory().getItemInOffHand().getType() == Material.NETHER_STAR)
            player.getInventory().setItemInOffHand(null);
        player.getInventory().remove(Material.NETHER_STAR);
        player.updateInventory();
    }

}
