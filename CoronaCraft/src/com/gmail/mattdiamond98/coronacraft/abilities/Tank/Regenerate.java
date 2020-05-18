package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Regenerate extends AbilityStyle {

    public Regenerate() {
        super("Regenerate", new String[]{
                "Heal nearby allies over time,",
                "healing more if they are hurt",
                "Radius: 10",
                "Duration: 4 seconds",
                "Cooldown: 35 seconds"},
                "coronacraft.tank.regenerate");
    }

    /***
     * @param args not required
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).flicker(true).build();
        for (Location loc : AbilityUtil.getCircle(p.getLocation(), 10, 10)) {
            new InstantFirework(fireworkEffect, loc);
        }
        for (Player teammate : Team.getTeamByPlayerName(p.getName()).getPlayers()) {
            if (teammate.getLocation().distanceSquared(p.getLocation()) < 10 * 10) {
                if (teammate.getHealth() <= 16.0) {
                    int amplifier = 0;
                    if (teammate.getHealth() <= 12.0) amplifier = 1;
                    if (teammate.getHealth() <= 8.0 ) amplifier = 2;
                    if (teammate.getHealth() <= 4.0 ) amplifier = 3;
                    new PotionEffect(PotionEffectType.REGENERATION, 80, amplifier).apply(teammate);
                    teammate.getWorld().spawnParticle(Particle.HEART, teammate.getEyeLocation(), 3);
                    teammate.getWorld().playSound(teammate.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                    teammate.sendMessage(ChatColor.GREEN + "You feel your wounds closing up.");
                }

            }
        }
        return 40 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
