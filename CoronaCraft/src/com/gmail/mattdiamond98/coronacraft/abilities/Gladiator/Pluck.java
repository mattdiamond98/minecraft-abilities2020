package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Pluck extends AbilityStyle {

    public Pluck() {
        super("Pluck", new String[] {
                "Pull your opponent toward you",
                "with great force, stunning them."
        }, "coronacraft.gladiator.pluck");
    }

    public int execute(Player p, Object... args) {
        Player caught = (Player) args[0];
        caught.setVelocity(p.getEyeLocation().toVector().subtract(caught.getLocation().toVector()).normalize().multiply(2));

        new PotionEffect(PotionEffectType.SLOW, 20, 3).apply(caught);
        new PotionEffect(PotionEffectType.WEAKNESS, 20, 1).apply(caught);
        new PotionEffect(PotionEffectType.BLINDNESS, 20, 0).apply(caught);
        return 5 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
