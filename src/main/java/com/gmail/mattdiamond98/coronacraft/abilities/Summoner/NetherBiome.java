package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.*;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;

public class NetherBiome extends SummonerBiome {
    public NetherBiome() {
        super(new Entity[]{new CustomBlaze(CoronaCraft.randomLoc, null, null, 0), new CustomZombiePigman(CoronaCraft.randomLoc, null, null), new CustomMagmaCube(CoronaCraft.randomLoc, null, null ,0)}, "Nether", new FireWither(CoronaCraft.randomLoc, null, null, 0), "coronacraft.summoner.nether", new String[]{"Maximum Creatures: 3","Possible Creatures:", "Blaze", "Magma Cube", "Zombie Pigman",  "Boss Mob: Fire Wither"}, 3);
    }

    @Override
    public Entity getEntityByName(String name, Team t, Location loc, Player p) {
        String s=name.toLowerCase();
        if(s.contains("blaze")){
            return new CustomBlaze(loc, p, t, 0);}
        else if(s.contains("magma")){
            return new CustomMagmaCube(loc, p, t, 0);
        }else if(s.contains("pigman")){
            return new CustomZombiePigman(loc, p, t);
        }else if(s.contains("fire")){
            return new FireWither(loc, p, t, 0);
        }



        return null;
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }
}
