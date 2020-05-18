package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.TrapAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.gmail.mattdiamond98.coronacraft.util.PlayerInteraction;
import com.gmail.mattdiamond98.coronacraft.util.PlayerTimerKey;
import com.gmail.mattdiamond98.coronacraft.util.PlayerTimerKey.PlayerTimerType;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.event.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerEventListener implements Listener {

    private java.util.Random random = new java.util.Random();

    public Set<Material> lockedItems() {
        if (CoronaCraft.getAbilities() == null) return new HashSet<>();
        Set<Material> base = new HashSet<>(CoronaCraft.getAbilities().keySet());
        base.add(Material.ARROW);
        base.add(Material.TNT);
        base.add(Material.OAK_PLANKS);
        base.add(Material.COBBLESTONE);
        base.add(Material.SNOWBALL);
        base.add(Material.COBWEB);
        return base;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) == null) {
            if (e.hasItem() && e.getItem().getType() == Material.NETHER_STAR
                    && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                List<Warzone> warzones = War.war.getActiveWarzones();
                if (warzones.size() == 0) {
                    warzones = War.war.getEnabledWarzones();
                    warzones.removeIf(zone -> {
                        if (zone.isReinitializing()) return true;
                        if (zone.isFull(e.getPlayer())) return true;
                        int totalPlayers = Bukkit.getServer().getOnlinePlayers().size();
                        if (totalPlayers < 4) { // Deathmatch only
                            return zone.getTeams().stream().anyMatch(team -> team.getTeamFlag() != null);
                        }
                        if (totalPlayers < 6) { // Small Deathmatch and CTF
                            return zone.getTotalCapacity() > 12;
                        }
                        if (zone.getTeams().stream().allMatch(team -> team.getTeamFlag() == null)) return true;
                        if (totalPlayers < 8) { // Small and medium CTF
                            return zone.getTotalCapacity() > 20;
                        }
                        if (totalPlayers < 16) { // Medium and large CTF
                            return zone.getTotalCapacity() <= 12;
                        }
                        return zone.getTotalCapacity() <= 20; // Large CTF
                    });
                }
                if (warzones.size() == 0) return;
                Warzone zone = warzones.get(random.nextInt(warzones.size()));
                if (zone != null) {
                    e.setCancelled(true);
                    zone.autoAssign(e.getPlayer());
                }
            }
        } else {
            if (e.getAction() == Action.PHYSICAL) {
                if (e.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                    Object style = AbilityUtil.getMetadata(e.getClickedBlock(), MetadataKey.ON_STEP);
                    Object player = AbilityUtil.getMetadata(e.getClickedBlock(), MetadataKey.PLAYER);
                    if (style instanceof AbilityStyle && player instanceof Player) {
                        if (!((Player) player).isOnline()
                                || !Warzone.getZoneByLocation(e.getClickedBlock().getLocation()).equals(Warzone.getZoneByPlayerName(((Player) player).getName()))) {
                            e.getClickedBlock().setType(Material.AIR);
                            e.getClickedBlock().removeMetadata(MetadataKey.PLAYER, CoronaCraft.instance);
                            e.getClickedBlock().removeMetadata(MetadataKey.ON_STEP, CoronaCraft.instance);
                            TrapAbilityStyle.getPlayerTraps().remove(((Player) player).getName());
                        }
                        if (!Team.getTeamByPlayerName(((Player) player).getName()).equals(Team.getTeamByPlayerName(e.getPlayer().getName()))) {
                            if (TrapAbilityStyle.getPlayerTraps().containsKey(((Player) player).getName())) {
                                CoronaCraft.setCooldown((Player) player, e.getClickedBlock().getType(), ((AbilityStyle) style).execute((Player) player, e));
                            }
                            e.getClickedBlock().setType(Material.AIR);
                            e.getClickedBlock().removeMetadata(MetadataKey.PLAYER, CoronaCraft.instance);
                            e.getClickedBlock().removeMetadata(MetadataKey.ON_STEP, CoronaCraft.instance);
                            TrapAbilityStyle.getPlayerTraps().remove(((Player) player).getName());
                        }
                    } else {
                        e.getClickedBlock().setType(Material.AIR);
                        e.getClickedBlock().removeMetadata(MetadataKey.PLAYER, CoronaCraft.instance);
                        e.getClickedBlock().removeMetadata(MetadataKey.ON_STEP, CoronaCraft.instance);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Player) {
                PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) e.getDamager());
            }
            if (e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player) {
                PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) ((Projectile) e.getDamager()).getShooter());
            }
            if (e.getDamager() instanceof TNTPrimed && e.getDamager().hasMetadata(MetadataKey.PLAYER)) {
                PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) e.getDamager().getMetadata(MetadataKey.PLAYER).get(0).value());
            }
        }
    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident && event.getEntity().getShooter() instanceof Player) {
            if (!event.getEntity().hasMetadata(MetadataKey.ON_HIT)) return;
            Object meta = event.getEntity().getMetadata(MetadataKey.ON_HIT).get(0).value();
            if (meta instanceof AbilityStyle) {
                ((AbilityStyle) meta).execute((Player) event.getEntity().getShooter(), event);
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(WarPlayerLeaveEvent event) {
        AbilityUtil.removeCooldowns(Bukkit.getPlayer(event.getQuitter()));
    }

    @EventHandler
    public void onWarPlayerThief(WarPlayerThiefEvent event) {
        for (PotionEffectType type : new PotionEffectType[]{
                PotionEffectType.SPEED,
                PotionEffectType.FAST_DIGGING,
                PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.JUMP,
                PotionEffectType.REGENERATION,
                PotionEffectType.DAMAGE_RESISTANCE,
                PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.WATER_BREATHING,
                PotionEffectType.NIGHT_VISION,
                PotionEffectType.HEALTH_BOOST,
                PotionEffectType.ABSORPTION,
                PotionEffectType.LUCK,
                PotionEffectType.SLOW_FALLING,
                PotionEffectType.HERO_OF_THE_VILLAGE
        }) {
            if (event.getThief().hasPotionEffect(type)) event.getThief().removePotionEffect(type);
        }
        if (UltimateTracker.isUltimateActive(event.getThief())) {
            UltimateTracker.removeProgress(event.getThief());
        }
    }

    @EventHandler
    public void onGameEnd(WarBattleWinEvent event) {
        for (Player player : event.getZone().getPlayers()) {
            AbilityUtil.removeCooldowns(player);
        }
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
            if (!AbilityUtil.inventoryContains(e.getPlayer(), Material.FISHING_ROD) || AbilityUtil.inventoryContains(e.getPlayer(), Material.TRIDENT)) {
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
        if (AbilityUtil.inSpawn(e.getPlayer())) {
            Set<Material> keySet = CoronaCraft.getAbilities().keySet();
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () ->
                    Arrays.stream(e.getPlayer().getInventory().getContents())
                            .filter(Objects::nonNull)
                            .filter(i -> keySet.contains(i.getType()))
                            .forEach(i -> AbilityUtil.formatItem(e.getPlayer(), i)), 2);
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
    public void onGameEnd(WarScoreCapEvent e) {
        List<Team> winners = e.getWinningTeams();
//        losers.removeAll(winners);
        int reward = winners.stream().map(team -> team.getPlayers().size()).min(Integer::compareTo).orElse(1);
        final int adjustedReward = 1 + ((reward > 5) ? (int) Math.round(Math.floor(5 + Math.log(reward - 5) / Math.log(2))) : reward);
        winners.stream().forEach(team -> payTeam(team, adjustedReward));
//        losers.stream().forEach(team -> payTeam(team, Math.max(adjustedReward / 3, 1)));
    }

    private void payTeam(Team team, int amount) {
//        if (team.getPlayers().stream().anyMatch(p -> p.hasPermission("coronacraft.coins.allyboost"))) {
////            amount = (int) Math.ceil(amount * 1.2);
//            team.getPlayers().stream().forEach(p -> p.sendMessage(ChatColor.LIGHT_PURPLE + "You received a bonus for having a VIP on your team!"));
//        }
        for (Player p : team.getPlayers()) {
            double adjustedAmount = amount * 1.0;
            if (p.hasPermission("coronacraft.coins.hugeboost")) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "You recevied a bonus for being a VIP!");
                adjustedAmount *= 2.5;
            } else if (p.hasPermission("coronacraft.coins.largeboost")) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "You recevied a bonus for being a VIP!");
                adjustedAmount *= 2.0;
            } else if (p.hasPermission("coronacraft.coins.smallboost")) {
                p.sendMessage(ChatColor.LIGHT_PURPLE + "You recevied a bonus for being a VIP!");
                adjustedAmount *= 1.5;
            }
            adjustedAmount = Math.ceil(adjustedAmount);
//            adjustedAmount *= 2;
            CoronaCraft.getEconomy().depositPlayer(p, (int) adjustedAmount);
            p.sendMessage(ChatColor.GREEN + "You received " + ((int) adjustedAmount) + " Corona Coins!");
        }
    }

    @EventHandler
    public void onFlagStolen(WarPlayerThiefEvent e) {
        if (e.getStolenObject() == WarPlayerThiefEvent.StolenObject.FLAG) {
            int timeLong = 90 * 20;
            int timeShort = 45 * 20;

            Player p = e.getThief();
            Warzone warzone = Warzone.getZoneByPlayerName(p.getName());

            Team players = Team.getTeamByPlayerName(p.getName());
            Team opponents = warzone.getVictimTeamForFlagThief(p);
            // schedule anti-hide task
            int taskIdLong = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                if (warzone.isFlagThief(p)) {
                    if (!p.hasPotionEffect(PotionEffectType.GLOWING)) {
                        p.sendMessage(ChatColor.YELLOW + "You have been revealed!");
                        for (Player other : warzone.getPlayers()) {
                            if (!other.equals(p))
                                other.sendMessage(ChatColor.YELLOW + players.getName() + " has been revealed!");
                        }
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10_000, 0, true));
                }
            }, timeLong);
            
            if (taskIdLong != -1)
                CoronaCraft.addPlayerTimer(p, PlayerTimerType.FLAG_IND, taskIdLong);

            if (warzone.isTeamFlagStolen(players)) {
                for (Player opponent : opponents.getPlayers()) {
                    if (warzone.isFlagThief(opponent)) {
                        // schedule omni-team anti-hide task
                        int taskIdShort = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                            if (warzone.isFlagThief(p) && warzone.isFlagThief(opponent)) {
                                if (!p.hasPotionEffect(PotionEffectType.GLOWING)) {
                                    p.sendMessage(ChatColor.YELLOW + "You have been revealed!");
                                    for (Player other : warzone.getPlayers()) {
                                        if (!other.equals(p))
                                            other.sendMessage(ChatColor.YELLOW + players.getName() + " has been revealed!");
                                    }
                                }
                                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100_000, 0, true));
                                if (!opponent.hasPotionEffect(PotionEffectType.GLOWING)) {
                                    opponent.sendMessage(ChatColor.YELLOW + "You have been revealed!");
                                    for (Player other : warzone.getPlayers()) {
                                        if (!other.equals(opponent))
                                            other.sendMessage(ChatColor.YELLOW + opponent.getName() + " has been revealed!");
                                    }
                                }
                                opponent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100_000, 0, true));
                            }
                        }, timeShort);

                        if (taskIdShort != -1) {
                            CoronaCraft.addPlayerTimer(p, PlayerTimerType.FLAG_BOTH, taskIdShort);
                            CoronaCraft.addPlayerTimer(opponent, PlayerTimerType.FLAG_BOTH, taskIdShort);
                        }
                    }
                }
            }
        }
    }

    private Runnable spawnResistanceRunnable(final Player p) {
        return new Runnable() {
            @Override
            public void run() {
                if (!p.isDead()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 2));
                }
                if (AbilityUtil.inSpawn(p)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, spawnResistanceRunnable(p), 20);
                }
            }
        };
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        // clear single life player timers
        final Player p = e.getVictim();
        Warzone warzone = Warzone.getZoneByPlayerName(p.getName());
        if (warzone == null) return;

        Team opponents = warzone.getTeams().get(0).getPlayers().contains(p) ? warzone.getTeams().get(1) : warzone.getTeams().get(0);
        // TODO: work for when there is more than 2 teams

        if (warzone.getTeamDefaultConfig().contains(TeamConfig.RESPAWNTIMER) && warzone.getTeamDefaultConfig().getInt(TeamConfig.RESPAWNTIMER) >= 3)
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, spawnResistanceRunnable(p), 20);

        if (warzone.isFlagThief(p)) {
            // cancel long capture timer
            PlayerTimerKey longPTK = new PlayerTimerKey(p, PlayerTimerType.FLAG_IND);
            int longTaskId = CoronaCraft.getTaskId(longPTK);
            
            Bukkit.getServer().getScheduler().cancelTask(longTaskId);
            CoronaCraft.removePlayerTimer(longPTK);

            // cancel short capture timer
            PlayerTimerKey shortPTKMe = new PlayerTimerKey(p, PlayerTimerType.FLAG_BOTH);
            int shortTaskId = CoronaCraft.getTaskId(shortPTKMe);

            Bukkit.getServer().getScheduler().cancelTask(shortTaskId);
            CoronaCraft.removePlayerTimer(shortPTKMe);

            for (Player opponent : opponents.getPlayers()) {
                if (warzone.isFlagThief(opponent)) {
                    CoronaCraft.removePlayerTimer(opponent, PlayerTimerType.FLAG_BOTH);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            // TODO: change to tutorial once implemented
            e.getPlayer().sendTitle(ChatColor.GOLD + "Corona Capture the Flag", ChatColor.GREEN + "Right click the Nether Star to play", 20, 100, 20);
        }
        if (!AbilityUtil.inventoryContains(e.getPlayer(), Material.NETHER_STAR)) {
            ItemStack autoJoin = new ItemStack(Material.NETHER_STAR, 1);
            ItemMeta meta = autoJoin.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Join Active Game");
            meta.setLore(Arrays.asList(ChatColor.AQUA + "Right click to join the active game."));
            autoJoin.setItemMeta(meta);
            e.getPlayer().getInventory().addItem(autoJoin);
        }
    }
}
