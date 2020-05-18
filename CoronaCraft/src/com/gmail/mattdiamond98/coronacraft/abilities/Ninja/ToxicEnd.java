package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class ToxicEnd extends AbilityStyle {

    public ToxicEnd() {
        super("Toxic End", new String[]{
                "Crouch hit with Shadow Knife to",
                "remove all poison from the enemy,",
                "damaging them equal to the duration."
        });
    }

    /***
     * @param args arg0: the target instanceof LivingEntity
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        LivingEntity target = (LivingEntity) args[0];
        if (target.getPotionEffect(PotionEffectType.POISON) == null) return 0;
        int duration = target.getPotionEffect(PotionEffectType.POISON).getDuration();
        target.removePotionEffect(PotionEffectType.POISON);
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.SLIME_BLOCK);
        if (target.getHealth() - (duration / 20) <= 0) {
            target.damage(200);
        } else {
            target.setHealth(target.getHealth() - (duration / 20));
            target.damage( 2);
        }
        return 0;
    }

}
