package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class EngineerUltimateManager extends UltimateManager {
    public EngineerUltimateManager() {
        super(new UltimateAbility[]{new SpacetimePortal()});
    }
}
