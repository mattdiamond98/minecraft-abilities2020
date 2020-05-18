package com.gmail.mattdiamond98.coronacraft.tutorial;

import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.FlagReturn;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.event.*;
import org.black_ixx.bossshop.events.BSPlayerPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class Tutorial implements Listener {

    public static final TutorialStep CHANGE_LOADOUTS = new TutorialStep(
            "coronacraft.tutorial.changeloadouts", "Tutorial", "Crouch to change loadouts");

    public static final TutorialStep FIGHTER_CHANGE_STYLES = new TutorialStep(
            "coronacraft.tutorial.fighter.changestyles", "Fighter", "Press 'Q' on sword to change styles");

    public static final TutorialStep RANGER_USE_STYLES = new TutorialStep(
            "coronacraft.tutorial.ranger.usestyles", "Ranger", "Crouch shoot for a special arrow");
    public static final TutorialStep RANGER_CHANGE_STYLES = new TutorialStep(
            "coronacraft.tutorial.ranger.changestyles", "Ranger", "Press 'Q' on bow to change styles");

    public static final TutorialStep TANK_USE_STYLES = new TutorialStep(
            "coronacraft.tutorial.tank.usestyles", "Tank", "Right click slime ball to Rally");
    public static final TutorialStep TANK_CHANGE_STYLES = new TutorialStep(
            "coronacraft.tutorial.tank.changestyles", "Tank", "Press 'Q' on slime ball");

    public static final TutorialStep SCORE_FLAG = new TutorialStep(
            "coronacraft.tutorial.score.flag", "Scoring", "Score when you capture the flag");
    public static final TutorialStep SCORE_LIFEPOOL = new TutorialStep(
            "coronacraft.tutorial.score.lifepool", "Scoring", "Score when enemy runs out of lives");

    public static final TutorialStep MONUMENTS = new TutorialStep(
            "coronacraft.tutorial.monuments", "Monuments", "Remove your helmet to get wool");

    public static final TutorialStep SHOP_INTRO = new TutorialStep(
            "coronacraft.tutorial.shop.intro", "Trade Bazaar", "Visit the Engineer's Workshop");
    public static final TutorialStep SHOP_PURCHASE = new TutorialStep(
            "coronacraft.tutorial.shop.purchase", "Trade Bazaar", "Unlock the Engineer class");

    public static void initTutorial() {
        FIGHTER_CHANGE_STYLES.linkTo(SCORE_FLAG);

        RANGER_USE_STYLES.linkTo(RANGER_CHANGE_STYLES);
        RANGER_CHANGE_STYLES.linkTo(SCORE_FLAG);

        TANK_USE_STYLES.linkTo(TANK_CHANGE_STYLES);
        TANK_CHANGE_STYLES.linkTo(SCORE_FLAG);

        SCORE_FLAG.linkTo(SCORE_LIFEPOOL);

        SHOP_INTRO.linkTo(SHOP_PURCHASE);
    }

    @EventHandler
    public void onPlayerJoin(WarPlayerJoinEvent e) {
        if (!CHANGE_LOADOUTS.isCompleted(e.getPlayer())) {
            CHANGE_LOADOUTS.sendTitleTo(e.getPlayer());
        }
        e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + e.getWarzone().getName());
        if (e.getWarzone().getTeams().stream().anyMatch(team -> team.getTeamFlag() != null)) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "Capture the Flag");
        } else {
            e.getPlayer().sendMessage(ChatColor.GREEN + "Team Deathmatch");
        }
        e.getPlayer().sendMessage(ChatColor.GREEN + "Lifepool " + ChatColor.YELLOW  + "" + e.getTeam().getTeamConfig().resolveInt(TeamConfig.LIFEPOOL));
        e.getPlayer().sendMessage(ChatColor.GREEN + "Score " + ChatColor.YELLOW + e.getTeam().getTeamConfig().resolveInt(TeamConfig.MAXSCORE) + ChatColor.GREEN + " points to win");

        if (e.getTeam().getTeamConfig().resolveFlagReturn() != FlagReturn.BOTH) {

            e.getPlayer().sendMessage(ChatColor.YELLOW + "Return flags to " + e.getTeam().getTeamConfig().resolveFlagReturn().toString().toLowerCase() + " only");
        }
        if (e.getWarzone().getWarzoneConfig().getBoolean(WarzoneConfig.UNBREAKABLE)) {
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Building and destruction is disabled");
        }
        if (e.getWarzone().getName().equals("CircusUltimatus")) {
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Special Rule: 1000% ultimate ability charge rate");
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        if (AbilityUtil.inSpawn(e.getPlayer()) && !CHANGE_LOADOUTS.isCompleted(e.getPlayer())) {
            CHANGE_LOADOUTS.setCompleted(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerThiefFlag(WarPlayerThiefEvent e) {
        if (!SCORE_FLAG.isCompleted(e.getThief())) {
            SCORE_FLAG.setCompleted(e.getThief(), true, false);
            e.getThief().sendMessage(ChatColor.GREEN + "Return the flag to score");
        }
    }

    @EventHandler
    public void onMatchWin(WarScoreCapEvent e) {
        for (Team team : e.getWinningTeams()) {
            for (Player player : team.getPlayers()) {
                if (SHOP_INTRO.getNextIncomplete(player) != null) {
                    player.sendTitle(ChatColor.GOLD + "Win Corona Coins", ChatColor.GREEN + "Spend at Trade Bazaar", 20, 80, 20);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getZ() > 216 && e.getTo().getZ() <= 216 && e.getTo().distanceSquared(e.getFrom()) < 9 && Math.abs(e.getTo().getX() - 143) < 5) {
            TutorialStep shopStep = SHOP_INTRO.getNextIncomplete(e.getPlayer());
            if (shopStep != null) {
                shopStep.sendTitleTo(e.getPlayer());
            } else {
                e.getPlayer().sendTitle(ChatColor.GOLD + "Trade Bazaar", ChatColor.GREEN + "Purchase classes and abilities", 20, 80, 20);
            }
        }
    }

    @EventHandler
    public void onPlayerPurchase(BSPlayerPurchaseEvent e) {
        if (e.getShop().getShopName().equalsIgnoreCase("engineer") && e.getShopItem().getName().equals("unlock")) {
            if (!SHOP_PURCHASE.isCompleted(e.getPlayer())) {
                SHOP_PURCHASE.setCompleted(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) {
            if (e.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                if (e.getPlayer().getLocation().distanceSquared(new Location(Bukkit.getWorld("world"),125.0, 16.0, 184.0)) < 16) {
                    if (!SHOP_INTRO.isCompleted(e.getPlayer())) {
                        if (!e.getPlayer().hasPermission("coronacraft.engineer.play")) {
                            SHOP_INTRO.setCompleted(e.getPlayer());
                        } else {
                            SHOP_INTRO.setCompleted(e.getPlayer(), true, false);
                            SHOP_PURCHASE.setCompleted(e.getPlayer());
                            e.getPlayer().sendMessage(ChatColor.YELLOW + "Spend Corona Coins to unlock more classes and abilities!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLifepoolEmpty(WarBattleWinEvent e) {
        for (Player player : e.getZone().getPlayers()) {
            if (SCORE_LIFEPOOL.isCompletedAnyPrev(player)) {
                player.sendTitle(ChatColor.GOLD + "Scoring", ChatColor.GREEN + "Reach the score cap to win", 20, 80, 20);
                SCORE_LIFEPOOL.setCompleted(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        Warzone zone = Warzone.getZoneByPlayerName(player.getName());
        if (zone == null) {
            return;
        }
        if (e.getSlotType() == InventoryType.SlotType.ARMOR
                && e.getSlot() == 39
                && zone.getWarzoneConfig().getBoolean(WarzoneConfig.BLOCKHEADS)
                && !MONUMENTS.isCompleted((Player) e.getWhoClicked())) {
            ((Player) e.getWhoClicked()).sendTitle(ChatColor.GOLD + "Monuments", ChatColor.GREEN + "Use wool to claim monuments", 20, 80, 20);
            MONUMENTS.setCompleted((Player) e.getWhoClicked());
            AbilityUtil.sendTip((Player) e.getWhoClicked(), "Claimed monuments heal you and your allies as you move near them.");
        }
    }

    @EventHandler
    public void onPlayerLeave(WarPlayerLeaveSpawnEvent e) {
        for (ItemStack item : e.getPlayer().getInventory()) { // TODO: add check for offhand
            if (item != null) {
                switch (item.getType()) {
                    case DIAMOND_SWORD:
                        TutorialStep fighter = FIGHTER_CHANGE_STYLES.getNextIncomplete(e.getPlayer());
                        if (fighter != null) fighter.sendTitleTo(e.getPlayer());
                        return;
                    case BOW:
                        TutorialStep ranger = RANGER_USE_STYLES.getNextIncomplete(e.getPlayer());
                        if (ranger != null) ranger.sendTitleTo(e.getPlayer());
                        return;
                    case SLIME_BALL:
                        TutorialStep tank = TANK_USE_STYLES.getNextIncomplete(e.getPlayer());
                        if (tank != null) tank.sendTitleTo(e.getPlayer());
                        return;
                }
            }
            TutorialStep other = SCORE_FLAG.getNextIncomplete(e.getPlayer());
            if (other != null) other.sendTitleTo(e.getPlayer());
            else {
                Warzone zone = Warzone.getZoneByPlayerName(e.getPlayer().getName());
                if (zone.getMonuments().size() > 0 && !MONUMENTS.isCompleted(e.getPlayer())) {
                    MONUMENTS.sendTitleTo(e.getPlayer());
                    return;
                }

//                Tip.sendRandomTip(e.getPlayer());
            }
        }
    }
}
