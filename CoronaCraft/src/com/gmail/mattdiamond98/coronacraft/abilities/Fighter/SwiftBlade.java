package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
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
        new PotionEffect(PotionEffectType.SPEED, 40, 0).apply(p);
        return 0;
    }
}