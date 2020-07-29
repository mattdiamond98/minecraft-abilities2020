package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class SoulDrain extends AbilityStyle {

    public SoulDrain() {
        super("Soul Drain", new String[]{"Gain back lost health",
                "when hitting glowing enemies"
        }, "coronacraft.skirmisher.souldrain", 723457);
    }

    /***
     * @param p the player activating the ability
     * @param args args[0] instanceof EntityDamageByEntityEvent the event
     * @return
     */
    public int execute(Player p, Object... args) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) args[0];
        if (((LivingEntity) e.getEntity()).getPotionEffect(PotionEffectType.GLOWING) != null) {
            double heal = Math.min(e.getFinalDamage(), 3.0);
            p.setHealth(Math.min(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), p.getHealth() + heal));
            e.getEntity().getLocation().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, Material.WHITE_WOOL);
        }
        return 0;
    }
}
