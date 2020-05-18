package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.PlayerInteraction;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VoidShuriken extends AbilityStyle {

    public static final double DAMAGE = 2.0;
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
        if (!(args[0] instanceof Player)) return 0;
        Player target = (Player) args[0];
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 10);
        p.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
        p.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 10);

        Location targetLoc = target.getLocation();
        targetLoc.setDirection(p.getLocation().getDirection());
        Location prevLoc = p.getLocation();
        prevLoc.setDirection(target.getLocation().getDirection());
        target.teleport(p);
        p.teleport(targetLoc);
        PlayerInteraction.playerHarm(target, p);
        double newHealth = target.getHealth() - DAMAGE;
        if (newHealth <= 0) {
            target.damage(200, p);
        } else {
            target.setHealth(newHealth);
        }
        return 0;
    }

}
