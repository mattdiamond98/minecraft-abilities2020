package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Spellbook extends AbilityStyle {

    private WizardStyle light;
    private WizardStyle medium;
    private WizardStyle heavy;
    private WizardStyle capstone;
    private UltimateAbility ultimate;

    public Spellbook(
            String name,
            String[] description,
            String permission,
            WizardStyle light,
            WizardStyle medium,
            WizardStyle heavy,
            WizardStyle capstone,
            UltimateAbility ultimate, int ModelData
    ) {
        super(name, description, permission, ModelData);
        this.light = light;
        this.medium = medium;
        this.heavy = heavy;
        this.capstone = capstone;
        this.ultimate = ultimate;
    }

    public Spellbook(
            String name,
            String[] description,
            WizardStyle light,
            WizardStyle medium,
            WizardStyle heavy,
            WizardStyle capstone,
            UltimateAbility ultimate, int ModelData
    ) {
        this(name, description, null, light, medium, heavy, capstone, ultimate, ModelData);
    }

    @Override
    public int execute(Player player, Object... data) {
        if (data[0] instanceof PlayerInteractEvent) {
            PlayerInteractEvent e = (PlayerInteractEvent) data[0];
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                return (player.isSneaking() ? getHeavyStyle() : getLightStyle()).execute(player, e);
            } else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                return (player.isSneaking() ?  getCapstoneStyle() : getMediumStyle()).execute(player, e);
            }
        } else {
            Bukkit.getLogger().warning("Spellbook class executed on invalid Object");
        }
        return 0;
    }

    public WizardStyle getLightStyle() {
        return light;
    }

    public WizardStyle getMediumStyle() {
        return medium;
    }

    public WizardStyle getHeavyStyle() {
        return heavy;
    }

    public WizardStyle getCapstoneStyle() {
        return capstone;
    }

    public UltimateAbility getUltimate() { return ultimate; }

    public WizardStyle[] getStyles() {
        return new WizardStyle[]{light, medium, heavy, capstone};
    }
}
