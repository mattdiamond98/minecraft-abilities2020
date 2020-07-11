package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoronaCraftTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.stream.Collectors;

public class FlameRune extends WizardStyle {

    public static final int MANA_COST = 25;

    public static final int CHANNEL_AMOUNT = 10;

    public static final int COOLDOWN_SECONDS = 10;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final Map<UUID, RuneData> RUNE_DATA_MAP = new HashMap<>();

    private class RuneData {
        Block block;
        BlockFace face;
        int level;
        private RuneData(Block block, BlockFace face, int level) {
            this.block = block;
            this.face = face;
            this.level = level;
        }
    }

    public FlameRune() {
        super("Flame Rune", new String[]{
                "Flame Rune",
                String.format("%d Mana", MANA_COST),
                String.format("%ds Cooldown", COOLDOWN_SECONDS),
                "",
                "Hold right click with wand on a block to cast"
        }, new Ability("Flame Rune", Material.FIRE_CORAL) {
            @EventHandler
            public void onCooldownTick(CoolDownTickEvent e) {
                AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
            }
            @EventHandler
            public void onTick(CoronaCraftTickEvent e) {
                for (UUID id : new HashSet<>(RUNE_DATA_MAP.keySet())) {
                    RuneData rune = RUNE_DATA_MAP.get(id);
                    if (rune.level >= CHANNEL_AMOUNT) {
                        Player p = Bukkit.getPlayer(id);
                        if (p == null || !p.isOnline()) {
                            RUNE_DATA_MAP.remove(id);
                            return;
                        }
                        Warzone playerZone = Warzone.getZoneByPlayerName(p.getName());
                        Warzone runeZone = Warzone.getZoneByLocation(rune.block.getLocation());
                        if (runeZone == null || !runeZone.equals(playerZone) || runeZone.isReinitializing()) {
                            RUNE_DATA_MAP.remove(id);
                            return;
                        }
                        if (!rune.block.getType().isSolid()) {
                            RUNE_DATA_MAP.remove(id);
                            p.sendMessage(ChatColor.RED + "Your rune has been destroyed.");
                            return;
                        }
                        rune.block.getWorld().playEffect(getFace(rune.block, rune.face), Effect.MOBSPAWNER_FLAMES, 10);
                        rune.block.getWorld().spawnParticle(Particle.FLAME, getFace(rune.block, rune.face), 1, 0.1, 0.1, 0.1, 0.0);
                        rune.block.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, getFace(rune.block, rune.face), 10, 0.2, 0.2, 0.2, 1.0);
                        Team team = Team.getTeamByPlayerName(p.getName());
                        final Location target = rune.block.getRelative(rune.face, 2).getLocation();
                        final Set<Player> nearby = target.getWorld().getNearbyEntities(target, 2.5, 2.5, 2.5).stream()
                                .filter(entity -> entity instanceof Player)
                                .map(player -> (Player) player)
                                .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                                .filter(player -> !team.getPlayers().contains(player))
                                .collect(Collectors.toSet());
                        if (!nearby.isEmpty()) {
                            RUNE_DATA_MAP.remove(id);
                            p.sendMessage(ChatColor.GREEN + "Your rune has activated.");
                            rune.block.getWorld().playSound(rune.block.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5F, 0.5F);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                final Set<Player> enemies = target.getWorld().getNearbyEntities(target, 3, 3, 3).stream()
                                        .filter(entity -> entity instanceof Player)
                                        .map(player -> (Player) player)
                                        .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                                        .filter(player -> !team.getPlayers().contains(player))
                                        .collect(Collectors.toSet());
                                rune.block.getWorld().playSound(rune.block.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.7F, 0.7F);
                                target.getWorld().createExplosion(target, 2.0F, true, false);
                                target.getWorld().playEffect(target, Effect.MOBSPAWNER_FLAMES, 10);
                                for (Player enemy : enemies) {
                                    enemy.setFireTicks(100);
                                }
                            }, 10);
                        }
                    }
                }
            }
        });
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, true)) {
            PlayerInteractEvent e = (PlayerInteractEvent) args[0];
            if (RUNE_DATA_MAP.containsKey(p.getUniqueId()) && RUNE_DATA_MAP.get(p.getUniqueId()).level >= CHANNEL_AMOUNT) {
                p.sendMessage(ChatColor.RED + "You already have a rune set.");
                return 0;
            }
            if (!e.hasBlock()) return 0;
            Block target = e.getClickedBlock();
            RuneData rune = RUNE_DATA_MAP.get(p.getUniqueId());
            if (rune == null) {
                rune = new RuneData(target, e.getBlockFace(), 0);
                RUNE_DATA_MAP.put(p.getUniqueId(), rune);
                p.sendTitle("", ChatColor.YELLOW + "Hold right click to channel", 0, 40, 0);
            }
            if (!target.equals(rune.block)) {
                rune.level = 0;
                rune.block = target;
            }
            else rune.level++;
            p.getWorld().playEffect(getFace(target, e.getBlockFace()), Effect.MOBSPAWNER_FLAMES, 10);
            p.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, getFace(target, e.getBlockFace()), 10, 0.2, 0.2, 0.2, 1.0);
            if (rune.level >= CHANNEL_AMOUNT) {
                p.sendMessage(ChatColor.GREEN + "Your rune has been set.");
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5F, 0.5F);
                p.setLevel(p.getLevel() - MANA_COST);
                CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
            }
        }
        return 0;
    }

    private static Location getFace(Block block, BlockFace face) {
        return center(block.getRelative(face)).add(center(block)).multiply(0.5);
    }

    private static Location center(Block block) {
        return block.getLocation().add(0.5, 0.5, 0.5);
    }

}
