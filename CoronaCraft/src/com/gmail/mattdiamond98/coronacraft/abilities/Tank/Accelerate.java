package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Accelerate extends AbilityStyle {

    public Accelerate() {
        super("Accelerate", new String[]{"Buff allies with a speed boost!", "Radius: 10", "Duration: 10 seconds", "Cooldown: 30 seconds"});
    }

    /***
     * @param args not required
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BURST).withColor(Color.WHITE).withFade(Color.ORANGE).build();
        for (Location loc : AbilityUtil.getCircle(p.getLocation(), 10, 25)) {
            new InstantFirework(fireworkEffect, loc);
        }
        for (Player teammate : Team.getTeamByPlayerName(p.getName()).getPlayers()) {
            if (teammate.getLocation().distanceSquared(p.getLocation()) < 10 * 10) {
                new PotionEffect(PotionEffectType.SPEED, 200, 1).apply(teammate);
                teammate.getWorld().playEffect(teammate.getLocation(), Effect.STEP_SOUND, Material.SEA_LANTERN);
                teammate.getWorld().playSound(teammate.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                teammate.sendMessage(ChatColor.GREEN + "You feel invigorated with a boost of speed!");
            }
        }
        return 40 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }

}
