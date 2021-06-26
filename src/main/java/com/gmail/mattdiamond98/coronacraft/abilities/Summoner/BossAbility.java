package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BossAbility extends UltimateAbility {
    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isSolid).negate()).collect(Collectors.toSet());
    public static final int DURATION = 20 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public BossAbility() {
        super("Spawn Boss");
    }
    /*@Override
    public String getName(Player p){
        switch(((SummonerBiome) CoronaCraft.getAbility(Material.GRASS_BLOCK).getStyle(p)).getName().toLowerCase()){
            case "cave":
                return "Giant Slime";
        }
       return null;
    }*/
    @Override
    public void activate(Player p){


        ((SummonerBiome) CoronaCraft.getAbility(Material.GRASS_BLOCK).getStyle(p)).SpawnBoss(((CraftWorld)p.getWorld()).getHandle(), p,p.getLocation(), Team.getTeamByPlayerName(p.getName()));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 2, false, false));
        CoronaCraft.setCooldown(p, Material.NETHER_STAR, DURATION);
    }
    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.SUMMONER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / 1;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.SUMMONER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.SUMMONER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }

}

