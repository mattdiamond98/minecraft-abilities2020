package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RoseStyle extends AbilityStyle {
    public int cooldownSeconds = 100;
    public int fuelCostpercent;

    public RoseStyle(String name, String[] description, int ModelData) {
        super(name, description, ModelData);
    }

    public RoseStyle(String name, String[] description, String permission, int ModelData) {
        super(name, description, permission, ModelData);
    }

    @Override
    public int execute(Player player, Object... data) {
        Bukkit.broadcastMessage("This RoseStyle has not been implemented yet");
        return 0;
    }
}
