package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer.PyromancerSpellbook;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class MeteorRain extends UltimateAbility {

    public MeteorRain() {
        super("Meteor Rain");
    }

    public static final int DURATION = 8;
    public static final int DURATION_COOLDOWN_TICKS = DURATION * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION * 20;

    private Random rand  = new Random();

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
    }

    @EventHandler
    public void onCooldownTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR
                && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof GeomancerSpellbook) {
            Location target = e.getPlayer().getTargetBlock(AbilityUtil.transparent, 30).getRelative(0, 30, 0).getLocation();
            for (int i = 0; i < 2; i++) {
                Catapult.spawnCatapultProjectile(
                        e.getPlayer(),
                        target.clone().add(new Vector(rand.nextFloat() * 10 - 5, 0, rand.nextFloat() * 10 - 5)).getBlock(),
                        Material.MAGMA_BLOCK, new Vector(0, -1, 0));
            }
            target.getWorld().playEffect(target, Effect.STEP_SOUND, Material.MAGMA_BLOCK);
            target.getWorld().playSound(target, Sound.ENTITY_HOSTILE_BIG_FALL, 2.0F, 0.1F);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof GeomancerSpellbook) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getVictim()) instanceof GeomancerSpellbook) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.WIZARD
                && CoronaCraft.getAbility(Material.BLAZE_ROD).getStyle(e.getPlayer()) instanceof GeomancerSpellbook) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION_COOLDOWN_TICKS;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }
}
