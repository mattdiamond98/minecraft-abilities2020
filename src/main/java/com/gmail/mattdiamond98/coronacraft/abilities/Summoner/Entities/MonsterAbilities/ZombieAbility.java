package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomZombie;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import org.bukkit.entity.Player;

public class ZombieAbility extends MonsterAbilityStyle {
    public ZombieAbility() {
        super("Zombie", new String[]{"Basic zombie mob"}, 0, new CaveBiome(), new CustomZombie(CoronaCraft.randomLoc, null, null, 0));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}
