package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DivineAura extends UltimateAbility {
    public static final int DURATION = 20 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public DivineAura() {
        super("Divine Aura");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
       /* BukkitRunnable b=new BukkitRunnable() {
            @Override
            public void run() {
                for(Entity e:player.getNearbyEntities(8, 8, 8)){
                    if(e instanceof Player&& Team.getTeamByPlayerName(e.getName()).equals(Team.getTeamByPlayerName(player.getName()))){
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                        ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                    }else if(Team.getNonPlayerEntityTeam(e).equals(Team.getTeamByPlayerName(player.getName()))){
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                    }
                }
              player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
               player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
               for(Location l: AbilityUtil.getCircle(player.getLocation(), 8, 20)){
             player.getWorld().spawnParticle(Particle.HEART, l, 1);


               }
            }
        };
        b.runTaskTimer(CoronaCraft.instance, 0L, 1L);
        BukkitRunnable b2=new BukkitRunnable() {
            @Override
            public void run() {
                b.cancel();
            }
        };
        b2.runTaskLater(CoronaCraft.instance, 400L);*/

    }
    @EventHandler
    public void onTick(CoolDownTickEvent event) {
        if (event.getPlayer() == null || !event.getPlayer().isOnline()) return;
        if (event.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(event.getPlayer()) == Loadout.HEALER) {
            float remaining = (event.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            event.getPlayer().setExp(remaining);
            Player player=event.getPlayer();

            if(event.getTicksRemaining()>=4){
            for(Entity e:player.getNearbyEntities(8, 8, 8)){
                if(e instanceof Player&& Team.getTeamByPlayerName(e.getName()).equals(Team.getTeamByPlayerName(player.getName()))){
                    ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                    ((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                }else if(Team.getNonPlayerEntityTeam(e)!=null&&Team.getNonPlayerEntityTeam(e).equals(Team.getTeamByPlayerName(player.getName()))){
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                }
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
            for(Location l: AbilityUtil.getCircle(player.getLocation(), 8, 20)){
                player.getWorld().spawnParticle(Particle.HEART, l, 1);


            }}

        }

    }
    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.HEALER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }
    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.HEALER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }
}
