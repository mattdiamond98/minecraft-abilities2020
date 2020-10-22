package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

public class Firebolt extends WizardStyle {

    public static final int MANA_COST = 3;
    public static final int COOLDOWN_ABILITY_TICKS = 2;
    public static final int MAX_COOLDOWN = 8;

    public Firebolt() {
        super("Firebolt", new String[]{
                        "Firebolt",
                        String.format("%d Mana", MANA_COST),
                        "",
                        "Left click with wand to cast"
                },
                new Ability("Firebolt", Material.BLAZE_POWDER) {});
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (handleManaCostAndCooldown(p, this, MANA_COST)) {
            if (CoronaCraft.isOnCooldown(p, getItem())) {
                CoronaCraft.setCooldown(p, getItem(), CoronaCraft.getCooldown(p, getItem()) + COOLDOWN_ABILITY_TICKS);
            } else {
                CoronaCraft.setCooldown(p, getItem(), COOLDOWN_ABILITY_TICKS);
            }
            p.setLevel(p.getLevel() - MANA_COST);
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0.5F);
            SmallFireball projectile = p.launchProjectile(SmallFireball.class);
            projectile.setVelocity(p.getLocation().getDirection().normalize().multiply(2.5));
        }
        return 0;
    }

    private boolean handleManaCostAndCooldown(Player p, WizardStyle style, int MANA_COST) {
        if (p.getLevel() < MANA_COST) {
            AbilityUtil.notifyAbilityRequiresResources(p, "mana", MANA_COST);
            return false;
        } else if (isOnCooldown(p, getItem())) {
            return false;
        }
        return true;
    }

    private boolean isOnCooldown(Player p, Material item) {
        if (p == null || item == null || !p.isOnline()) return false;
        return CoronaCraft.getPlayerCoolDowns().containsKey(new AbilityKey(p, item))
                ? CoronaCraft.getPlayerCoolDowns().get(new AbilityKey(p, item)) > MAX_COOLDOWN
                : false;
    }
}
