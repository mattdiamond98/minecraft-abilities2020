package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TNTTrail extends UltimateAbility {

    public static final int DURATION = 20 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public TNTTrail() {
        super("TNT Trail");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION / CoronaCraft.ABILITY_TICK_PER_SECOND * 20, 1));
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) == null) {
            UltimateTracker.removeProgress(e.getPlayer());
        }
        else if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.ANARCHIST) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
            e.getPlayer().getWorld().playEffect(e.getPlayer().getLocation(), Effect.SMOKE, 10);
            TNTPrimed tnt = e.getPlayer().getWorld().spawn(e.getPlayer().getLocation(), TNTPrimed.class);
            tnt.setFuseTicks(50);
            tnt.setYield(4);
            tnt.setMetadata(MetadataKey.PLAYER, new FixedMetadataValue(CoronaCraft.instance, e.getPlayer()));
            tnt.setMetadata(MetadataKey.BREAK_BLOCKS, new FixedMetadataValue(CoronaCraft.instance, false)); // false doesn't matter right now
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity().hasMetadata(MetadataKey.BREAK_BLOCKS)) {
            e.blockList().removeIf(block -> block.getType() != Material.TNT);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.ANARCHIST) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.ANARCHIST) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && e.getEntity() instanceof Player
                && e.getDamager() instanceof TNTPrimed
                && e.getDamager().hasMetadata(MetadataKey.PLAYER)) {
            Player player = (Player) e.getEntity();
            TNTPrimed tnt = (TNTPrimed) e.getDamager();
            Player activator = (Player) AbilityUtil.getMetadata(tnt, MetadataKey.PLAYER);
            if (activator == null || Team.getTeamByPlayerName(activator.getName()) == null) {
                e.setDamage(0);
                return;
            }
            Team team = Team.getTeamByPlayerName(activator.getName());
            if (team.getPlayers().contains(player)) {
                e.setDamage(0);
            }
        }
    }
}
