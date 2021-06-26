package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;

public class Buddies extends UltimateAbility {
    public static final int DURATION = 25 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public ArrayList<BuddyCreeper> allBuddies=new ArrayList<>();
    public Buddies() {
        super("Buddies!", "coronacraft.ultimates.buddies");
    }
    @Override
    public void activate(Player player){
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR &&UltimateTracker.isUltimateActive(e.getPlayer(), this)) {
            //e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getPlayer().getLocation(), 1, Bukkit.createBlockData(Material.REDSTONE_WIRE));
            BuddyCreeper buddy=new BuddyCreeper(e.getPlayer().getLocation(), e.getPlayer(), Team.getTeamByPlayerName(e.getPlayer().getName()));
            ( (CraftWorld)e.getPlayer().getWorld()).getHandle().addEntity(buddy);
            allBuddies.add(buddy);
        }
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) == null) {
            UltimateTracker.removeProgress(e.getPlayer());
        }
        else if (e.getItem() == Material.NETHER_STAR && UltimateTracker.isUltimateActive(e.getPlayer(), this)) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);}
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.ANARCHIST) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.ANARCHIST&&UltimateTracker.isUltimateActive(e.getPlayer(), this)) {


            UltimateTracker.removeProgress(e.getPlayer());
            for(BuddyCreeper b:allBuddies){
                if(!b.getBukkitEntity().isDead()){
                b.getBukkitEntity().remove();}
            }
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }
}
