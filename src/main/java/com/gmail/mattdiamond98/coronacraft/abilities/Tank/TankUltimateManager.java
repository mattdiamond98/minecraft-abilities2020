package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class TankUltimateManager extends UltimateManager{


    public TankUltimateManager() {
        super(new UltimateAbility[]{new DesolationFist()});
    }
}