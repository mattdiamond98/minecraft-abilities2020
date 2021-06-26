package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class BatKillEffect extends KillEffect {
    public BatKillEffect() {
        super("Spawn Bat");
    }

    @Override
    public void runEffect(Location loc) {
    final Entity e=loc.getWorld().spawnEntity(loc, EntityType.BAT);
    BukkitRunnable br=new BukkitRunnable() {
        @Override
        public void run() {
            e.remove();
        }
    };
    br.runTaskLater(CoronaCraft.instance, 100L);
    }
}
