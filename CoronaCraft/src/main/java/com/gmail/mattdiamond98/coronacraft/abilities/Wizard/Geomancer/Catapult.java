package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Catapult extends WizardStyle {

    public static final int MANA_COST = 25;

    public static final int COOLDOWN_SECONDS = 10;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final Set<Vector> PIERCE_SHAPE = new HashSet<>();

    private static final Random rand = new Random();

    public Catapult() {
        super("Catapult", new String[]{
                        "Catapult",
                        String.format("%d Mana", MANA_COST),
                        String.format("%ds Cooldown", COOLDOWN_SECONDS),
                        "",
                        "Crouch + left click with wand to cast"
                },
                new Ability("Catapult", Material.CLAY_BALL) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                });
        ability.getStyles().add(this);
        if (PIERCE_SHAPE.isEmpty()) {
            for (BlockFace face1 : BlockFace.values()) {
                for (BlockFace face2 : BlockFace.values()) {
                    if (face1 != face2) PIERCE_SHAPE.add(face1.getDirection().add(face2.getDirection()));
                }
            }
        }
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, false)) {
            Location target = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(4));
            Material material = Material.STONE;
            Block block = target.getBlock();
            for (int i = 0; i < 10 && !block.getType().isSolid(); i++) {
                block = block.getRelative(0, -1, 0);
            }
            if (block.getType().isSolid()) material = block.getType();
            block.setType(Material.AIR);
            final Vector velocity = p.getLocation().getDirection().normalize().multiply(2).add(new Vector(0, 0.3, 0));
            spawnCatapultProjectile(p, block, material, velocity);
            p.setLevel(p.getLevel() - MANA_COST);
            CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
        }
        return 0;
    }

    public static FallingBlock spawnCatapultProjectile(Player p, Block block, Material material, Vector velocity) {
        FallingBlock projectile = block.getWorld().spawnFallingBlock(block.getLocation().add(0, 1.2, 0), Bukkit.createBlockData(material));
        projectile.setVelocity(new Vector(0, 0.3, 0));
        projectile.setDropItem(false);
        Team team = Team.getTeamByPlayerName(p.getName());
        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
            projectile.setVelocity(velocity);
        }, 10);
        new BukkitRunnable() {
            boolean piercing = true; // Break through a cornerless 3x3 volume, but only once
            @Override
            public void run() {
                if (!valid(projectile, p)) {
                    projectile.remove();
                    cancel();
                } else {
                    projectile.getWorld().playEffect(projectile.getLocation(), Effect.STEP_SOUND, projectile.getBlockData().getMaterial());
                    Location path = projectile.getLocation().add(projectile.getVelocity()); // TODO: account for gravity
                    if (path.getBlock().getType().isSolid()) {
                        if (piercing) {
                            PIERCE_SHAPE.stream().forEach(v -> {
                                Block block = path.add(v).getBlock();
                                if (block.getType().isSolid() && AbilityUtil.validBlock(block)) {
                                    AbilityUtil.dropBlockRandomly(block).setVelocity(projectile.getVelocity());
                                }
                            });
                            piercing = false;
                        } else {
                            cancel();
                        }
                    }
                    final Set<Player> nearby = projectile.getWorld().getNearbyEntities(projectile.getLocation(), 1.5, 1.5, 1.5).stream()
                            .filter(entity -> entity instanceof Player)
                            .map(player -> (Player) player)
                            .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                            .filter(member -> !AbilityUtil.inSpawn(member))
                            .collect(Collectors.toSet());
                    if (!nearby.isEmpty()) {
                        for (Player player : nearby) {
                            if (!player.equals(p)) {
                                cancel();
                                player.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, projectile.getBlockData().getMaterial());
                                if (!Team.getTeamByPlayerName(player.getName()).equals(team)) {
                                    player.damage(piercing ? 10 : 5, p);
                                    AbilityUtil.applyStunEffect(player, 2 * 20);
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_HURT, 1, 1);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(CoronaCraft.instance, 11, 1);
        return projectile;
    }

    private static boolean valid(FallingBlock earth, Player p) {
        if (p == null || earth == null || !p.isOnline() || earth.getTicksLived() > 800 || earth.isOnGround() || earth.getVelocity().lengthSquared() < 0.0001) return false;
        Warzone zone1 = Warzone.getZoneByLocation(earth.getLocation());
        Warzone zone2 = Warzone.getZoneByPlayerName(p.getName());
        if (zone1 == null || !zone1.equals(zone2) || zone1.isReinitializing()) return false;
        return true;
    }
}
