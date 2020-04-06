package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Brawn extends AbilityStyle {

    public Brawn() {
        super("Brawn", new String[]{
                "Gain a boost of strength.",
                "Duration: 7 seconds",
                "Cost: 2 hearts"
        });
    }

    public int execute(Player p, Object... args) {
        p.damage(4.0);
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7 * 20, 1).apply(p);
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        return 0;
    }

}
