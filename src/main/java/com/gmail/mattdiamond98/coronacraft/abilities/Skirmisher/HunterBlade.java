package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class HunterBlade extends AbilityStyle {
    public HunterBlade() {
        super("Hunter Blade", new String[]{"Deal 50% more damage",
                "against glowing enemies."
        },723456);
    }

    /***
     * @param p the player activating the ability
     * @param args args[0] instanceof EntityDamageByEntityEvent the event
     * @return
     */
    public int execute(Player p, Object... args) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        if (((LivingEntity) e.getEntity()).getPotionEffect(PotionEffectType.GLOWING) != null) {
            e.setDamage(e.getDamage() * 1.5);
            e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        }
        return 0;
    }
}
