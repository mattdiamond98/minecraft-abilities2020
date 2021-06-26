package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.InstantFirework;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CelestialCrusade extends AbilityStyle {
    public CelestialCrusade() {
        super("Celestial Crusade", new String[]{"Sends a pulse of Strength and Speed I for 10s", "for all allies within a 7 block radius", "25 sec cooldown."}, "coronacraft.healer.celestialcrusade", 0);

    }

    @Override
    public int execute(Player player, Object... data) {
        FireworkEffect fireworkEffect = FireworkEffect.builder().flicker(false).trail(true)
                .with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).flicker(true).build();
        for (Location loc : AbilityUtil.getCircle(player.getLocation().add(0, 1, 0), 7, 10)) {
            loc.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 1);
        }
        for (Entity teammate : Team.getTeamByPlayerName(player.getName()).getPlayers()) {
            if(teammate instanceof Player && Loadout.getLoadout((Player)teammate).equals(Loadout.REAPER)){
                continue;
            }
            if (teammate.getLocation().distanceSquared(player.getLocation()) < 7 * 7&&!teammate.equals(player)) {
                new PotionEffect(PotionEffectType.SPEED, 200, 0).apply((LivingEntity) teammate);
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE,200, 0).apply((LivingEntity) teammate);
                teammate.getWorld().playEffect(teammate.getLocation(), Effect.STEP_SOUND, Material.SEA_LANTERN);
                teammate.getWorld().playSound(teammate.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                teammate.sendMessage(ChatColor.GREEN + "You have been buffed");
            }
        }

        for (Entity teammate : Team.getTeamByPlayerName(player.getName()).getNonPlayerEntities()) {
            if (teammate.getLocation().distanceSquared(player.getLocation()) < 7 * 7&&!teammate.equals(player)) {
                new PotionEffect(PotionEffectType.SPEED, 200, 0).apply((LivingEntity) teammate);
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE,200, 0).apply((LivingEntity) teammate);
                teammate.getWorld().playEffect(teammate.getLocation(), Effect.STEP_SOUND, Material.SEA_LANTERN);
                teammate.getWorld().playSound(teammate.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                teammate.sendMessage(ChatColor.GREEN + "You have been buffed");
            }
        }

        return 25* CoronaCraft.ABILITY_TICK_PER_SECOND;
    }}
