package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomSpider;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import org.bukkit.entity.Player;

public class SpiderAbility extends MonsterAbilityStyle {
    public SpiderAbility() {
        super("Spider", new String[]{"Basic spider mob"}, 0, new CaveBiome(), new CustomSpider(CoronaCraft.randomLoc, null, null, 0));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}

