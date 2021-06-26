package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.UUID;

public class ReaperCooldownTracker {
    private static LinkedList<UUID> cooldownList = new LinkedList<>();

    public static boolean isOnCooldown(Player player) {
        return cooldownList.contains(player.getUniqueId());
    }

    public static void setCooldown(Player player, HoeStyle style) {
        if (style.cooldownTicks <= 0) { return; }
        cooldownList.add(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
            cooldownList.remove(player.getUniqueId());
        }, style.cooldownTicks);
    }

    public static void setCooldown(Player player, int ticks) {
        cooldownList.add(player.getUniqueId());
        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
            cooldownList.remove(player.getUniqueId());
        }, ticks);
    }
}
