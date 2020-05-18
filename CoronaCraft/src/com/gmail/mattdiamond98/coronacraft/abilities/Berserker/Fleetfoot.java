package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Fleetfoot extends AbilityStyle {

    public Fleetfoot() {
        super ("Fleetfoot", new String[] {
                "Gain a boost of speed.",
                "Duration: 20 seconds",
                "Cost: 2 hearts"
        }, "coronacraft.berserker.fleetfoot");
    }

    public int execute(Player p, Object... args) {
        p.damage(UltimateTracker.isUltimateActive(p) ? 2.0 : 4.0);
        new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1).apply(p);
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        return 0;
    }
}
