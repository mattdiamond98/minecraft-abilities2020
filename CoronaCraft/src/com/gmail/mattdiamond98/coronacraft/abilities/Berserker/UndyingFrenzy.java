package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UndyingFrenzy extends UltimateAbility {

    public static final int DURATION_SECONDS = 30;
    public static final int DURATION_COOLDOWN_TICKS = DURATION_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int DURATION_MINECRAFT_TICKS = DURATION_SECONDS * 20;

    public UndyingFrenzy() {
        super("Undying Frenzy");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION_COOLDOWN_TICKS);
        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
        } else {
            player.getInventory().addItem(new ItemStack(Material.TOTEM_OF_UNDYING));
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, DURATION_MINECRAFT_TICKS, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, DURATION_MINECRAFT_TICKS, 0));
    }

    @EventHandler
    public void onPlayerResurrect(EntityResurrectEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (UltimateTracker.isUltimateActive(p) && UltimateTracker.getLoadout(p) == Loadout.BERSERKER) {
                p.setHealth(10);
                UltimateTracker.removeProgress(p);
            }
        }
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.BERSERKER) {
            e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getPlayer().getLocation(), 1, Bukkit.createBlockData(Material.REDSTONE_WIRE));
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.BERSERKER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.BERSERKER) {
            if (e.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
                e.getPlayer().getInventory().setItemInOffHand(null);
            } else {
                for (ItemStack stack : e.getPlayer().getInventory()) {
                    if (stack != null && stack.getType() == Material.TOTEM_OF_UNDYING) stack.setType(Material.AIR);
                }
            }
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }
}
