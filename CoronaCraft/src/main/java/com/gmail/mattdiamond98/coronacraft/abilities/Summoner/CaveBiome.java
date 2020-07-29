package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.*;
import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.Entity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CaveBiome extends SummonerBiome {

    public CaveBiome() {
        super(new Entity[]{ new CustomZombie(null, null, null), new CustomSkeleton(null, null, null)}, "Cave", null, "coronacraft.summoner.cave", new String[]{"Maximum Creatures: 4","Possible Creatures:", "Zombie", "Skeleton", "Silverfish", "Spider", "Creeper", "Cave Spider", "Boss Mob: Giant Slime"}, 4);
    }

    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }

    @Override
    public Entity getEntityByName(String name, Team t, Location loc, Player p) {
        switch(name.toLowerCase()){
            case "zombie":
                return new CustomZombie(loc, t, p);
            case "skeleton":
                return new CustomSkeleton(loc, t, p);
            case "spider":
                return new CustomSpider(loc, t, p);
            case "silverfish":
                return new CustomSilverfish(loc, t, p);
            case "cave":
                return new CustomCaveSpider(loc, t, p);
        }

        return null;
    }
}
