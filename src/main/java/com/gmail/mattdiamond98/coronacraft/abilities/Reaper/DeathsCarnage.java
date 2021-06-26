package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.stream.Collectors;

public class DeathsCarnage extends UltimateAbility {

    public static final int DURATION = 25 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    private int blockRange = 10;

    public DeathsCarnage() {
        super("Death's Carnage");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION /
                CoronaCraft.ABILITY_TICK_PER_SECOND * 20, 2));
        player.removePotionEffect(PotionEffectType.SLOW);

        // Create particle effect
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(false)
                .withColor(Color.BLACK).withFade(Color.GRAY).build();
        for (Location loc : AbilityUtil.getCircle(player.getLocation(), blockRange, 20)) {
            new InstantFirework(fireworkEffect, loc);
        }
    }

    @EventHandler
    public void onTick(CoolDownTickEvent event) {
        if (event.getPlayer() == null || !event.getPlayer().isOnline()) return;
        if (Warzone.getZoneByPlayerName(event.getPlayer().getName()) == null) {
            UltimateTracker.removeProgress(event.getPlayer());
        }
        else if (event.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(
                event.getPlayer()) == Loadout.REAPER) {
            float remaining = (event.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;

            Player player = event.getPlayer();
            Team team = Team.getTeamByPlayerName(player.getName());
            player.setExp(remaining);
            player.getWorld().playEffect(event.getPlayer().getLocation(), Effect.SMOKE, 10);

            // Apply effects to all nearby enemies
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

                    if (!enemy.hasPotionEffect(PotionEffectType.WITHER)) {
                        enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,
                                3 * 20, 0));
                    }
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,
                            3 * 20, 0));
                    enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                            3 * 20, 1));
                }
            }
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.REAPER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
            if (!AbilityUtil.inSpawn(e.getPlayer()) && AbilityUtil.inWarzone(e.getPlayer())) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                        10000000, 0, true, false));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.ANARCHIST) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }
}
