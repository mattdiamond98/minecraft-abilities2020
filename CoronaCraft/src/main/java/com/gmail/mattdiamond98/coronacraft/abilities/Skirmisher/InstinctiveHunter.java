package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

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
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InstinctiveHunter extends UltimateAbility {

    public static final int DURATION_SECONDS = 30;
    public static final int DURATION_COOLDOWN_TICKS = DURATION_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;

    public InstinctiveHunter() {
        super("Instinctive Hunter");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_MINECRAFT_TICKS, 1, true, true));
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.SKIRMISHER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION_COOLDOWN_TICKS;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.SKIRMISHER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.SKIRMISHER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player target = (Player) e.getEntity();
            Player player = null;
            if (e.getDamager() instanceof Player) {
                player = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                if (((Projectile) e.getDamager()).getShooter() instanceof Player)
                    player = (Player) ((Projectile) e.getDamager()).getShooter();
            }
            if (player == null) return;
            if (UltimateTracker.isUltimateActive(player)
                    && UltimateTracker.getLoadout(player) == Loadout.SKIRMISHER) {
                Team playerTeam = Team.getTeamByPlayerName(player.getName());
                Team targetTeam = Team.getTeamByPlayerName(target.getName());
                if (playerTeam != null && targetTeam != null && !playerTeam.equals(targetTeam)) {
                    if (player.getInventory().getItemInOffHand().getType() == Material.CROSSBOW) {
                        reloadCrossbow(player.getInventory().getItemInOffHand());
                    } else {
                        for (ItemStack item : player.getInventory()) {
                            reloadCrossbow(item);
                        }
                    }
                }
            } else if (UltimateTracker.isUltimateActive(target)
                    && UltimateTracker.getLoadout(target) == Loadout.SKIRMISHER
                    && player.hasPotionEffect(PotionEffectType.GLOWING)) {
                e.setDamage(e.getFinalDamage() / 2);
                target.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, target.getLocation(), 1);
            }
        }
    }

    private static void reloadCrossbow(ItemStack crossbow) {
        if (crossbow == null || crossbow.getType() != Material.CROSSBOW) return;
        CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
        if (!meta.hasChargedProjectiles()) meta.addChargedProjectile(new ItemStack(Material.SPECTRAL_ARROW, 1));
        crossbow.setItemMeta(meta);
    }
}
