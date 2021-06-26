package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomSkeleton;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomZombie;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import org.bukkit.entity.Player;

public class SkeletonAbility extends MonsterAbilityStyle {
    public SkeletonAbility() {
        super("Skeleton", new String[]{"Basic skeleton mob"}, 0, new CaveBiome(), new CustomSkeleton(CoronaCraft.randomLoc, null, null, 0));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}