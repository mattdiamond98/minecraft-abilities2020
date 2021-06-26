package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class GladiatorUltimateManager extends UltimateManager {
    public GladiatorUltimateManager() {
        super(new UltimateAbility[]{new StormGodsWrath()});
    }
}
