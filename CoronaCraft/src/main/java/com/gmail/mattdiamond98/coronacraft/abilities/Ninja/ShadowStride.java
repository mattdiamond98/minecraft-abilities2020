package com.gmail.mattdiamond98.coronacraft.abilities.Ninja;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ShadowStride extends UltimateAbility {

    public static final int DURATION_SECONDS = 30;
    public static final int DURATION_COOLDOWN_TICKS = DURATION_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;

    public ShadowStride() {
        super("Shadow Step");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_MINECRAFT_TICKS, 4));
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.NINJA) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.NINJA) {
            e.getPlayer().getWorld().spawnParticle(Particle.SQUID_INK, e.getPlayer().getLocation(), 1);
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION_COOLDOWN_TICKS;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.NINJA) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

}
