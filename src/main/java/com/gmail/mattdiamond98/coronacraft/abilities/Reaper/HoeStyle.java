package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HoeStyle extends AbilityStyle {
    public int cooldownTicks;
    public int fuelCost;

    public HoeStyle(String name, String[] description, int ModelData) {
        super(name, description, ModelData);
    }

    public HoeStyle(String name, String[] description, String permission, int ModelData) {
        super(name, description, permission, ModelData);
    }

    @Override
    public int execute(Player player, Object... data) {
        Bukkit.broadcastMessage("This HoeStyle has not been implemented yet");
        return 0;
    }
}
