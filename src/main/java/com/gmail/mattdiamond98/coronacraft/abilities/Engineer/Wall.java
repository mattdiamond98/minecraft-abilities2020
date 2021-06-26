package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import org.bukkit.Material;

import java.util.Map;
import java.util.TreeMap;

public class Wall extends SchematicStyle {

    public static final int COST_PLANKS = 16;
    public static final int COST_COBBLE = 32;

    private static final Map<Material, Integer> costs = new TreeMap<>();
    static {
        costs.put(Material.OAK_PLANKS,  COST_PLANKS);
        costs.put(Material.COBBLESTONE, COST_COBBLE);
    }

    public Wall() {
        super("Wall", new String[] {
                "Create a strategic wall",
                "in front of you.",
                String.format("Cost: %d Planks, %d Cobblestone", COST_PLANKS, COST_COBBLE),
                "Construction Time: 5 seconds"
        }, "coronacraft.engineer.wall", 703456, costs);
    }
}
