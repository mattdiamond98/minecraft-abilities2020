package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;

public class ReaperUltimateManager extends UltimateManager {
    public ReaperUltimateManager() {
        super(new UltimateAbility[]{new DeathsCarnage()});
    }
}
