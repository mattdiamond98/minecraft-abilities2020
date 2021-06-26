package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomSilverfish;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import org.bukkit.entity.Player;

public class SilverfishAbility extends MonsterAbilityStyle {
    public SilverfishAbility() {
        super("Silverfish", new String[]{"Basic silverfish mob"}, 0, new CaveBiome(), new CustomSilverfish(CoronaCraft.randomLoc, null, null, 0));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}