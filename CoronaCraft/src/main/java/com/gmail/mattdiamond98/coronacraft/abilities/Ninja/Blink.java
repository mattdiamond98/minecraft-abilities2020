package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Blink extends AbilityStyle {

    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isSolid).negate()).collect(Collectors.toSet());

    public static final int COOL_DOWN = 10 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public Blink() {
        super("Blink", new String[]{"Teleport a short distance away"}, "coronacraft.ninja.blink");
    }

    public int execute(Player p, Object... args) {
        Block target = p.getTargetBlock(transparent, 15);
        if (target.getType().equals(Material.AIR)) return 0;
        Block destination = target.getRelative(0, 1, 0);
        if (!destination.getType().isSolid()) {
            p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
            Location destinationLoc = destination.getLocation().add(new Vector(0.5, 0, 0.5));
            destinationLoc.setDirection(p.getLocation().getDirection());
            p.teleport(destinationLoc);
            destination.getWorld().playSound(destination.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
            destination.getWorld().playEffect(destination.getLocation(), Effect.SMOKE, 10);
            return COOL_DOWN;
        }
        else return 0;
    }

}
