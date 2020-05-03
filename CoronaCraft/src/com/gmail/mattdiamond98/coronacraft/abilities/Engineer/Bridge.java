package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import org.bukkit.Material;

import java.util.Map;
import java.util.TreeMap;

public class Bridge extends SchematicStyle {

    public static final int COST_PLANKS = 32;
    public static final int COST_COBBLE = 8;

    private static final Map<Material, Integer> costs = new TreeMap<>();
    static {
        costs.put(Material.OAK_PLANKS,  COST_PLANKS);
        costs.put(Material.COBBLESTONE, COST_COBBLE);
    }

    public Bridge() {
        super("Bridge", new String[] {
                "Create a tactical bridge",
                "in front of you.",
                String.format("Cost: %d Planks, %d Cobblestone", COST_PLANKS, COST_COBBLE),
                "Construction Time: 5 seconds"
        }, "coronacraft.engineer.bridge", costs);
    }
}
