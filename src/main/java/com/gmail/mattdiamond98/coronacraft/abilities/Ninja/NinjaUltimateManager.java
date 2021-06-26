package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class NinjaUltimateManager extends UltimateManager {
    public NinjaUltimateManager() {
        super(new UltimateAbility[]{new ShadowStride()});
    }
}
