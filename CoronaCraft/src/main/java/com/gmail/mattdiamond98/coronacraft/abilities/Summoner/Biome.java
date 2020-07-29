package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Biome extends Ability {
    public Biome() {
        super("Biome", Material.GRASS_BLOCK);
    }
    public static SummonerBiome getPlayerBiome(Player p){
        ItemStack item=p.getInventory().getItem(2);
        if(item.getItemMeta().getDisplayName().toLowerCase().contains("cave")){
            return new CaveBiome();
        }
        return null;


    }
}
