package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import org.bukkit.Material;

import java.util.Map;
import java.util.TreeMap;

public class Tower extends SchematicStyle {

    public static final int COST_PLANKS = 48;
    public static final int COST_COBBLE = 12;

    private static final Map<Material, Integer> costs = new TreeMap<>();
    static {
        costs.put(Material.OAK_PLANKS,  COST_PLANKS);
        costs.put(Material.COBBLESTONE, COST_COBBLE);
    }

    public Tower() {
        super("Tower", new String[] {
                "Create a defensive tower",
                "where you stand.",
                String.format("Cost: %d Planks, %d Cobblestone", COST_PLANKS, COST_COBBLE),
                "Construction Time: 5 seconds"
        }, null, 703458, costs);
    }




}
