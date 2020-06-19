package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TigerPounce extends AbilityStyle {

    public static final int DURATION_SECONDS = 5;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;
    public static final int DURATION_COOLDOWN_TICKS = 10 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int COOLDOWN = 30 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public TigerPounce() {
        super("Tiger Pounce", new String[] {
                "Jump high up, and deal true damage to",
                "enemies you hit while falling."
        }, "coronacraft.fighter.tigerpounce");
    }

    @Override
    public int execute(Player p, Object... args) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, DURATION_MINECRAFT_TICKS, 4, true, true, true));
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_HORSE_GALLOP, 0.7F, 0.3F);
        Rush.RUSHING_PLAYERS.add(p.getUniqueId());
        return DURATION_COOLDOWN_TICKS;
    }
}
