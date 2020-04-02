package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class VoidShuriken extends AbilityStyle {

    public VoidShuriken() {
        super("Void Shuriken", new String[]{
                "Damage and switch places with",
                "the target on hit with a shuriken."
        }, "coronacraft.ninja.voidshuriken");
    }

    /***
     * @param args target
     * @return
     */
    public int execute(Player p, Object... args) {
        LivingEntity target = (LivingEntity) args[0];

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 10);
        p.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        p.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 10);

        Location targetLoc = target.getLocation();
        target.teleport(p);
        p.teleport(targetLoc);
        target.damage(2);
        return 0;
    }

}
