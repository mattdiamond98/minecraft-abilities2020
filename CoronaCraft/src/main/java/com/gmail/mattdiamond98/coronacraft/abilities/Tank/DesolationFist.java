package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.volume.Volume;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class DesolationFist extends UltimateAbility {

    private static Random rand = new Random();

    public static final int RADIUS = 5;
    public static final int HEIGHT = 3; // how far to search for surface when throwing blocks up
    public static final float JUMP_HEIGHT = 2.5F;

    public static final int DURATION = 20;
    public static final int DURATION_COOLDOWN_TICKS = DURATION * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION * 20;

    private static final HashSet<UUID> fallingPlayers = new HashSet<>();

    public DesolationFist() {
        super("Desolation Fist");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        player.setVelocity(new Vector(0, JUMP_HEIGHT, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, DURATION_MINECRAFT_TICKS, 0));
        if (player.getHealth() < 10) player.setHealth(10);
        player.sendMessage(ChatColor.GREEN + "Right click to target your Desolation Fist");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (UltimateTracker.isUltimateActive(e.getPlayer())
                && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.TANK
                && e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getPlayer().getVelocity().getY() < 0.5 && e.getPlayer().getLocation().getDirection().getY() < 0) { // at top of arc
                FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                        .with(FireworkEffect.Type.BURST).withColor(Color.WHITE).withFade(Color.BLUE).build();
                for (Location loc : AbilityUtil.getCircle(e.getPlayer().getLocation(), 10, 25)) {
                    new InstantFirework(fireworkEffect, loc);
                }
                e.getPlayer().removePotionEffect(PotionEffectType.SLOW_FALLING);

                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(3));
                fallingPlayers.add(e.getPlayer().getUniqueId());
                UltimateTracker.removeProgress(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (UltimateTracker.isUltimateActive(p) && UltimateTracker.getLoadout(p) == Loadout.TANK) {
                e.setCancelled(true);
                p.setVelocity(new Vector(0, JUMP_HEIGHT, 0));
            }
            else if (!fallingPlayers.contains(p.getUniqueId()) || (e.getCause() != EntityDamageEvent.DamageCause.FALL)) return;
            fallingPlayers.remove(p.getUniqueId());
            e.setCancelled(true);
            doShockwave(p);
        }
    }

    private void doShockwave(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 3, 1);
        final Location center = player.getLocation();
        final Warzone zone = Warzone.getZoneByPlayerName(player.getName());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 1));
        AbilityUtil.getEnemies(player).stream().filter(e -> e.getLocation().distanceSquared(player.getLocation()) < (RADIUS * RADIUS) + 2).forEach(enemy -> {
            enemy.setVelocity(enemy.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize().multiply(1.5).setY(1.5));
            enemy.damage(14, player);
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1));
        });

        throwBlockUp(center.getBlock());
        for (int i = 1; i <= RADIUS; i++) {
            final Set<Block> blocks = AbilityUtil.advancedCircle(center, i, i, true, false, 0).stream()
                    .map(Location::getBlock)
                    .map(DesolationFist::findTopSolid)
                    .filter(Objects::nonNull)
                    .filter(block -> zone.getVolume().contains(block.getLocation()))
                    .filter(block -> volumes(zone).stream().noneMatch(vol -> vol.contains(block)))
                    .collect(Collectors.toSet());
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                if (!zone.isReinitializing()) {
                    blocks.forEach(DesolationFist::throwBlockUp);
                }
            }, i * 2);
        }
    }

    private static Set<Volume> volumes(Warzone zone) {
        Set<Volume> zoneVolumes = new HashSet<>();
        zone.getTeams().stream().map(Team::getFlagVolume).filter(Objects::nonNull).collect(Collectors.toCollection(() -> zoneVolumes));
        zone.getTeams().stream().map(Team::getSpawnVolumes).map(x -> x.values())
                .flatMap(Collection::stream).collect(Collectors.toCollection(() -> zoneVolumes));
        return zoneVolumes;
    }

    private static Block findTopSolid(Block block) {
        if (block.getType().isSolid()) {
            for (int i = 0; i < HEIGHT; i++) {
                if (!block.getRelative(0, 1, 0).getType().isSolid()) return block;
                block = block.getRelative(0, 1, 0);
            }
            return null;
        } else {
            for (int i = 0; i < HEIGHT; i++) {
                if (block.getRelative(0, -1, 0).getType().isSolid()) return block.getRelative(0, -1, 0);
                block = block.getRelative(0, -1, 0);
            }
            return null;
        }
    }

    private static void throwBlockUp(Block block) {
        if (block != null && block.getType() != Material.AIR) {
            Material material = block.getType();
            block.setType(Material.AIR);
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), Bukkit.createBlockData(material));
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(new Vector(rand.nextFloat() * 0.5 - 0.25, 0.75, rand.nextFloat() * 0.5 - 0.25));
        }
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.TANK) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.TANK) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.TANK) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }


}

