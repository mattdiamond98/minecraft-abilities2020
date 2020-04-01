package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.CoolDownKey;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        Map<CoolDownKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
        if (player.getInventory().contains(item)) {
            int coolDown = coolDowns.get(new CoolDownKey(player, item));
            setStackCount(player, item, coolDown + 1 / ABILITY_TICK_PER_SECOND);
        }
    }

    public static final boolean notInSpawn(Player p) {
        Team team = Team.getTeamByPlayerName(p.getName());
        if (team == null) return false;
        return !team.isSpawnLocation(p.getLocation());
    }

}
