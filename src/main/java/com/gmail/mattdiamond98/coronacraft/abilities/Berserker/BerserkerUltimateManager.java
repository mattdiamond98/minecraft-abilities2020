package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class BerserkerUltimateManager extends UltimateManager {
    public BerserkerUltimateManager(){
        super(new UltimateAbility[]{new UndyingFrenzy(), new PureHatred()});
    }
}
