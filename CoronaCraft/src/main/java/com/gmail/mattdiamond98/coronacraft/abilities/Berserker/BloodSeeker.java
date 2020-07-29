package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BloodSeeker extends AbilityStyle {

    public BloodSeeker() {
        super("Blood Seeker", new String[] {
                "Increase the duration and",
                "amplifier of your strength",
                "effect with each kill."
        }, "coronacraft.berserker.bloodseeker", 903456);
    }

    public int execute(Player p, Object... args) {
        if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            PotionEffect current = p.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
                    Math.min(current.getDuration() + 6 * 20, 8 * 20), current.getAmplifier() + 1).apply(p);
        } else {
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6 * 20, 0).apply(p);
        }
        return 0;
    }
}
