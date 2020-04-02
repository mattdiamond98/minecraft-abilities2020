package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WarpStrike extends AbilityStyle {

    public WarpStrike() {
        super("Warp Strike", new String[]{
                "Crouch hit with Shadow Knife to",
                "teleport a short distance in the",
                "opposite direction of the enemy"
        }, "coronacraft.ninja.warpstrike");
    }

    /***
     * @param args arg0: the target instanceof LivingEntity
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        LivingEntity e = (LivingEntity) args[0];
        Vector difference = p.getLocation().subtract(e.getLocation()).toVector();
        difference = difference.normalize().multiply(10);
        Location destination = p.getLocation().add(difference);
        if (!p.getWorld().getBlockAt(destination).getType().isOccluding()) {
            teleportWithEffect(p, destination);
            return 0;
        }
        for (int i = 1; i < 5; i++) {
            for (Vector v : AbilityUtil.unitVectors()) {
                if (!p.getWorld().getBlockAt(destination.add(v.multiply(i))).getType().isOccluding()) {

                    teleportWithEffect(p, destination.add(v.multiply(i)));
                    return 0;
                }
            }
        }
        teleportWithEffect(p, destination);
        return 0;
    }

    private void teleportWithEffect(Player p, Location destination) {
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 10);
        destination.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        destination.getWorld().playEffect(destination, Effect.SMOKE, 10);
        p.teleport(destination);
    }

}
