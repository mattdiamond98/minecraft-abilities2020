package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Omnislash extends UltimateAbility {

    public static final int DURATION = 30 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final Random rand = new Random();

    public Omnislash() {
        super("Omnislash");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (DURATION / CoronaCraft.ABILITY_TICK_PER_SECOND) * 20, 11, true, true));
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.FIGHTER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.FIGHTER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.FIGHTER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            if (UltimateTracker.isUltimateActive(player, new Omnislash())
                    && UltimateTracker.getLoadout(player) == Loadout.FIGHTER
                    && player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
                Player target = (Player) e.getEntity();
                Team playerTeam = Team.getTeamByPlayerName(player.getName());
                Team targetTeam = Team.getTeamByPlayerName(target.getName());
                if (playerTeam != null && targetTeam != null && !playerTeam.equals(targetTeam)) {
                    List<Player> enemies = AbilityUtil.getEnemies(player).stream()
                            .filter(enemy -> enemy.getLocation().distanceSquared(player.getLocation()) < 100)
                            .collect(Collectors.toList());
                    if (target.getHealth() - e.getFinalDamage() <= 0) enemies.remove(target);
                    if (enemies.size() > 0) {
                        player.teleport(getRandomValidLocation(player, enemies, 12));
                    }
                }
            }

        }else if(e.getDamager() instanceof Player &&e.getEntity() instanceof LivingEntity){
            Player player = (Player) e.getDamager();
            if (UltimateTracker.isUltimateActive(player, new Omnislash())
                    && UltimateTracker.getLoadout(player) == Loadout.FIGHTER
                    && player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
                LivingEntity target = (LivingEntity) e.getEntity();
                Team playerTeam = Team.getTeamByPlayerName(player.getName());
                Team targetTeam = Team.getNonPlayerEntityTeam(target);
                if (playerTeam != null && targetTeam != null && !playerTeam.equals(targetTeam)) {
                    List<Player> enemies = AbilityUtil.getEnemies(player).stream()
                            .filter(enemy -> enemy.getLocation().distanceSquared(player.getLocation()) < 100)
                            .collect(Collectors.toList());
                    if (target.getHealth() - e.getFinalDamage() <= 0) enemies.remove(target);
                    if (enemies.size() > 0) {
                        player.teleport(getRandomValidLocation(player, enemies, 12));
                    }
                }
            }
        }
    }

    private static Location getRandomValidLocation(Player player, List<Player> nearbyEnemies, int tries) {
        Player enemy = nearbyEnemies.get(rand.nextInt(nearbyEnemies.size()));
        double degree = rand.nextDouble() * 2 * Math.PI;
        return getRandomValidLocation(player, enemy, degree, tries);
    }

    /***
     * Chooses a random nearby enemy to teleport nearby, looking toward where the player will be
     */
    private static Location getRandomValidLocation(Player player, Player enemy, double degree, int tries) {
        Location possibleLocation = enemy.getLocation().clone().add(Math.cos(degree) * 2, 0.5, Math.sin(degree) * 2);
        possibleLocation = getValidPlace(possibleLocation);
        if (possibleLocation == null) {
            if (tries > 0) return getRandomValidLocation(player, enemy, degree + Math.toRadians(30), tries - 1);
            return player.getLocation();
        }
        Vector direction = enemy.getEyeLocation().add(enemy.getVelocity()).toVector()
                .subtract(possibleLocation.toVector().add(new Vector(0, 1, 0)))
                .normalize().multiply(-1);
        double x = direction.getX();
        double y = direction.getY();
        double z = direction.getZ();
        possibleLocation.setYaw(180 - ((float) Math.toDegrees(Math.atan2(x, z))));
        possibleLocation.setPitch(90 - ((float) Math.toDegrees(Math.acos(y))));
        enemy.setNoDamageTicks(0);
        return possibleLocation;
    }

    private static Location getValidPlace(Location location) {
        Location valid = null;
        if (location.getBlock().getType() == Material.AIR) {
            // Search down until find solid
            for (int i = 0; i < 2; i++) {
                if (location.add(0, -i, 0).getBlock().getType().isSolid()) {
                    valid = location.add(0, 1 - i, 0);
                    break;
                }
            }
        } else {
            // Search up until find air
            for (int i = 0; i < 2; i++) {
                if (!location.add(0, i, 0).getBlock().getType().isSolid()) {
                    location.add(0, i, 0);
                    break;
                }
            }
        }
        if (valid == null) return null;
        if (valid.add(0, 1, 0).getBlock().getType().isSolid()) return null;
        return valid;
    }

}
