package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WizardUltimateAbility extends UltimateAbility {

    public WizardUltimateAbility(){
        super("Wizard Ultimate");
    }

    @Override
    public String getName(Player player) {
        AbilityStyle spellBook = CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(player);
        if (spellBook instanceof Spellbook) {
            return ((Spellbook) spellBook).getUltimate().getName();
        }
        return getName();
    }

    @Override
    public void activate(Player player) {
        AbilityStyle spellBook = CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(player);
        if (spellBook instanceof Spellbook) {
            ((Spellbook) spellBook).getUltimate().activate(player);
        }
    }
}
