package com.gmail.mattdiamond98.coronacraft.abilities.Ranger;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowStorm extends UltimateAbility {

    private static final Random rand = new Random();

    public static final int DURATION = 30 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final double SPREAD = Math.toRadians(2.0);

    public ArrowStorm() {
        super("Arrow Storm");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.RANGER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.RANGER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.RANGER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    public static void arrowDuplicate(Player player, Arrow arrow, float force) {
        double velocity = arrow.getVelocity().length();
        for (double f : new double[]{-SPREAD * 2F, -SPREAD, SPREAD, SPREAD * 2F}) {
            Arrow newArrow = arrow.getWorld().spawnArrow(
                    arrow.getLocation().clone(),
                    rotateVector(arrow.getVelocity(), f),
                    force,
                    0F
            );
            if (arrow.hasCustomEffects()) {
                for (PotionEffect effect : arrow.getCustomEffects()) {
                    newArrow.addCustomEffect(effect, true);
                }
            }
            newArrow.setShooter(player);
            newArrow.setVelocity(newArrow.getVelocity().normalize().multiply(velocity));
        }
    }

    public static Vector rotateVector(Vector vector, double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
        return vector.setX(x).setZ(z);
    }

}
