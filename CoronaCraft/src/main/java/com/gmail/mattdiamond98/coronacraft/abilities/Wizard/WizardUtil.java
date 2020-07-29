package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.entity.Player;

public final class WizardUtil {

    public static boolean handleManaCostAndCooldown(Player p, WizardStyle style, int MANA_COST) {
        return handleManaCostAndCooldown(p, style, MANA_COST, false);
    }

    public static boolean handleManaCostAndCooldown(Player p, WizardStyle style, int MANA_COST, boolean hideCooldownMessage) {
        if (p.getLevel() < MANA_COST) {
            AbilityUtil.notifyAbilityRequiresResources(p, "mana", MANA_COST);
            return false;
        } else if (CoronaCraft.isOnCooldown(p, style.ability.getItem())) {
            if (!hideCooldownMessage) AbilityUtil.notifyAbilityOnCooldown(p, style.ability);
            return false;
        }
        return true;
    }

}
