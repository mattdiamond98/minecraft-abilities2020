package com.gmail.mattdiamond98.coronacraft;

import com.sun.istack.internal.Nullable;
import org.bukkit.entity.Player;

public abstract class AbilityStyle {

    private String name;
    private String[] description;
    private String permission;

    public AbilityStyle(String name, String[] description, @Nullable String permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    public AbilityStyle(String name, String[] description) {
        this(name, description, null);
    }

    public abstract int execute(Player player, Object... data);

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public boolean canBeUsedBy(Player player) {
        if (permission == null) return true;
        return player.hasPermission(permission);
    }

    // NOTE: compares only by name
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AbilityStyle)) return false;
        return name.equals(((AbilityStyle)o).getName());
    }


}
