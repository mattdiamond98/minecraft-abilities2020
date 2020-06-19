package com.gmail.mattdiamond98.coronacraft.abilities;

import com.sun.istack.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;

import java.util.HashMap;
import java.util.Map;

public abstract class TrapAbilityStyle extends AbilityStyle {

    private static Map<String, Metadatable> playerTraps = new HashMap<>();

    public TrapAbilityStyle(String name, String[] description, @Nullable String permission) {
        super(name, description, permission);
    }

    public TrapAbilityStyle(String name, String[] description) {
        this(name, description, null);
    }

    public static Map<String, Metadatable> getPlayerTraps() { return playerTraps; }

    public abstract int onDestroy(Player player);
    public abstract int onPlace(Player player, Metadatable p);
}
