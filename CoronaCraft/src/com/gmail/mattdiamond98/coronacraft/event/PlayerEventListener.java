package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarBattleWinEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerEventListener implements Listener {

    public Set<Material> lockedItems() {
        if (CoronaCraft.getAbilities() == null) return new HashSet<>();
        Set<Material> base = new HashSet<>(CoronaCraft.getAbilities().keySet());
        base.add(Material.ARROW);
        base.add(Material.TNT);
        base.add(Material.OAK_PLANKS);
        base.add(Material.COBBLESTONE);
        base.add(Material.SNOWBALL);
        base.add(Material.COBWEB);
        base.add(Material.CROSSBOW);
        return base;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryInteract(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(e.getWhoClicked().getName()) != null) {
                if (!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
                    if (lockedItems().contains(e.getCursor().getType())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player && Team.getTeamByPlayerName(e.getEntity().getName()) != null) {
            e.setCancelled(true);
            e.getItem().remove();
        }
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent e) {
        if (e.getArrow() instanceof Trident) {
            if (!e.getPlayer().getInventory().contains(Material.FISHING_ROD) || e.getPlayer().getInventory().contains(Material.TRIDENT)) {
                e.setCancelled(true);
                e.getArrow().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item == null || item.getType() == null) return;
        if (CoronaCraft.getAbilities().keySet().contains(item.getType())) {
            Ability ability = CoronaCraft.getAbility(item.getType());
            if (ability.getStyles().size() == 0) return;
            AbilityStyle style = ability.getStyle(e.getPlayer());
            if (!item.getItemMeta().getDisplayName().equals(AbilityUtil.formatStyleName(style))) {
                AbilityUtil.formatItem(e.getPlayer(), item);
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        // TODO: this method should be "inSpawn" but will have to change other stuff
        if (!AbilityUtil.notInSpawn(e.getPlayer())) {
            Set<Material> keySet = CoronaCraft.getAbilities().keySet();
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () ->
                    Arrays.stream(e.getPlayer().getInventory().getContents())
                            .filter(Objects::nonNull)
                            .filter(i -> keySet.contains(i.getType()))
                            .forEach(i -> AbilityUtil.formatItem(e.getPlayer(), i)), 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void craftItem(PrepareItemCraftEvent e) { {
        if (e.getInventory().getViewers().stream().anyMatch(v -> Warzone.getZoneByPlayerName(v.getName()) != null)) {
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }}

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (Warzone.getZoneByPlayerName(e.getWhoClicked().getName()) != null) {
                if (!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
                    if (lockedItems().contains(e.getOldCursor().getType())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow || e.getEntity() instanceof SpectralArrow)
            ((AbstractArrow) e.getEntity()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    @EventHandler
    public void onGameEnd(WarBattleWinEvent e) {
        List<Team> winners = e.getWinningTeams();
        List<Team> losers = new ArrayList<>(e.getZone().getTeams());
        losers.removeAll(winners);
        int reward = losers.stream().map(team -> team.getPlayers().size()).min(Integer::compareTo).orElse(1);
        final int adjustedReward = (reward > 5) ? (int) Math.round(Math.floor(5 + Math.log(reward - 5) / Math.log(2))) : reward;
        winners.stream().forEach(team -> payTeam(team, adjustedReward));
        losers.stream().forEach(team -> payTeam(team, Math.max(adjustedReward / 3, 1)));
    }

    private void payTeam(Team team, int amount) {
        for (Player p : team.getPlayers()) {
            CoronaCraft.getEconomy().depositPlayer(p, amount);
        }
    }

}
