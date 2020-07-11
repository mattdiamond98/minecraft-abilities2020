package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Material;

/**
 * Helper class that also has an enclosed Ability to allow a style to have a unique item/cooldown
 */
public abstract class WizardStyle extends AbilityStyle {

    protected Ability ability;

    public WizardStyle(String name, String[] description, Ability ability) {
        super(name, description, null);
        this.ability = ability;
    }

    public Ability getAbility() { return ability; }

    public Material getItem() { return ability.getItem(); }
}
