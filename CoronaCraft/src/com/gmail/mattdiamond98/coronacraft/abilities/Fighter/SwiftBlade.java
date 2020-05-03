package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwiftBlade  extends AbilityStyle {

    public SwiftBlade() {
        super("Swift Blade",
                new String[]{"Gain a boost of speed when you", "hit your opponent."},
                "coronacraft.fighter.swiftblade");
    }

    /***
     * @param args not required
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        new PotionEffect(PotionEffectType.SPEED, 140, 0, true).apply(p);
        return 0;
    }
}