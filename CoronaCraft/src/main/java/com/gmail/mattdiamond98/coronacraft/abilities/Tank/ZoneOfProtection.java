package com.gmail.mattdiamond98.coronacraft.abilities.Tank;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneOfProtection extends UltimateAbility {

    public static final int RADIUS = 10;
    public static final int DURATION_SECONDS = 30;
    public static final int DURATION_COOLDOWN_TICKS = DURATION_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public ZoneOfProtection() {
        super("Zone of Protection");
    }

    private HashMap<UUID, Zone> playerZones = new HashMap<>();

    private class Zone {
        Location center;
        Location[] sphere;
        Zone(Location center, Location[] sphere) {
            this.center = center;
            this.sphere = sphere;
        }
    }

    @Override
    public void activate(Player player) {
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        List<Location> sphere = AbilityUtil.advancedCircle(player.getLocation(),
                RADIUS, RADIUS, true, true, 0);
        sphere = sphere.stream().filter(location -> location.getBlock().getType().isSolid()).collect(Collectors.toList());
        playerZones.put(player.getUniqueId(), new Zone(player.getLocation(), sphere.toArray(new Location[sphere.size()])));
    }

    private static void particleEffect(Location location) {
        if (location != null)
        location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 5);
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.TANK) {
            Zone zone = playerZones.get(e.getPlayer().getUniqueId());
            if (zone == null) return;
            Arrays.stream(zone.sphere).forEach(ZoneOfProtection::particleEffect);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.TANK) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
            playerZones.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.TANK) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }
}
