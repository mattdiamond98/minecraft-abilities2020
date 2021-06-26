package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class GraspOfTheDamned extends RoseStyle implements Listener {
    private int blockRange = 5;

    private final int snakeSeconds = 6;
    private final int updatesPerSecond = 4;
    private LinkedList<Player> snakedPlayers = new LinkedList<>();

    public GraspOfTheDamned() {
        super("Grasp of the Damned", new String[] {
                "Send soulsand snakes after nearby",
                "enemies within a 5 block radius.",
                "Cooldown: 20 seconds"
        }, "coronacraft.reaper.graspofthedamned", 0);
        cooldownSeconds = 20;
        fuelCostpercent=20;
    }

    @Override
    public int execute(Player player, Object... data) {
        // Get player's team
        Team team = Team.getTeamByPlayerName(player.getName());
        if (team == null) return 0; // Do not allow spectators to use this ability

        // Do soulsand trap for all nearby enemies
        Set<Player> enemies = player.getLocation().getWorld().getNearbyEntities(player.getLocation(),
                blockRange, blockRange, blockRange).stream()
                .filter(entity -> entity instanceof Player)
                .map(enemy -> (Player) enemy)
                .filter(enemy -> Team.getTeamByPlayerName(enemy.getName()) != null)
                .filter(enemy -> !team.getPlayers().contains(enemy))
                .collect(Collectors.toSet());

        if (!enemies.isEmpty()) {
            for (Player enemy : enemies) {
                if (AbilityUtil.inSpawn(enemy)) { continue; }
                snakedPlayers.add(enemy);

                // Create runnable
                BukkitRunnable run = new BukkitRunnable() {
                    private int count = 0;
                    private Player target = enemy;
                    @Override
                    public void run() {
                        // Handle stopping the runnable
                        if (!snakedPlayers.contains(target)) {
                            cancel();
                        }
                        if (count >= snakeSeconds * updatesPerSecond) {
                            snakedPlayers.remove(target);
                            cancel();
                        }
                        if (target == null || AbilityUtil.inSpawn(target) ||
                                !AbilityUtil.inWarzone(target)) {
                            snakedPlayers.remove(target);
                            cancel();
                        }

                        // Locate center of soulsand square
                        Vector location = target.getLocation().toVector();
                        location.setX(Math.round(location.getX()));
                        location.setY(Math.round(location.getY()) - 1);
                        location.setZ(Math.round(location.getZ()));

                        // Create soulsand square
                        spawnSoulSand(target, location.toLocation(target.getWorld()));
                        location.add(new Vector(-1,0,0));
                        spawnSoulSand(target, location.toLocation(target.getWorld()));
                        location.add(new Vector(1, 0, -1));
                        spawnSoulSand(target, location.toLocation(target.getWorld()));
                        location.add(new Vector(-1, 0, 0));
                        spawnSoulSand(target, location.toLocation(target.getWorld()));

                        // increment
                        count++;
                    }
                };
                run.runTaskTimer(CoronaCraft.instance, 0, 20 / updatesPerSecond);
            }
        }

        // Create particle effect
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(false)
                .withColor(Color.fromRGB(152, 116, 86)).withFade(Color.MAROON).build();
        for (Location loc : AbilityUtil.getCircle(player.getLocation(), blockRange, 10)) {
            new InstantFirework(fireworkEffect, loc);
        }
        return 0;
    }

    private void spawnSoulSand(Player target, Location location) {
        // Check if soulsand can be created
        World world = location.getWorld();
        if (world.getBlockAt(location).getType() != Material.AIR && world.getBlockAt(location).getType() !=
                Material.SOUL_SAND) {
            // Spawn soul sand
            Material oldBlockType = world.getBlockAt(location).getType();
            BlockData oldBlockData = world.getBlockAt(location).getBlockData();
            world.getBlockAt(location).setType(Material.SOUL_SAND);

            // Create runnable for detecting when soulsand should be removed
            BukkitRunnable run = new BukkitRunnable() {
                @Override
                public void run() {
                    // Handle stopping the runnable
                    if (AbilityUtil.inSpawn(target) || !AbilityUtil.inWarzone(target) ||
                            !snakedPlayers.contains(target) || target == null ||
                            target.getLocation().distanceSquared(location) > 6) {
                        world.getBlockAt(location).setType(oldBlockType);
                        world.getBlockAt(location).setBlockData(oldBlockData);
                        cancel();
                    }
//                    if (target == null || target.getLocation().distanceSquared(location) > 6) {
//                        world.getBlockAt(location).setType(oldBlockType);
//                        world.getBlockAt(location).setBlockData(oldBlockData);
//                        cancel();
//                    }
                }
            };
            run.runTaskTimer(CoronaCraft.instance, 20 / updatesPerSecond, 20 /
                    updatesPerSecond);
        }
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
        if (event.getPlayer().getFallDistance() >= 0.8 && snakedPlayers.contains(event.getPlayer())) {
            event.getPlayer().damage(2);
        }
    }
}
