package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class AnarchistUltimateManager extends UltimateManager {

    public AnarchistUltimateManager() {
        super(new UltimateAbility[]{new TNTTrail(), new Buddies()});
    }
}
