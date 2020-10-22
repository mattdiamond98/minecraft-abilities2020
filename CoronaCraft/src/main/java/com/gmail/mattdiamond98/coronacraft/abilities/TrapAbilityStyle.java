package com.gmail.mattdiamond98.coronacraft.abilities;

import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class TrapAbilityStyle extends AbilityStyle {

    private static Map<String, Metadatable> playerTraps = new HashMap<>();

    public TrapAbilityStyle(String name, String[] description, @Nullable String permission, int ModelData) {
        super(name, description, permission, ModelData);
    }

    public TrapAbilityStyle(String name, String[] description, int ModelData) {
        this(name, description, null, ModelData);
    }

    public static Map<String, Metadatable> getPlayerTraps() { return playerTraps; }

    public abstract int onDestroy(Player player);
    public abstract int onPlace(Player player, Metadatable p);
}
