package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DippedArrow extends ProjectileAbilityStyle {

    public static int ARROW_COST = 6;

    public DippedArrow() {
        super("Dipped Arrow", new String[] {
                "When crouching, fire an arrow that applies",
                "poison and slowness to an enemy.",
                "Cost: " + ARROW_COST + " Arrows"
        }, 0);
    }

    /***
     * @param p the RateablePlayer
     * @param args arg0 instanceof Arrow the projectile
     * @return cooldown (currently 0)
     */
    public int execute(Player p, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        Projectile arrow = event.getEntity();
        Location loc = arrow.getLocation();

        // Safety checks
        if (arrow.isDead()) arrow.remove();

        return 0;
    }

    @Override
    public int onShoot(Projectile projectile) {
        if (Longbow.makeSpecialArrow(projectile, this, ARROW_COST)) {
            // Add potion effects
            ((Arrow) projectile).addCustomEffect(new PotionEffect(PotionEffectType.POISON, 6 * 20, 0), false);
            ((Arrow) projectile).addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 20, 0), false);
        }
        return 0;
    }
}
