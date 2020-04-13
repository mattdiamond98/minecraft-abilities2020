package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Entangle extends AbilityStyle {

    public Entangle() {
        super("Entangle", new String[] {
                "Bind your opponent, preventing",
                "them from moving."
        });
    }

    public int execute(Player p, Object... args) {
        Player caught = (Player) args[0];
        new PotionEffect(PotionEffectType.SLOW, 8 * 20, 3).apply(caught);
        caught.getLocation().getWorld().playEffect(caught.getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
        return 5 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
