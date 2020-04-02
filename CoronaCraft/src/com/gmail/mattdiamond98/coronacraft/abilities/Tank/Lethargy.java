package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Lethargy extends AbilityStyle {

    public static final int COOL_DOWN = 40 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public Lethargy() {
        super("Lethargy", new String[]{"Weaken and fatigue your opponents", "Radius: 10", "Duration: 10 seconds", "Cooldown: 40 seconds"},
                "coronacraft.tank.lethargy");
    }

    public int execute(Player p, Object... args) {
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL).withColor(Color.BLUE).withFade(Color.BLACK).build();
        for (Location loc : AbilityUtil.getCircle(p.getLocation(), 10, 25)) {
            new InstantFirework(fireworkEffect, loc);
        }

        for (Player player : Warzone.getZoneByPlayerName(p.getName()).getPlayers()) {
            if (player.getLocation().distanceSquared(p.getLocation()) < 10 * 10) {
                if (!Team.getTeamByPlayerName(player.getName()).equals(Team.getTeamByPlayerName(p.getName()))) {
                    new PotionEffect(PotionEffectType.WEAKNESS, 200, 0).apply(player);
                    new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1).apply(player);
                    player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.LAVA);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 10, 1);
                }
            }
        }
        return COOL_DOWN;
    }
}
