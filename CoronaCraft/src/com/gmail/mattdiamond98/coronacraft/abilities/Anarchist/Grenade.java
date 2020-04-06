package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;

public class Grenade extends AbilityStyle {
    public Grenade() {
        super("Grenade", new String[] {
                "Blow apart enemies with an",
                "explosive shot.",
                "Cost: 1 TNT",
                "Cooldown: 10 seconds"
        });
    }

    public int execute(Player p, Object... args) {
        Egg egg = (Egg) args[0];
        egg.getWorld().createExplosion(egg.getLocation(),2.0F, false, true, (Player) egg.getShooter());
        return 0;
    }
}
