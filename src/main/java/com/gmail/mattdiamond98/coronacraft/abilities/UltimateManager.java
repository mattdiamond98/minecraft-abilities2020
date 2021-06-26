package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.AnarchistUltimateManager;

public abstract class UltimateManager {
    public UltimateAbility[] allAbilities;
    public UltimateAbility defaultAbility;

    public UltimateManager(UltimateAbility[] allAbilities){
        this.allAbilities=allAbilities;
        for (UltimateAbility ua:allAbilities){
            ua.manager=this;
        }
    }

}
