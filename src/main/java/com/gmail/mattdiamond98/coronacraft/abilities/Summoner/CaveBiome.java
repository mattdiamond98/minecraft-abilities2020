package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.*;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CaveBiome extends SummonerBiome {

    public CaveBiome() {
        super(new Entity[]{ new CustomZombie(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null, 0), new CustomSkeleton(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null, 0), new CustomCaveSpider(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null, 0), new CustomSpider(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null, 0), new CustomSlime(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null)}, "Cave", new SplitterSilverfish(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), null, null), "coronacraft.summoner.cave", new String[]{"Maximum Creatures: 4","Possible Creatures:", "Zombie", "Skeleton", "Silverfish", "Spider",  "Cave Spider", "Boss Mob: Giant Slime"}, 4);
    }


    @Override
    public int execute(Player player, Object... data) {
        return 0;
    }

    @Override
    public Entity getEntityByName(String name, Team t, Location loc, Player p) {
        String s=name.toLowerCase();
            if(s.contains("slime")){
                return new CustomSlime(loc, t, p);}
            else if(s.contains("zombie")){
                return new CustomZombie(loc, t, p, 0);}
            else if(s.contains("splitter")){
                return new SplitterSilverfish(loc, t, p);
            }
            else if(s.contains("skeleton")){
            return new CustomSkeleton(loc, t, p, 0);}
            else if(s.contains("spider")){
                return new CustomSpider(loc, t, p, 0);}

            else if(s.contains("cave")){
                return new CustomCaveSpider(loc, t, p, 0);}


        return null;
    }
}
