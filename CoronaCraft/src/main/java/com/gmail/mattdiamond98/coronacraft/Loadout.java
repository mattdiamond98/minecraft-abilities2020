package com.gmail.mattdiamond98.coronacraft;


import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.TNTTrail;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.UndyingFrenzy;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.SpacetimePortal;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.Omnislash;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.StormGodsWrath;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShadowStride;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.ArrowStorm;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.InstinctiveHunter;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.DesolationFist;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUltimateAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum Loadout {

    FIGHTER(Material.DIAMOND_SWORD, new Omnislash()),
    RANGER(Material.BOW, new ArrowStorm()),
    ENGINEER(Material.IRON_PICKAXE, new SpacetimePortal()),
    BERSERKER(Material.DIAMOND_AXE, new UndyingFrenzy()),
    SKIRMISHER(Material.CROSSBOW, new InstinctiveHunter()),
    NINJA(Material.SHEARS, new ShadowStride()),
    GLADIATOR(Material.TRIDENT, new StormGodsWrath()),
    TANK(Material.SHIELD, new DesolationFist()),
    ANARCHIST(Material.FLINT_AND_STEEL, new TNTTrail()),
    WIZARD(Material.BLAZE_ROD, new WizardUltimateAbility());

    private Material item;
    private UltimateAbility ultimate;

    Loadout(Material item, UltimateAbility ultimate) {
        this.item = item;
        this.ultimate = ultimate;
    }

    public Material getItem() {
        return item;
    }

    public UltimateAbility getUltimate() {
        return ultimate;
    }

    public static Loadout loadoutFromItem(Material material) {
        for (Loadout loadout : Loadout.values()) {
            if (material == loadout.item) return loadout;
        }
        return null;
    }

    public static Loadout getLoadout(Player p) {
        Loadout offhand = loadoutFromItem(p.getInventory().getItemInOffHand().getType());
        if (offhand != null) return offhand;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null) {
                Loadout loadout = loadoutFromItem(itemStack.getType());
                if (loadout != null) return loadout;
            }
        }
        return null;
    }
}
