package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

public class SummonStaff extends AbilityStyle {

    public SummonStaff() {
        super("Summoner's Staff", new String[]{"Summon your creatures"}, 0);
    }

    @Override
    public int execute(Player player, Object... data) {
        return ((SummonerBiome) CoronaCraft.getAbility(Material.GRASS_BLOCK).getStyle(player)).SpawnRandomCreature(((CraftWorld)player.getWorld()).getHandle(), Team.getTeamByPlayerName(player.getName()), player.getLocation(), player);
    }
}
