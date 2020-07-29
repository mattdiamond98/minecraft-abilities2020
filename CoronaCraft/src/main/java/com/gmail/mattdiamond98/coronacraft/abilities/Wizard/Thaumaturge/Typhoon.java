package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer.GeomancerSpellbook;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;

public class Typhoon extends UltimateAbility {

    public Typhoon() {
        super("Typhoon");
    }

    public static final int HEIGHT = 7;
    public static final int LENGTH = 40;
    public static final int WIDTH = 20;

    public static final int STUN_DURATION_SECONDS = 5;
    public static final int STUN_DURATION_TICKS = STUN_DURATION_SECONDS * 20;

    public static final int VELOCITY = 10;

    public static final int DURATION = LENGTH / VELOCITY;
    public static final int DURATION_TICKS = DURATION * 20;
    public static final int DURATION_COOLDOWN_TICKS = DURATION * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public static final double A = -0.25; // curvature in quadratic
    public static final double H = 20;

    private static final Predicate<Block> validBlock = AbilityUtil::validBlock;

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof ThaumaturgeSpellbook) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION_COOLDOWN_TICKS;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        UltimateTracker.removeProgress(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Vector orthogonal = direction.clone().rotateAroundY(90);
        Location playerLoc = player.getLocation();
        Location endLoc = playerLoc.clone().add(direction.clone().multiply(LENGTH));

        Vector[] corners = new Vector[]{
                endLoc.clone().add(orthogonal.clone().multiply(WIDTH)).toVector(),
                endLoc.clone().subtract(orthogonal.clone().multiply(WIDTH)).toVector(),
                playerLoc.clone().add(orthogonal.clone().multiply(WIDTH)).toVector(),
                playerLoc.clone().subtract(orthogonal.clone().multiply(WIDTH)).toVector()
        };

        Vector upperBounds = new Vector(Double.MIN_VALUE, playerLoc.getY(), Double.MIN_VALUE);
        Vector lowerBounds = new Vector(Double.MAX_VALUE, playerLoc.getY(), Double.MAX_VALUE);
        for (Vector corner : corners) {
            upperBounds.setX(Math.max(upperBounds.getX(), corner.getX()));
            lowerBounds.setX(Math.min(lowerBounds.getX(), corner.getX()));
            upperBounds.setZ(Math.max(upperBounds.getZ(), corner.getZ()));
            lowerBounds.setZ(Math.min(lowerBounds.getZ(), corner.getZ()));
        }

        List<Vector> centerPoints = new ArrayList<>(LENGTH / 2);
        List<List<Block>> rows = new ArrayList<>();
        for (int i = 0; i < LENGTH / 2; i++) {
            centerPoints.add(playerLoc.clone().add(direction.clone().multiply(i * 2)).toVector());
            rows.add(new LinkedList<>());
        }

        for (int i = lowerBounds.getBlockX(); i < upperBounds.getBlockX(); i++) {
            for (int j = lowerBounds.getBlockZ(); j < upperBounds.getBlockZ(); j++) {
                Vector P = new Vector(i, 0, j);
                if (
                        dotCenter(corners[1], corners[0], P)
                        && dotCenter(corners[1], corners[2], P)
                        && dotCenter(corners[0], corners[1], P)
                        && dotCenter(corners[1], corners[3], P)
                        && dotCenter(corners[3], corners[2], P)
                        && dotCenter(corners[3], corners[0], P)
                        && dotCenter(corners[2], corners[1], P)
                        && dotCenter(corners[2], corners[3], P)
                ) {
                    double minLength = Double.MAX_VALUE;

                    for (int k = 0; k < centerPoints.size(); k++) {
                        double lengthSquared = centerPoints.get(k).distanceSquared(P);
                        if (lengthSquared < minLength) {
                            minLength = lengthSquared;
                        } else {
                            rows.get(k - 1).add(player.getWorld().getBlockAt(i, playerLoc.getBlockY(), j));
                            break;
                        }
                    }
                }
            }
        }
        rows.forEach(list -> {
            list.removeIf(validBlock.negate());
            list.forEach(Typhoon::getSolidBlock);
            list.forEach(block -> block.getRelative(BlockFace.UP));
            list.forEach(Typhoon::getNonLiquidBlock);
        });

        new BukkitRunnable() {
            int tick = 0;
            double[] levels = new double[LENGTH / 2];
            Set<Player> damaged = new HashSet<>();
            @Override
            public void run() {
                if (tick == 0) {
                    for (int i = 0; i < levels.length; i++) {
                        levels[i] = playerLoc.getY();
                    }
                }
                if (++tick > DURATION_TICKS || !player.isOnline() || Team.getTeamByPlayerName(player.getName()) == null) {
                    cancel();
                    for (int i = 0; i < levels.length; i++) {
                        executeDiff(rows.get(i), levels[i], playerLoc.getY() - 20);
                    }
                } else {
                    for (int i = 0; i < levels.length; i++) {
                        double prev = levels[i];
                        levels[i] = Math.max(A * ((tick - i) - H) * ((tick - i) - H)
                                + HEIGHT + playerLoc.getY(), playerLoc.getY());
                        executeDiff(rows.get(i), prev, levels[i]);
                    }
                    if (tick % VELOCITY == 0) {
                        int k = 0;
                        double max = Double.MIN_VALUE;
                        for (int i = 0; i < levels.length; i++) {
                            if (levels[i] > max) {
                                k = i;
                                max = levels[i];
                            }
                        }
                        player.getWorld().playSound(centerPoints.get(k).toLocation(player.getWorld()), Sound.AMBIENT_UNDERWATER_ENTER, 0.7F, 2.0F);
                        damageNearby(centerPoints.get(k).toLocation(player.getWorld()), player, damaged);
                    }
                }
            }
        }.runTaskTimer(CoronaCraft.instance, 1, 2);

    }

    private void damageNearby(Location location, Player caster, Set<Player> damaged) {
        Team team = Team.getTeamByPlayerName(caster.getName());
        if (team == null) return;
        location.getWorld().getNearbyEntities(location, WIDTH / 2, HEIGHT, WIDTH / 2).stream()
                .filter(entity -> entity instanceof Player)
                .map(player -> (Player) player)
                .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                .filter(player -> !team.getPlayers().contains(player))
                .filter(player -> !damaged.contains(player))
                .forEach(player -> {
                    damaged.add(player);
                    AbilityUtil.applyStunEffect(player, STUN_DURATION_TICKS);
                    player.damage(12, caster);
                });
    }

    private static void executeDiff(List<Block> zone, double prev, double next) {
        int direction = prev > next ? -1 : 1;
        zone.stream()
                .map(block -> block.getRelative(0, (int) prev - block.getY(), 0))
                .forEach(block -> {
                    if (direction == 1) {
                        while (block.getY() < (int) next) {
                            if (!block.getType().isSolid() && block.getType() != Material.WATER && AbilityUtil.validBlock(block)) {
                                block.setMetadata(MetadataKey.NO_FLOW, new FixedMetadataValue(CoronaCraft.instance, true));
                                block.setType(Material.WATER);
                            }
                            block = block.getRelative(0, direction, 0);
                        }
                    } else {
                        while (block.getY() >= (int) next) {
                            if (block.hasMetadata(MetadataKey.NO_FLOW)) {
                                block.removeMetadata(MetadataKey.NO_FLOW, CoronaCraft.instance);
                                if (block.getType() == Material.WATER) {
                                    block.setType(Material.AIR);
                                }
                            }
                            block = block.getRelative(0, direction, 0);
                        }
                    }

                });
    }


    private static Block getSolidBlock(Block block) {
        return AbilityUtil.getSolidBlock(block, 4);
    }

    private static Block getNonLiquidBlock(Block block) {
        return block; // TODO: implement
    }

    private boolean dotCenter(Vector U, Vector V, Vector P) {
        return U.clone().subtract(P).dot(U.clone().subtract(V)) > 0;
    }
}
