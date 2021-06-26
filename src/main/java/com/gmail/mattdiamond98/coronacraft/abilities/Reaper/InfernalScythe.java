package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.PlayerInteraction;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import java.util.Set;
import java.util.stream.Collectors;

public class InfernalScythe extends HoeStyle {
    private static int fuelTimeSeconds = 2;
    private int ticksPerSecond = 20;
    private final int maxBlockRange = 6;
    private final double targetingTolerance = 1.5;

    private final int streamsPerSecond = 4;
    private final int streamTicks = ticksPerSecond / streamsPerSecond;
    private final int streamDurability = (Scythe.hoeMaxDurability - Scythe.hoeMinDurability) /
            (fuelTimeSeconds * streamsPerSecond);

    private final int cyclesPerSecond = 10;
    private final int cycleTicks = ticksPerSecond / cyclesPerSecond;
    private final double cycleDistance = 15 / cyclesPerSecond;

    public InfernalScythe() {
        super("Infernal Scythe", new String[] {
                "Spray nearby enemies with flammable particles",
                "that cause a short period of slowness.",
                "Cost: " + Math.round(1000f / fuelTimeSeconds) / 10f + "% / second"
        }, "coronacraft.reaper.infernalscythe", 0);
        cooldownTicks = streamTicks;
        fuelCost = streamDurability;
    }

    /**
     * Creates a stream of blindness particles
     * @param player The player who triggered this ability
     * @param data Unused
     * @return 0
     */
    @Override
    public int execute(Player player, Object... data) {
        // Get player's team
        Team team = Team.getTeamByPlayerName(player.getName());
        if (team == null) return 0; // Do not allow spectators to use this ability

        // Maths
        final Location firedFrom = player.getLocation().add(0, 0.5, 0);
        final Location currentLocation = player.getLocation().add(0, 1, 0);
        final BlockVector increment = (BlockVector) player.getLocation().getDirection()
                .toBlockVector().normalize().multiply(cycleDistance).multiply(1.6).multiply(0.75);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);
        // Create a runnable that acts as a stream
        BukkitRunnable run = new BukkitRunnable() {
            @Override
            public void run() {
                // Handle stopping the runnable
                if (currentLocation.distanceSquared(firedFrom) > maxBlockRange*maxBlockRange) {
                    cancel();
                }

                // Update the current stream position
                currentLocation.add(increment);
                currentLocation.getWorld().spawnParticle(Particle.REDSTONE, currentLocation, 1,
                        new Particle.DustOptions(Color.fromRGB(214, 0, 0), 3));

                // Give slowness to all nearby enemies and set them on fire
                Set<Player> enemies = currentLocation.getWorld().getNearbyEntities(currentLocation,
                        targetingTolerance, targetingTolerance, targetingTolerance).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(player -> (Player) player)
                        .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                        .filter(player -> !team.getPlayers().contains(player))
                        .filter(AbilityUtil::notInSpawn)
                        .collect(Collectors.toSet());

                if (!enemies.isEmpty()) {
                    for (Player enemy : enemies) {

                        if(Loadout.getLoadout(enemy).equals(Loadout.REAPER)){
                            continue;
                        }
                        int ft=ticksPerSecond+enemy.getFireTicks();
                        if(enemy.getFireTicks()<0){
                            ft=ticksPerSecond;
                        }else if(ft>100){
                            ft=100;
                        }
                        int sd=ticksPerSecond;
                        if(enemy.hasPotionEffect(PotionEffectType.SLOW)) sd+=(enemy.getPotionEffect(PotionEffectType.SLOW).getDuration());
                        if(sd>100){
                            sd=100;
                        }
                        enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                                sd, 0));
                       // Bukkit.broadcastMessage("Current fire ticks are " + ft);
                        enemy.setFireTicks(ft);
                        PlayerInteraction.playerHarm(enemy, player);



                    }
                }
            }
        };
        run.runTaskTimer(CoronaCraft.instance, 0, cycleTicks);

        return 0;
    }
}
