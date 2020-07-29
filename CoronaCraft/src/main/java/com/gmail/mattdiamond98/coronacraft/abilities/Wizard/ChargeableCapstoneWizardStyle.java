package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ChargeableCapstoneWizardStyle extends WizardStyle {

    private int manaCost;
    private int maxCharge;
    private int cooldownAbilityTicks;

    protected static final Map<UUID, Integer> CHARGE_DATA_MAP = new HashMap<>();

    public ChargeableCapstoneWizardStyle(String name, String[] description, Ability ability, int manaCost, int maxCharge, int cooldownAbilityTicks) {
        super(name, description, ability);
        this.manaCost = manaCost;
        this.maxCharge = maxCharge;
        this.cooldownAbilityTicks = cooldownAbilityTicks;
    }

    public abstract void onCharge(final Player p, int charge);
    public abstract void onRelease(final Player p, int charge);

    @Override
    public int execute(final Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, manaCost)) {
            int charge = 1;
            if (!CHARGE_DATA_MAP.containsKey(p.getUniqueId())) {
                p.sendTitle("", ChatColor.YELLOW + "Hold right click to channel", 0, 40, 0);
                CHARGE_DATA_MAP.put(p.getUniqueId(), charge);
            } else {
                charge = CHARGE_DATA_MAP.get(p.getUniqueId()) + 1;
                if (charge <= maxCharge) {
                    onCharge(p, charge);
                    Warzone zone = Warzone.getZoneByPlayerName(p.getName());
                    final int finalCharge = charge;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        Warzone currentZone = Warzone.getZoneByPlayerName(p.getName());
                        if (p.isOnline() && currentZone != null && currentZone.equals(zone) && !zone.isReinitializing()) {
                            if (!p.isSneaking() || finalCharge == CHARGE_DATA_MAP.get(p.getUniqueId())) {
                                CoronaCraft.setCooldown(p, ability.getItem(), cooldownAbilityTicks);
                                CHARGE_DATA_MAP.remove(p.getUniqueId());
                                onRelease(p, finalCharge);
                            }
                        } else {
                            CHARGE_DATA_MAP.remove(p);
                        }
                    }, 15);
                }

            }
        }
        return 0;
    }
}
