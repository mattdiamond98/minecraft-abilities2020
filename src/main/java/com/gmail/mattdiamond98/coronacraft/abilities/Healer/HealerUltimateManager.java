package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class HealerUltimateManager extends UltimateManager {
    public HealerUltimateManager() {
        super(new UltimateAbility[]{new DivineAura()});
    }
}
