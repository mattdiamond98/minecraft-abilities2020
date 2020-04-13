package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Enfeeble extends AbilityStyle {

    public Enfeeble() {
        super("Enfeeble", new String[] {
                "Restrict your opponent's attacks,",
                "lessening their damage."
        }, "coronacraft.gladiator.enfeeble");
    }

    public int execute(Player p, Object... args) {
        Player caught = (Player) args[0];
        new PotionEffect(PotionEffectType.WEAKNESS, 8 * 20, 1).apply(caught);
        caught.getLocation().getWorld().playEffect(caught.getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
        return 5 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
