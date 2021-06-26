package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rapture extends AbilityStyle {
    public Rapture() {
        super("Rapture", new String[]{"Gives Resistance and Weakness II ", "for 10 secs"}, "coronacraft.healer.rapture", 0);
    }

    @Override
    public int execute(Player player, Object... data) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
        return 20* CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
