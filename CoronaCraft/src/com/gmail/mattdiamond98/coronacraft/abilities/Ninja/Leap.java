package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Leap extends AbilityStyle {

    public static final int COOL_DOWN = 3 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public Leap() {
        super("Leap", new String[]{"Jump in the direction you are facing"});
    }

    public int execute(Player p, Object... args) {
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.BLACK_WOOL);
        Vector v = p.getVelocity().add(p.getEyeLocation().getDirection().normalize().multiply(1.3));
        v.setY(v.getY() * 0.5);
        p.setVelocity(v);
        return COOL_DOWN;
    }
}
