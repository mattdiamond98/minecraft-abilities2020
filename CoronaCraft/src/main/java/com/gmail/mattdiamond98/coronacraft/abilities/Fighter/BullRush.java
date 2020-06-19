package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BullRush extends AbilityStyle {

    public static final int DURATION_SECONDS = 10;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;
    public static final int DURATION_COOLDOWN_TICKS = 10 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int COOLDOWN = 35 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public BullRush() {
        super("Bull Rush", new String[] {
                "Rush forward until you hit an",
                "enemy, knocking them up."
        });
    }

    @Override
    public int execute(Player p, Object... args) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_MINECRAFT_TICKS, 1, true, true, true));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PANDA_BITE, 0.8F, 0.5F);
        Rush.RUSHING_PLAYERS.add(p.getUniqueId());
        return DURATION_COOLDOWN_TICKS;
    }
}
