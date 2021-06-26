package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CallCreatures extends AbilityStyle {
    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isSolid).negate()).collect(Collectors.toSet());
    public CallCreatures() {
        super("Call Creatures", new String[]{"Calls all of your", "creatures to where you are looking for 10 secs"}, 0);
    }

    @Override
    public int execute(Player player, Object... data) {
        return  ((SummonerBiome)CoronaCraft.getAbility(Material.GRASS_BLOCK).getStyle(player)).CallAllCreatures(player, player.getTargetBlock(transparent, 100).getLocation());
    }
}
