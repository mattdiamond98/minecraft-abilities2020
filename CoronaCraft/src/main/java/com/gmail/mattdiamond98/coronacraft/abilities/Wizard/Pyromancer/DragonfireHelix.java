package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ejml.simple.SimpleMatrix;

public class DragonfireHelix extends UltimateAbility {

    public static final double RADIUS = 10.0;

    public static final int STARTUP = 4;
    public static final int STARTUP_COOLDOWN_TICKS = STARTUP * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int STARTUP_MINECRAFT_TICKS = STARTUP * 20;

    private static final FireworkEffect BLUE_SMALL_BALL = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.BALL).withColor(Color.BLUE).build();
    private static final FireworkEffect RED_SMALL_BALL = FireworkEffect.builder().withTrail().with(FireworkEffect.Type.BALL).withColor(Color.RED).build();

    public DragonfireHelix() {
        super("Dragonfire Helix");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, STARTUP_MINECRAFT_TICKS, 9));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, STARTUP_MINECRAFT_TICKS, 3));
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, STARTUP_COOLDOWN_TICKS);
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR
                && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof PyromancerSpellbook) {
            float remaining = 1.0F - (e.getTicksRemaining() * 1.0F) / STARTUP;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, remaining, remaining);
            double degrees = remaining * Math.PI * 2;
            e.getPlayer().getWorld().playEffect(e.getPlayer().getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 5);
            new InstantFirework(RED_SMALL_BALL, e.getPlayer().getEyeLocation().add(RADIUS * Math.cos(degrees), 0, RADIUS * Math.sin(degrees)));
            new InstantFirework(BLUE_SMALL_BALL, e.getPlayer().getEyeLocation().add(RADIUS * Math.sin(degrees), 0, RADIUS * Math.cos(degrees)));
        }
    }


    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getVictim()) instanceof PyromancerSpellbook) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onFire(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof PyromancerSpellbook) {
            if (AbilityUtil.notInSpawn(e.getPlayer())) {
                Location startLoc = e.getPlayer().getEyeLocation();
                Vector direction = e.getPlayer().getLocation().getDirection().normalize();
                if (direction.equals(new Vector(-1, 0, 0)))
                    direction = new Vector(-1.001, 0, 0); // Ensure vectors aren't opposite
                Vector mx = new Vector(1, 0, 0);
                Vector mz = mx.getCrossProduct(direction).normalize();
                Vector my = mz.getCrossProduct(mx).normalize();
                SimpleMatrix rot = new SimpleMatrix(new double[][]{
                        {mx.getX(), mx.getY(), mx.getZ()},
                        {mz.getX(), mz.getY(), mz.getZ()},
                        {my.getX(), my.getY(), my.getZ()}
                }
                );
                for (int i = 0; i < 60; i++) {
                    final int fi = i;
                    final Vector fdirection = direction.clone();
                    SimpleMatrix orthogonal = new SimpleMatrix(new double[][]{
                            {0.0},
                            {Math.cos(((double) i) / 3.0)},
                            {Math.sin((double) i) / 3.0}
                    });
                    SimpleMatrix result = rot.mult(orthogonal);
                    final Vector offset = new Vector(result.get(0), result.get(1), result.get(2)).multiply(3);
                    Location midpoint = startLoc.add(fdirection.clone().multiply(fi));
                    Location loc1 = midpoint.clone().add(offset);
                    Location loc2 = midpoint.clone().subtract(offset);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        new InstantFirework(RED_SMALL_BALL, loc1);
                        new InstantFirework(BLUE_SMALL_BALL, loc2);
                        e.getPlayer().playSound(midpoint, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5F, 0.5F);
                        e.getPlayer().getWorld().createExplosion(loc1, 2.75F, true, false, e.getPlayer());
                        e.getPlayer().getWorld().createExplosion(loc2, 2.75F, true, false, e.getPlayer());
                    }, i * 3);
                }
            }
            UltimateTracker.removeProgress(e.getPlayer());
        }
    }
}
