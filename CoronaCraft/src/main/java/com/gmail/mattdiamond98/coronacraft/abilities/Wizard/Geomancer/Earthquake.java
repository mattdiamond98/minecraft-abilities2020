package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.ChargeableCapstoneWizardStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class Earthquake extends ChargeableCapstoneWizardStyle {

    public static final int MANA_COST = 5;

    public static final int COOLDOWN_SECONDS = 60;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public static final int MAX_DISTANCE = 65;
    public static final int MAX_EDGE_DISTANCE = 5;
    public static final double SPREAD = 20;
    public static final int NODES_PER_ITER = 2;

    public static final int MAX_CHARGE = 12;
    public static final int MIN_CHARGE = 5;

    private static final Random rand = new Random();

    public Earthquake() {
        super("Earthquake", new String[]{
                        "Earthquake",
                        String.format("%d Mana per second", MANA_COST),
                        "",
                        "Crouch + hold right click with wand to cast"
                },
                new Ability("Earthquake", Material.MUSIC_DISC_11) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                }, MANA_COST, MAX_CHARGE, COOLDOWN_ABILITY_TICKS
        );
        ability.getStyles().add(this);
    }

    @Override
    public void onRelease(final Player p, int finalCharge) {
        Set<Vector> tree = new HashSet<>();
        tree.add(p.getLocation().toVector());
        for (int i = 1; i < finalCharge; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                Vector target = p.getTargetBlock(AbilityUtil.transparent, MAX_DISTANCE).getLocation().toVector();
                for (int j = 0; j < NODES_PER_ITER; j++) {
                    final Vector child = target.clone().add(new Vector(rand.nextDouble() * SPREAD - (SPREAD / 2), 0, rand.nextDouble() * SPREAD - (SPREAD / 2)));
                    Optional<Vector> parentOptional = tree.stream().min((v1, v2) -> (int) (v1.distanceSquared(child) - v2.distanceSquared(child)));
                    if (parentOptional.isPresent()) {
                        Vector parent = parentOptional.get();
                        if (parent.distanceSquared(child) > MAX_EDGE_DISTANCE * MAX_EDGE_DISTANCE) {
                            Vector adjChild = parent.clone().add(child.clone().subtract(parent).normalize().multiply(MAX_EDGE_DISTANCE));
                            tree.add(adjChild);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                drawEdge(parent, adjChild, p);
                            }, j);
                        } else {
                            tree.add(child);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                drawEdge(parent, child, p);
                            }, j);
                        }
                    }
                }
            }, i * 3);
        }
        CoronaCraft.setCooldown(p, getItem(), COOLDOWN_ABILITY_TICKS);
    }

    @Override
    public void onCharge(final Player p, int charge) {
        if (charge == MAX_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1F, 0.5F);
        } else {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, ((float) charge) / ((float) MAX_CHARGE));
        }
        if (charge == MIN_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 1F, 0.5F);
        }
        p.getWorld().playEffect(p.getEyeLocation(), Effect.STEP_SOUND, Material.STONE);
        CHARGE_DATA_MAP.put(p.getUniqueId(), charge);
        p.setLevel(p.getLevel() - MANA_COST);
    }

    private void drawEdge(Vector from, Vector to, Player player) {
        Vector increment = to.clone().subtract(from).setY(0);
        double lengthSquared = increment.lengthSquared();
        increment = increment.normalize();
        Location baseLoc = from.toLocation(player.getWorld());
        Team team = Team.getTeamByPlayerName(player.getName());
        baseLoc.getWorld().playSound(baseLoc, Sound.ENTITY_IRON_GOLEM_REPAIR, 0.5F, 0F);
        for (int i = 0; i * i < lengthSquared; i++, baseLoc = baseLoc.add(increment)) {
            for (Block block : Arrays.stream(new Block[] {
                    baseLoc.getBlock(),
                    baseLoc.getBlock().getRelative(BlockFace.NORTH),
                    baseLoc.getBlock().getRelative(BlockFace.EAST),
                    baseLoc.getBlock().getRelative(BlockFace.SOUTH),
                    baseLoc.getBlock().getRelative(BlockFace.WEST)
            }).map(Earthquake::getSolidBlock).sorted(Comparator.comparing(Block::getY).reversed()).limit(2).collect(Collectors.toList())) {
                baseLoc.setY(block.getY());
                if (AbilityUtil.validBlock(block) && block.getType().isSolid() && !block.equals(player.getLocation().getBlock().getRelative(0, -1, 0))) {
                    AbilityUtil.dropBlockWithVector(block, new Vector(0, 0.5, 0));
                }
                final Set<Player> nearby = block.getWorld().getNearbyEntities(block.getLocation(), 2, 2.5, 2).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .filter(member -> Team.getTeamByPlayerName(member.getName()) != null)
                        .filter(member -> !Team.getTeamByPlayerName(member.getName()).equals(team))
                        .filter(member -> !AbilityUtil.inSpawn(member))
                        .collect(Collectors.toSet());
                if (!nearby.isEmpty()) {
                    for (Player enemy : nearby) {
                        enemy.getWorld().playEffect(enemy.getEyeLocation(), Effect.STEP_SOUND, block.getBlockData().getMaterial());
                        enemy.damage(8, player);
                        enemy.setVelocity(new Vector(rand.nextDouble() * 0.5 - 0.25, 0.3, rand.nextDouble() * 0.5 - 0.25));
                        enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                        enemy.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 0));
                        enemy.getWorld().playSound(enemy.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.2F, 1);
                    }
                }
            }
        }
    }

    private static Block getSolidBlock(Block block) {
        return AbilityUtil.getSolidBlock(block, 4);
    }


}
