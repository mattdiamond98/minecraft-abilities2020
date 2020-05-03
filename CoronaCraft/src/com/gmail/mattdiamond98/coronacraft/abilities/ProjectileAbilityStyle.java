package com.gmail.mattdiamond98.coronacraft.abilities;

import com.sun.istack.internal.Nullable;
import org.bukkit.entity.Projectile;

public abstract class ProjectileAbilityStyle extends AbilityStyle {

    public ProjectileAbilityStyle(String name, String[] description, @Nullable String permission) {
        super(name, description, permission);
    }

    public ProjectileAbilityStyle(String name, String[] description) {
        this(name, description, null);
    }

    public abstract int onShoot(Projectile p);

}
