package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import net.minecraft.server.v1_16_R3.Entity;

public abstract class MonsterAbilityStyle extends AbilityStyle {
    protected SummonerBiome sb;
    protected Entity e;
    public MonsterAbilityStyle(String name, String[] description, String permission, int ModelData, SummonerBiome summonerBiome, Entity et) {
        super(name, description, permission, ModelData);
        this.sb=summonerBiome;
        this.e=et;
    }

    public MonsterAbilityStyle(String name, String[] description, int ModelData, SummonerBiome summonerBiome, Entity et) {
        super(name, description, ModelData);
        this.sb=summonerBiome;
        this.e=et;
    }
    public SummonerBiome getBiome(){
        return this.sb;
    }
    public Entity getBaseMob(){
        return this.e;
    }
}
