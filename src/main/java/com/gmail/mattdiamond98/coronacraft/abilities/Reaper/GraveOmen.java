package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.stream.Collectors;

public class GraveOmen extends RoseStyle {
    private int blockRange = 5;

    public GraveOmen() {
        super("Grave Omen", new String[] {
                "Give 7 seconds of Wither II to all",
                "enemies within a 5 block radius.",
                "Cooldown: 15 seconds"
        }, 0);
        cooldownSeconds = 15;
        fuelCostpercent=15;
    }

    @Override
    public int execute(Player player, Object... data) {
        // Get player's team
        Team team = Team.getTeamByPlayerName(player.getName());
        if (team == null) return 0; // Do not allow spectators to use this ability

        // Give wither 2 to all nearby enemies
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
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,
                        7 * 20, 1));
            }
        }

        // Create particle effect
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(false)
                .withColor(Color.WHITE).withFade(Color.PURPLE).build();
        for (Location loc : AbilityUtil.getCircle(player.getLocation(), blockRange, 10)) {
            new InstantFirework(fireworkEffect, loc);
        }
        return 0;
    }
}
