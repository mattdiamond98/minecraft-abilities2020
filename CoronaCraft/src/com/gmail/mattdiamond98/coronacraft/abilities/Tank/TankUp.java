package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TankUp extends AbilityStyle {

    public TankUp() {
        super("TankUp", new String[]{"Buff allies with protection!", "Radius: 10", "Duration: 10 seconds", "Cooldown: 60 seconds"});
    }

    /***
     * @param args not required
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL).withColor(Color.GRAY).withFade(Color.GREEN).build();
        for (Location loc : AbilityUtil.getCircle(p.getLocation(), 10, 25)) {
            new InstantFirework(fireworkEffect, loc);
        }
        for (Player teammate : Team.getTeamByPlayerName(p.getName()).getPlayers()) {
            if (teammate.getLocation().distanceSquared(p.getLocation()) < 10 * 10) {
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0).apply(teammate);
                teammate.getWorld().playEffect(teammate.getLocation(), Effect.STEP_SOUND, Material.IRON_BLOCK);
                teammate.getWorld().playSound(teammate.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 1);
                teammate.sendMessage(ChatColor.GREEN + "You feel as if nothing can hurt you!");
            }
        }
        return 60 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
