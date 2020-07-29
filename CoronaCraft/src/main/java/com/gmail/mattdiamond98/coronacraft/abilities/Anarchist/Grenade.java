package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarzoneConfig;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;

public class Grenade extends AbilityStyle {
    public Grenade() {
        super("Grenade", new String[] {
                "Blow apart enemies with an",
                "explosive shot.",
                "Cost: 1 TNT",
                "Cooldown: 10 seconds"
        }, 323456);
    }

    public int execute(Player p, Object... args) {
        Egg egg = (Egg) args[0];
        Warzone zone = Warzone.getZoneByLocation(egg.getLocation());
        boolean destroy = zone == null ||
                zone.getWarzoneConfig().contains(WarzoneConfig.UNBREAKABLE) && zone.getWarzoneConfig().getBoolean(WarzoneConfig.UNBREAKABLE);
        egg.getWorld().createExplosion(egg.getLocation(),2.0F, false, !destroy, (Player) egg.getShooter());
        return 0;
    }
}
