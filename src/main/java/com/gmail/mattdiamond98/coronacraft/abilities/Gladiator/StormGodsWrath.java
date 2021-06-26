package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.*;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StormGodsWrath extends UltimateAbility {

    public static final int DURATION_SECONDS = 22;
    public static final int DURATION_COOLDOWN_TICKS = DURATION_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;

    public StormGodsWrath() {
        super("Storm God's Wrath");
    }

    public static ProjectileAbilityStyle style = new ProjectileAbilityStyle("Storm Strike", new String[]{
            "Smite your enemies with",
            "the power of the Storm God."
    }, 0) {
        @Override
        public int onShoot(Projectile p) {
            p.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, this));
            return 1;
        }

        @Override
        public int execute(Player player, Object... args) {
            ProjectileHitEvent event = (ProjectileHitEvent) args[0];
            LightningStrike strike = event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
            strike.setMetadata(MetadataKey.PLAYER, new FixedMetadataValue(CoronaCraft.instance, player));
            event.getEntity().remove();
            return 0;
        }
    };

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        if (UltimateTracker.isUltimateActive(e.getPlayer()) && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.GLADIATOR) {
            ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
            if (item == null) return;
            if (item.getType() == Material.TRIDENT) {
                formatTrident(item);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LightningStrike && e.getEntity() instanceof Player) {
            if (e.getDamager().hasMetadata(MetadataKey.PLAYER)) {
                Player damager = (Player) AbilityUtil.getMetadata(e.getDamager(), MetadataKey.PLAYER);
                Player player = (Player) e.getEntity();
                if (damager != null) {
                    Team team1 = Team.getTeamByPlayerName(player.getName());
                    Team team2 = Team.getTeamByPlayerName(damager.getName());
                    if (team1 == null || team2 == null || !team1.getZone().equals(team2.getZone()) || team1.equals(team2)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }


    public static ItemStack formatTrident(ItemStack item) {
        if (item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && !item.getItemMeta().getDisplayName().equals(AbilityUtil.formatStyleName(style))) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(AbilityUtil.formatStyleName(style));
            meta.setLore(Arrays.stream(style.getDescription()).map(AbilityUtil::formatLoreLine).collect(Collectors.toList()));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_MINECRAFT_TICKS, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, DURATION_MINECRAFT_TICKS + 60, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, DURATION_MINECRAFT_TICKS, 7));
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.GLADIATOR) {
            e.getPlayer().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, e.getPlayer().getLocation(), 1);
            if (e.getPlayer().isSneaking()) {
                if (e.getPlayer().getVelocity().getY() < 0 || e.getPlayer().hasPotionEffect(PotionEffectType.LEVITATION))
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0, true));
            }
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION_COOLDOWN_TICKS;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.GLADIATOR) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.GLADIATOR) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
            for (ItemStack item : e.getPlayer().getInventory()) {
                if (item != null && item.getType() == Material.TRIDENT) {
                    AbilityUtil.formatItem(e.getPlayer(), item);
                }
            }
        }
    }
}
