package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Brawn extends AbilityStyle {

    public Brawn() {
        super("Brawn", new String[]{
                "Gain a boost of strength.",
                "Duration: 10 seconds",
                "Cost: 2 hearts"
        });
    }

    public int execute(Player p, Object... args) {
        p.damage(UltimateTracker.isUltimateActive(p) ? 2.0 : 4.0);
        new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 0).apply(p);
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        return 0;
    }

}
