package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.MonsterAbilities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CaveBiome;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.CustomSlime;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.MonsterAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.SummonerBiome;
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.entity.Player;

public class SlimeAbility extends MonsterAbilityStyle {
    public SlimeAbility(String name, String[] description, String permission, int ModelData, SummonerBiome summonerBiome, Entity et) {
        super("Slime", new String[]{"Basic slime mob"}, "coronacraft.summoner.slime", 0, new CaveBiome(), new CustomSlime(CoronaCraft.randomLoc, null, null));
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}
