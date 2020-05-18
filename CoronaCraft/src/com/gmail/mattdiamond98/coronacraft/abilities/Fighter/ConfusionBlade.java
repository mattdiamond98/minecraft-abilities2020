package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Random;

public class ConfusionBlade extends AbilityStyle {

    private Random random = new Random();

    public ConfusionBlade() {
        super("Confusion Blade", new String[]{"20% chance to distort opponent's view on hit"});
    }

    /***
     * @param args arg1 must be a LivingEntity, arg2 must be a Double
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        LivingEntity e = (LivingEntity) args[0];
        if (random.nextFloat() < 0.2) {
            Location loc = e.getLocation();
            loc.setPitch((random.nextFloat() * 180) - 90);
            loc.setYaw((random.nextFloat() * 360) - 180);
            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.GLOWSTONE);
            e.teleport(loc);
        }
        return 0;
    }

}
