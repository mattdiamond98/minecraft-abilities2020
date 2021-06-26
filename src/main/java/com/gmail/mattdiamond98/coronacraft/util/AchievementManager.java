package com.gmail.mattdiamond98.coronacraft.util;

import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import me.glaremasters.multieconomy.api.API;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.awt.*;

import static com.gmail.mattdiamond98.coronacraft.util.Achievement.permHeader;

public class AchievementManager implements Listener {

    @EventHandler
    public void OnGrassHoe(PlayerInteractEvent e){
        String name="Farmhand";
        if(!e.getPlayer().hasPermission(permHeader+name.toLowerCase().replaceAll(" ", ""))&&e.getClickedBlock()!=null&&e.getBlockFace()== BlockFace.UP && e.getClickedBlock().getType()== Material.GRASS_BLOCK&&e.getItem()!=null&& e.getItem().getType().name().toLowerCase().contains("hoe")&&!e.getItem().getType().equals(Material.NETHERITE_HOE)&& Warzone.getZoneByLocation(e.getPlayer())!=null &&Warzone.getZoneByLocation(e.getPlayer()).getName().equals("Castles")&&Warzone.getZoneByLocation(e.getPlayer()).getPlayers().contains(e.getPlayer())&&Warzone.getZoneByName("Castles").getVolume().getBlocks().stream().filter(bs -> bs.getType().equals(Material.FARMLAND)).count()>=3854){
        giveAchievement(e.getPlayer(), new Achievement(name, "Hoe all the grass blocks on Castles", 8));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerDeath(WarPlayerDeathEvent e){
        if(e.getKiller() != null&&e.getKiller() instanceof Player){
            Player killer=(Player) e.getKiller();
            Achievement tenkills=new Achievement("Casual murderer","Get 10 Kills", 1);
            Achievement hundredkills=new Achievement("Wanted for murder","Get 100 Kills", 5);
            Achievement fivehundredkills=new Achievement("Serial Killer","Get 500 Kills", 7);
            Achievement thousandkills=new Achievement("Mass murderer in training","Get 1000 Kills", 10);
            Achievement fivethousandkills=new Achievement("Mass murderer","Get 5000 Kills", 15);
            Achievement tenthousandkills=new Achievement("War God in Training","Get 10000 Kills", 20);
            Achievement fiftythousandkills=new Achievement("War God","Get 500000 Kills", 25);
            Achievement hundredthousandkills=new Achievement("Death God","Get 100000 Kills", 30);
            if(Leaderboard.getKills(killer)>=10 && !killer.hasPermission(tenkills.permission)){
                giveAchievement(killer, tenkills);
            }
            if(Leaderboard.getKills(killer)>=100 && !killer.hasPermission(hundredkills.permission)){
                giveAchievement(killer, hundredkills);
            }
            if(Leaderboard.getKills(killer)>=500 && !killer.hasPermission(fivehundredkills.permission)){
                giveAchievement(killer, fivehundredkills);
            }
            if(Leaderboard.getKills(killer)>=1000 && !killer.hasPermission(thousandkills.permission)){
                giveAchievement(killer, thousandkills);
            }
            if(Leaderboard.getKills(killer)>=5000 && !killer.hasPermission(fivethousandkills.permission)){
                giveAchievement(killer, fivethousandkills);
            }
            if(Leaderboard.getKills(killer)>=10000 && !killer.hasPermission(tenthousandkills.permission)){
                giveAchievement(killer, tenthousandkills);
            }
            if(Leaderboard.getKills(killer)>=50000 && !killer.hasPermission(fiftythousandkills.permission)){
                giveAchievement(killer, fiftythousandkills);
            }
            if(Leaderboard.getKills(killer)>=100000 && !killer.hasPermission(hundredthousandkills.permission)){
                giveAchievement(killer, hundredthousandkills);
            }
        }
        Player dead=e.getVictim();
        Achievement hundreddeaths=new Achievement("Weak","Die 100 Times", 1);
        Achievement fivehundreddeaths=new Achievement("Dead","Die 500 Times", 4);
        Achievement thousanddeaths = new Achievement("Very Dead","Die 1000 Times", 6);
        Achievement fivethousanddeaths=new Achievement("Even More Dead","Die 5000 Times", 9);
        Achievement tenthousanddeaths=new Achievement("Suicidal","Die 10000 Times", 13);
        Achievement fiftythousanddeaths=new Achievement("Friend of Death","Die 500000 Times", 17);
        Achievement hundredthousanddeaths=new Achievement("Family of Death","Die 100000 Times", 20);

        if(Leaderboard.getKills(dead)>=100 && !dead.hasPermission(hundreddeaths.permission)){
            giveAchievement(dead, hundreddeaths);
        }
        if(Leaderboard.getKills(dead)>=500 && !dead.hasPermission(fivehundreddeaths.permission)){
            giveAchievement(dead, fivehundreddeaths);
        }
        if(Leaderboard.getKills(dead)>=1000 && !dead.hasPermission(thousanddeaths.permission)){
            giveAchievement(dead, thousanddeaths);
        }
        if(Leaderboard.getKills(dead)>=5000 && !dead.hasPermission(fivethousanddeaths.permission)){
            giveAchievement(dead, fivethousanddeaths);
        }
        if(Leaderboard.getKills(dead)>=10000 && !dead.hasPermission(tenthousanddeaths.permission)){
            giveAchievement(dead, tenthousanddeaths);
        }
        if(Leaderboard.getKills(dead)>=50000 && !dead.hasPermission(fiftythousanddeaths.permission)){
            giveAchievement(dead, fiftythousanddeaths);
        }
        if(Leaderboard.getKills(dead)>=100000 && !dead.hasPermission(fiftythousanddeaths.permission)){
            giveAchievement(dead, hundredthousanddeaths);
        }


    }

    public static void giveAchievement(Player p, Achievement a){
        p.sendMessage(ChatColor.GREEN+"------------------------------------------------------------------------------------");
        TextComponent message = new TextComponent("Achievement Gained:: ");
        message.setColor(ChatColor.GREEN.asBungee());
        TextComponent interactiveBit = new TextComponent(a.name);
        interactiveBit.setColor(ChatColor.GOLD.asBungee());
        interactiveBit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                a.description).create()));
        message.addExtra(interactiveBit);
        p.spigot().sendMessage(message);
        p.sendMessage(ChatColor.GREEN+"------------------------------------------------------------------------------------");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user "+p.getName()+" permission set "+a.permission+" true");
        API.setAmount(p.getUniqueId().toString(), "achvpoints", Integer.parseInt(API.getAmount(p.getUniqueId().toString(), "achvpoints"))+a.pointvalue);

    }
}
