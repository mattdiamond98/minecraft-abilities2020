package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlayingArrow extends AbilityStyle {

    public static int ARROW_COST = 64;

    public SlayingArrow() {
        super("Slaying Arrow", new String[] {
                "Shoot an Arrow of Slaying by crouching.",
                "Cost: 64 Arrows"
        }, "coronacraft.ranger.slayingarrow");
    }

    /***
     * @param p the Player
     * @param args arg0 instanceof Arrow the projectile
     * @return cooldown (currently 0)
     */
    public int execute(Player p, Object... args) {
        PotionEffect effect = new PotionEffect(PotionEffectType.HARM, 9, 5);
        Longbow.potionEffectArrow(p, (Arrow) args[0], effect, ARROW_COST);
        return 0;
    }

}