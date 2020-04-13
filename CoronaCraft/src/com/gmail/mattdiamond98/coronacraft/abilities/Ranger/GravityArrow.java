package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GravityArrow extends AbilityStyle {

    public static int ARROW_COST = 20;

    public GravityArrow() {
        super("Gravity Arrow", new String[] {
                "Shoot an anti-gravity arrow by crouching.",
                "Cost: 20 Arrows"
        }, "coronacraft.ranger.gravityarrow");
    }

    /***
     * @param p the Player
     * @param args arg0 instanceof Arrow the projectile
     * @return cooldown (currently 0)
     */
    public int execute(Player p, Object... args) {
        PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION, 7 * 20, 0);
        Longbow.potionEffectArrow(p, (Arrow) args[0], effect, ARROW_COST);
        return 0;
    }

}