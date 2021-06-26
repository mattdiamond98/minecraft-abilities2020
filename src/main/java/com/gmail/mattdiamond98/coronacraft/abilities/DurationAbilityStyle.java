package com.gmail.mattdiamond98.coronacraft.abilities;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public interface DurationAbilityStyle {
    BukkitRunnable getRunnable(final Player p);
}
