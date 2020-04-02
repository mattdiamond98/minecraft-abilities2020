package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonShuriken extends AbilityStyle {

    public PoisonShuriken() {
        super("Poison Shuriken", new String[]{
                "Damage and poison on hit.",
                "increase total poison duration",
                "on successive hits."
        });
    }

    /***
     * @param args target
     * @return
     */
    public int execute(Player p, Object... args) {
        LivingEntity target = (LivingEntity) args[0];
        if (Team.getTeamByPlayerName(target.getName()).getPlayers().contains(p)) return 0;
        target.damage(2, p);
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.SLIME_BLOCK);
        if (target.getPotionEffect(PotionEffectType.POISON) == null) {
            new PotionEffect(PotionEffectType.POISON, 4 * 20, 0).apply(target);
        } else {
            int duration = target.getPotionEffect(PotionEffectType.POISON).getDuration();
            new PotionEffect(PotionEffectType.POISON, duration + 4 * 20, 0).apply(target);
        }
        return 0;
    }

}
