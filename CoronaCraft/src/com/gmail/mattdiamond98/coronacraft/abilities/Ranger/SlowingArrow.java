package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowingArrow extends AbilityStyle {

    public static int ARROW_COST = 5;

    public SlowingArrow() {
        super("Slowing Arrow", new String[] {
                "Shoot a slowing arrow by crouching.",
                "Cost: 5 Arrows"
        });
    }

    /***
     * @param p the Player
     * @param args arg0 instanceof Arrow the projectile
     * @return cooldown (currently 0)
     */
    public int execute(Player p, Object... args) {
        PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 10 * 20, 0);
        Longbow.potionEffectArrow(p, (Arrow) args[0], effect, ARROW_COST);
        return 0;
    }

}