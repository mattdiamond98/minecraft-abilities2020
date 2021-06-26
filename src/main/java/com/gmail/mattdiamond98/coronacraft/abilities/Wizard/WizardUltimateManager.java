package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class WizardUltimateManager extends UltimateManager {
    public WizardUltimateManager() {
        super(new UltimateAbility[]{new WizardUltimateAbility()});
    }
}
