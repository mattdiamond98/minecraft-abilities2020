package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class RangerUltimateManager extends UltimateManager {
    public RangerUltimateManager() {
        super(new UltimateAbility[]{new ArrowStorm()});
    }
}
