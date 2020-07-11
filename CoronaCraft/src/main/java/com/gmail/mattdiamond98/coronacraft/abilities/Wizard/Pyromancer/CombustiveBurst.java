package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.ChargeableCapstoneWizardStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class CombustiveBurst extends ChargeableCapstoneWizardStyle {

    public static final int MANA_COST = 3; // Per charge

    public static final int MAX_CHARGE = 20;
    public static final int MIN_CHARGE = 5;

    public static final int COOLDOWN_SECONDS = 60;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public CombustiveBurst() {
        super(
            "Combustive Burst",
            new String[] {
                "Combustive Burst",
                String.format("%d Mana", MANA_COST),
                String.format("%ds Cooldown", COOLDOWN_SECONDS),
                    "",
                    "Crouch + hold right click with wand to cast"
            }, new Ability("Combustive Burst", Material.CAMPFIRE) {
                @EventHandler
                public void onBlockPlace(BlockPlaceEvent e) {
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getBlockPlaced().getType() == item) {
                        e.setCancelled(true);
                    }
                }
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
        Team team = Team.getTeamByPlayerName(p.getName());
        if (team == null) return;
        List<Block> sight = p.getLineOfSight(AbilityUtil.transparent, 75);
        if (sight.size() < 1) return;
        Block target = sight.size() > 2 ? sight.get(sight.size() - 2) : sight.get(sight.size() - 1);
        List<BukkitTask> tasks = new LinkedList<>();
        for (int i = 1; i < sight.size(); i++) {
            Block block = sight.get(i);
            final Location location = block.getLocation().add(new Vector(0.5, 0.5, 0.5));
            if (i > sight.size() - 3) {
                tasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        burst(location, p, (((float) finalCharge) / MAX_CHARGE) * 3);
                    }
                }.runTaskLater(CoronaCraft.instance, i));
                break;
            }
            if (i % 10 == 0) {
                tasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.getWorld().createExplosion(location, 1, false, true, p);
                        FireworkEffect fireworkEffect = FireworkEffect.builder().trail(true)
                                .with(FireworkEffect.Type.BALL).withColor(Color.RED).build();
                        new InstantFirework(fireworkEffect, location);
                    }
                }.runTaskLater(CoronaCraft.instance, i));
            } else {
                tasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, block.getLocation()
                                .add(new Vector(0.5, 0.5, 0.5)), 1, 0, 0, 0);
                        final Set<Player> enemies = location.getWorld().getNearbyEntities(location, 2, 2, 2).stream()
                                .filter(entity -> entity instanceof Player)
                                .map(player -> (Player) player)
                                .filter(player -> Team.getTeamByPlayerName(player.getName()) != null)
                                .filter(player -> !team.getPlayers().contains(player))
                                .collect(Collectors.toSet());
                        if (!enemies.isEmpty()) {
                            burst(location, p, (((float) finalCharge) / MAX_CHARGE) * 3.3F);
                            for (BukkitTask task : tasks) {
                                if (!task.isCancelled()) {
                                    task.cancel();
                                }
                            }
                        }
                    }
                }.runTaskLater(CoronaCraft.instance, i));
            }
        }
    }

    @Override
    public void onCharge(final Player p, int charge) {
        if (charge == MAX_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1F, 1.0F);
        } else {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, ((float) charge) / ((float) MAX_CHARGE));
        }
        if (charge == MIN_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1F, 0.5F);
        }
        p.getWorld().playEffect(p.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 5);
        CHARGE_DATA_MAP.put(p.getUniqueId(), charge);
        p.setLevel(p.getLevel() - MANA_COST);
    }

    private void burst(Location location, Player p, float power) {
        location.getWorld().createExplosion(location, power, false, true, p);
        FireworkEffect fireworkEffect = FireworkEffect.builder().trail(true)
                .with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).build();
        new InstantFirework(fireworkEffect, location);
    }
}
