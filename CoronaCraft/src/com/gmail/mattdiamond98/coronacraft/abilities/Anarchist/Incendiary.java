package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;

public class Incendiary extends AbilityStyle {
    public Incendiary() {
        super("Incendiary", new String[] {
                "Set fire to an area with an",
                "fiery explosion.",
                "Cost: 1 TNT",
                "Cooldown: 10 seconds"
        }, "coronacraft.anarchist.incendiary");
    }

    public int execute(Player p, Object... args) {
        Egg egg = (Egg) args[0];
        egg.getWorld().createExplosion(egg.getLocation(),2.0F, true, false, (Player) egg.getShooter());
        return 0;
    }
}
