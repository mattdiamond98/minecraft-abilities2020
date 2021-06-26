package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomCaveSpider;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import org.bukkit.entity.Player;

public class CaveSpiderAbility extends MonsterAbilityStyle {
    public CaveSpiderAbility() {
        super("Cave Spider", new String[]{"Basic cave spider mob"}, 0, new CaveBiome(), new CustomCaveSpider(CoronaCraft.randomLoc, null, null, 0));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}