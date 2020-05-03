package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class InfernalBlade extends AbilityStyle {

    public InfernalBlade() {
        super("Infernal Blade", new String[]{"Set fire to opponents on hit"});
    }

    /***
     * @param args arg1 must be a LivingEntity
     * @return cooldown 0
     */
    public int execute(Player p, Object... args) {
        LivingEntity e = (LivingEntity) args[0];
        e.setFireTicks(40);
        e.getWorld().playEffect(e.getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
        return 0;
    }
}
