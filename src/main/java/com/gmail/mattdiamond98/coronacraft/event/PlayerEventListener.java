package com.gmail.mattdiamond98.coronacraft.event;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.*;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer.PyromancerSpellbook;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Spellbook;
import com.gmail.mattdiamond98.coronacraft.cosmetics.CosmeticManager;
import com.gmail.mattdiamond98.coronacraft.data.PlayerData;
import com.gmail.mattdiamond98.coronacraft.util.*;
import com.gmail.mattdiamond98.coronacraft.util.PlayerTimerKey.PlayerTimerType;
import com.sk89q.worldedit.bukkit.fastutil.Hash;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.TeamConfig;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.event.*;
import com.tommytony.war.event.WarBattleWinEvent;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.event.WarPlayerLeaveEvent;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import com.tommytony.war.event.WarPlayerThiefEvent;
import com.tommytony.war.event.WarScoreCapEvent;
import de.gesundkrank.jskills.*;
import me.glaremasters.multieconomy.MultiEconomy;
import me.glaremasters.multieconomy.api.API;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import simplepets.brainsynder.api.event.pet.PetMoveEvent;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerEventListener implements Listener {

    private java.util.Random random = new java.util.Random();

    private static List<Material> unplaceableItems = Arrays.asList(new Material[] {
            Material.FIRE_CHARGE,
            Material.TNT_MINECART,
            Material.FIRE_CORAL,
            Material.CAMPFIRE,
    });

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
    @EventHandler
    public void OnPlayerClick(InventoryClickEvent e){
        if(e.getView().getTitle().toLowerCase().contains("advent")&&e.getCurrentItem()!=null&&e.getCurrentItem().getType()!=Material.RED_CONCRETE&&e.getWhoClicked() instanceof Player){
            Player p=(Player) e.getWhoClicked();
            String s=ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replaceAll("December ", "");
            String date= String.valueOf(LocalDate.now(ZoneId.of("America/New_York")).getDayOfMonth());
            Random r=new Random();
                if(s.contains("7")&&date.equals("7")){

                    int coins=r.nextInt(60)+10;
                    CoronaCraft.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), coins);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+p.getName()+" permission set coronacraft.adventcalendar.7 true");
                }
              else if(s.contains("8")&&date.equals("8")) {

                    API.setAmount(p.getUniqueId().toString(), "coronagold", Integer.parseInt(API.getAmount(p.getUniqueId().toString(), "coronagold")) + 1);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+p.getName()+" permission set coronacraft.adventcalendar.8 true");
                }
              else if(s.contains("9")&&date.equals("9")){
                  int coins=(r.nextInt(10)+1)*9;
                  CoronaCraft.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), coins);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+p.getName()+" permission set coronacraft.adventcalendar.8 true");
                }
            e.getView().close();
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) == null) {
            if (e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (e.getItem().getType() == Material.NETHER_STAR) {
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
                } else if (e.getItem().getType() == Material.PAPER) {
                    e.getPlayer().performCommand("essentials:info Referrals");
                } else if (e.getItem().getType() == Material.GOLD_INGOT) {
                    e.getPlayer().performCommand("essentials:info VoteRewards");
                } else if (e.getItem().getType() == Material.DIAMOND) {
                    e.getPlayer().performCommand("csbuy");
                } else if (e.getItem().getType() == Material.MUSIC_DISC_STAL) {
                    e.getPlayer().performCommand("essentials:info Discord");
                }else if(e.getItem().getType()==Material.EMERALD){
                    CosmeticManager.OpenCosmeticWindow(e.getPlayer());

                }            }
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
            else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.hasItem()&&unplaceableItems.contains(e.getItem().getType())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || e.getCause() == EntityDamageEvent.DamageCause.POISON
                || e.getCause() == EntityDamageEvent.DamageCause.WITHER)) {
            ((Player) e.getEntity()).setNoDamageTicks(0);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Player) {
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        && ((Player) e.getDamager()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                    e.setCancelled(true);
                } else {
                    PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) e.getDamager());
                }
            }
            else if (e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player) {
                PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) ((Projectile) e.getDamager()).getShooter());
                ((Player) e.getEntity()).setNoDamageTicks(0);
            }
            else if (e.getDamager() instanceof TNTPrimed && e.getDamager().hasMetadata(MetadataKey.PLAYER)) {
                PlayerInteraction.playerHarm((Player) e.getEntity(), (Player) e.getDamager().getMetadata(MetadataKey.PLAYER).get(0).value());
            }
            else if (e.getDamager() instanceof Firework) {
                e.setCancelled(true);
            }
        }
    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            if (!event.getEntity().hasMetadata(MetadataKey.ON_HIT)) return;
            Object meta = event.getEntity().getMetadata(MetadataKey.ON_HIT).get(0).value();
            if (meta instanceof AbilityStyle) {
                ((AbilityStyle) meta).execute((Player) event.getEntity().getShooter(), event);
            }
        }
    }

    @EventHandler
    public void onEntityBlockChange(EntityChangeBlockEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK) {
            if (e.getEntity().hasMetadata(MetadataKey.ON_HIT)) {
                Object meta = e.getEntity().getMetadata(MetadataKey.ON_HIT).get(0).value();
                if (meta instanceof EventExecutable) {
                    ((EventExecutable) meta).onEvent(e);
                }
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
    public void onPetMove(PetMoveEvent e) {
        if (Warzone.getZoneByLocation(e.getTargetLocation()) != null) {
            Player owner = e.getEntity().getOwner();
            owner.sendMessage(ChatColor.GREEN + "Your pet has been stored since you entered a warzone.");
            e.getEntity().getPet().removePet(true);
        }
    }
    @EventHandler
    public void OnPlayerWarhub(PlayerCommandPreprocessEvent e){
        if(e.getMessage().contains("warhub")){
            addLobbyItem(e.getPlayer(), 0, Material.NETHER_STAR, "Join Game", "Right Click to join an active game!");
            addLobbyItem(e.getPlayer(), 5, Material.PAPER, "Refer", "Refer players to support the", "server and earn rewards!");
            addLobbyItem(e.getPlayer(), 6, Material.GOLD_INGOT, "Vote", "Vote to support the server", "and earn rewards!");
            addLobbyItem(e.getPlayer(), 7, Material.DIAMOND, "Donate", "Donate to support the", "server and get rewards!");
            addLobbyItem(e.getPlayer(), 8, Material.MUSIC_DISC_STAL, "Discord", "Join our discord community!");
            addLobbyItem(e.getPlayer(), 4, Material.EMERALD, "Cosmetics", "Right Click to open the cosmetic menu!");
        }
    }



    @EventHandler
    public void onGameEnd(final WarScoreCapEvent e) {
        Warzone zone = e.getWarzone();
        List<Team> winners = e.getWinningTeams();
        List<Team> losers = new ArrayList<>(zone.getTeams());

        losers.removeAll(winners);

        if (winners.size() == 0 || losers.size() == 0) return;
        int reward = losers.stream().map(team -> team.getPlayers().size()).min(Integer::compareTo).orElse(1);
        final int adjustedReward = 1 + ((reward > 5) ? (int) Math.round(Math.floor(5 + Math.log(reward - 5) / Math.log(2))) : reward);
        winners.stream().forEach(team -> payTeam(team, adjustedReward));
        for(Team t:winners){
            for(Player p:t.getPlayers()){

                API.setAmount(p.getUniqueId().toString(), "pumpkins", (int) Integer.parseInt(API.getAmount(p.getUniqueId().toString(), "pumpkins"))+(int)Math.ceil(e.getWarzone().getPlayerCount()/1.5));
            }
        }
        for(Team t:losers){
            for(Player p:t.getPlayers()){

                API.setAmount(p.getUniqueId().toString(), "pumpkins", (int) Integer.parseInt(API.getAmount(p.getUniqueId().toString(), "pumpkins"))+(int)Math.ceil(e.getWarzone().getPlayerCount()/2));
            }
        }
        /**
         * Calculate score if the game is two teams of three or more players with both teams having flags
         * We also remove match of the day style games.
         */
        if (losers.size() == 1 && winners.size() == 1
                && !zone.getName().equalsIgnoreCase("CircusUltimatus")
                && !zone.getName().equalsIgnoreCase("Renegade")) {
            Team winningTeam = winners.get(0);
            Team losingTeam = losers.get(0);
            if (winningTeam.getTeamFlag() != null && losingTeam.getTeamFlag() != null
                    && winningTeam.getPlayers().size() >= 1 && losingTeam.getPlayers().size() >= 1) { // TODO: set to >= 3
                List<ITeam> ratings = new ArrayList<>(2);

                TeamMap winningRatings = new TeamMap();
                winningTeam.getPlayers().stream().map(Player::getUniqueId).forEach(uuid -> {
                    winningRatings.put((IPlayer) new RateablePlayer<UUID>(uuid), PlayerData.getRating(uuid));
                });
                ratings.add((ITeam) winningRatings);
                TeamMap losingRatings = new TeamMap();
                losingTeam.getPlayers().stream().map(Player::getUniqueId).forEach(uuid -> {
                    losingRatings.put((IPlayer) new RateablePlayer<UUID>(uuid), PlayerData.getRating(uuid));
                });
                ratings.add((ITeam) losingRatings);
                Map<IPlayer, Rating> newRatings = TrueSkillCalculator.calculateNewRatings(GameInfo.getDefaultGameInfo(), ratings, 1, 2);
                newRatings.entrySet().forEach(entry -> {
                    PlayerData.updateRating(((RateablePlayer<UUID>) entry.getKey()).getId(), entry.getValue());
                });
                Bukkit.getLogger().info("Updated rankings successfully");
            }
        }
    }

    @EventHandler
    public void onPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e) {
        Warzone zone = Warzone.getZoneByPlayerName(e.getPlayer().getName());
        if (
                zone != null
                        && !zone.isReinitializing()
                        && zone.getName().equals("Arcanum")
                        && Loadout.getLoadout(e.getPlayer()) == Loadout.WIZARD
        ) {
            Ability ability = CoronaCraft.getAbility(Material.BLAZE_ROD);
            AbilityStyle prev = ability.getStyle(e.getPlayer());
            AbilityStyle next = prev;
            for (AbilityStyle style : ability.getStyles()) {
                if (style instanceof Spellbook) {
                    Spellbook spellbook = (Spellbook) style;
                    if (AbilityUtil.inventoryContains(e.getPlayer(), spellbook.getLightStyle().getItem())) {
                        next = spellbook;
                    }
                }
            }
            AbilityUtil.sendAbilityStyle(e.getPlayer(), next);
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Right click each spell icon to learn about it.");
            e.getPlayer().sendMessage(ChatColor.YELLOW + "All abilities are cast with your wand.");
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Remember this class is in beta, so expect some bugs!");
            if (!prev.equals(next)) {
                CoronaCraft.getPlayerAbilities().put(new AbilityKey(e.getPlayer(), Material.BLAZE_ROD), ability.getStylePosition(next));
            }
        }

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
            if(p.hasPermission("coronacraft.coins.teenyboost")){
                p.sendMessage(ChatColor.LIGHT_PURPLE+"You received a bonus from the advent calendar!");
                adjustedAmount *= 1.1;
            }
            adjustedAmount = Math.ceil(adjustedAmount);
            CoronaCraft.getEconomy().depositPlayer(p, (int) adjustedAmount);


            p.sendMessage(ChatColor.GREEN + "You received " + ((int) adjustedAmount) + " Corona Coins!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerData.asyncPersist(e.getPlayer().getUniqueId());
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
        if (!e.getPlayer().hasPlayedBefore() || !e.getPlayer().hasPermission("coronacraft.tutorial.complete")) {
         //   e.getPlayer().sendTitle(ChatColor.GOLD + "Corona Capture the Flag", ChatColor.GREEN + "Right click the Nether Star to play", 20, 100, 20);
            e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), 2564, 13, 996));
        }
        if(e.getPlayer().hasPermission("coronacraft.tutorial.complete")){
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.getPlayer().getInventory().clear();
        addLobbyItem(e.getPlayer(), 0, Material.NETHER_STAR, "Join Game", "Right Click to join an active game!");
        addLobbyItem(e.getPlayer(), 5, Material.PAPER, "Refer", "Refer players to support the", "server and earn rewards!");
        addLobbyItem(e.getPlayer(), 6, Material.GOLD_INGOT, "Vote", "Vote to support the server", "and earn rewards!");
        addLobbyItem(e.getPlayer(), 7, Material.DIAMOND, "Donate", "Donate to support the", "server and get rewards!");
        addLobbyItem(e.getPlayer(), 8, Material.MUSIC_DISC_STAL, "Discord", "Join our discord community!");
        addLobbyItem(e.getPlayer(), 4, Material.EMERALD, "Cosmetics", "Right Click to open the cosmetic menu!");
        PlayerData.preLoadPlayer(e.getPlayer().getUniqueId());}
    }

    void addLobbyItem(Player p, int slot, Material item, String title, String... description) {
        if (!AbilityUtil.inventoryContains(p, item)) {
            ItemStack stack = new ItemStack(item, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + title + ChatColor.GRAY + " (Right Click)");
            meta.setLore(Arrays.stream(description).map(line -> ChatColor.GRAY + line).collect(Collectors.toList()));
            meta.getPersistentDataContainer().set(NamespacedKey.minecraft("hideflags"), PersistentDataType.BYTE, (byte) 63);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            stack.setItemMeta(meta);
            p.getInventory().setItem(slot, stack);
        }
    }
}