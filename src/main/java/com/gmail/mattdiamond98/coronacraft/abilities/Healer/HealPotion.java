package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.entity.Player;

public class HealPotion extends AbilityStyle {
    public HealPotion() {
        super("Healing Potion Generator", new String[]{"Refills your healing potions", "once per 8 secs", "Max 2"}, 0);
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}
