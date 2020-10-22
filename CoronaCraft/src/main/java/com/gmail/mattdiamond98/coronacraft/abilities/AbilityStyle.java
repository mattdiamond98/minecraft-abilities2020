package com.gmail.mattdiamond98.coronacraft.abilities;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class AbilityStyle {

    private String name;
    private String[] description;
    private String permission;
    private int modelData;

    public AbilityStyle(String name, String[] description, @Nullable String permission, @Nullable int ModelData) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.modelData=ModelData;
    }

    public AbilityStyle(String name, String[] description, int ModelData) {
        this(name, description, null, ModelData);
    }

    public abstract int execute(Player player, Object... data);

    public String getName() {
        return name;
    }
    public int getModelData(){
        return this.modelData;
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
