package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;

public class SummonerUltimateManager extends UltimateManager {
    public SummonerUltimateManager() {
        super(new UltimateAbility[]{new BossAbility()});
    }
}
