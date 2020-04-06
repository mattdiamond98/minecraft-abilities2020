package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LifeThief extends AbilityStyle {

    public static final int HEAL_AMOUNT = 6;

    public LifeThief() {
        super("Life Thief", new String[] {
                "Gain back three hearts",
                "and restore your hunger",
                "when you slay an enemy"
        });
    }

    /***
     * @param p the player/killer using the waraxe
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        p.setHealth(p.getHealth() + HEAL_AMOUNT);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
        return 0;
    }
}
