package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class FighterUltimateManager extends UltimateManager {
    public FighterUltimateManager() {
        super(new UltimateAbility[]{new Omnislash(), new GigaSlash()});
    }
}
