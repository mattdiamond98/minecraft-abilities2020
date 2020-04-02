package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.CoronaCraft.ABILITY_TICK_PER_SECOND;

public final class AbilityUtil {

    public static final void setStackCount(Player player, Material item, int count) {
        if (player.getInventory().getItemInOffHand().getType().equals(item)) {
            player.getInventory().getItemInOffHand().setAmount(count);
        } else {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType().equals(item)) {
                    itemStack.setAmount(count);
                }
            }
        }
    }

    public static final int getTotalCount(Player player, Material item) {
        int total_count = player.getInventory().all(item).values()
                .stream().map(x -> ((ItemStack) x).getAmount()).reduce(0, Integer::sum);
        if (player.getInventory().getItemInOffHand().getType() == item) {
            total_count += player.getInventory().getItemInOffHand().getAmount();
        }
        return total_count;
    }

    public static final void setItemStackToCooldown(Player player, Material item) {
        Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
        if (player.getInventory().contains(item)) {
            int coolDown = coolDowns.get(new AbilityKey(player, item));
            setStackCount(player, item, coolDown + 1 / ABILITY_TICK_PER_SECOND);
        }
    }

    public static final boolean notInSpawn(Player p) {
        Team team = Team.getTeamByPlayerName(p.getName());
        if (team == null) return false;
        return !team.isSpawnLocation(p.getLocation());
    }

    public static final void toggleAbilityStyle(Player p, Material item) {
        Ability ability = CoronaCraft.getAbility(item);
        AbilityStyle currentStyle = ability.getStyle(p);
        int nextPosition = ability.getNextStylePosition(p);
        AbilityStyle nextStyle = ability.getStyle(nextPosition);
        if (!nextStyle.equals(currentStyle)) {
            CoronaCraft.getPlayerAbilities().put(new AbilityKey(p, item), nextPosition);
            p.sendMessage(ChatColor.AQUA + nextStyle.getName());
            for (String line : nextStyle.getDescription()) {
                p.sendMessage(ChatColor.GRAY + line);
            }
        }
    }

    public static final void notifyAbilityOnCooldown(Player p, Ability a) {
        p.sendMessage(ChatColor.RED + "Your " + a.getName() + " ability is on cooldown.");
    }

    public static final ArrayList<Location> getCircle(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2*Math.PI)/amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++){
            double angle = i*increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public static final List<Vector> unitVectors() {
        return Arrays.asList(new Vector[]{
                new Vector(1, 0, 0),
                new Vector(0, 1, 0),
                new Vector(0, 0, 1),
                new Vector(-1, 0, 0),
                new Vector(0, -1, 0),
                new Vector(0, 0, -1)
        });
    }

}
