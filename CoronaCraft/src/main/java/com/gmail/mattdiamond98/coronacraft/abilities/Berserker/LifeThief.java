package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class LifeThief extends AbilityStyle {

    public static final int HEAL_AMOUNT = 8;

    public LifeThief() {
        super("Life Thief", new String[] {
                "Gain back four hearts",
                "and restore your hunger",
                "when you slay an enemy"
        },903456);
    }

    /***
     * @param p the player/killer using the waraxe
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), p.getHealth() + HEAL_AMOUNT));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
        p.setSaturation(Math.min(p.getSaturation() + 2, 20));
        p.setFoodLevel(20);
        return 0;
    }
}
