package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class SkirmisherUltimateManager extends UltimateManager {
    public SkirmisherUltimateManager() {
        super(new UltimateAbility[]{new InstinctiveHunter()});
    }
}
