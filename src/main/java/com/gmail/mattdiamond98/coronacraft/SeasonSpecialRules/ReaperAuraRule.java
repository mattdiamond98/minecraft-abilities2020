package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.comphenix.protocol.PacketType;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ReaperAuraRule extends SpecialRule {
    public ReaperAuraRule() {
        super("Reaper Aura", new BukkitRunnable() {
            @Override
            public void run() {
                for(Warzone w: War.war.getActiveWarzones()){
                    for(Player p:w.getPlayers()){
                        if(!AbilityUtil.inSpawn(p)){
                           if(Loadout.getLoadout(p).equals(Loadout.REAPER)){
                               for(Entity e:p.getNearbyEntities(5, 5,5)){
                                   if(e instanceof Player&& Team.getTeamByPlayerName(((Player) e).getName())!=null&&!Team.getTeamByPlayerName(((Player)e).getName()).equals(Team.getTeamByPlayerName(p.getName()))&&AbilityUtil.notInSpawn((Player) e)){
                                       Loadout l=Loadout.getLoadout((Player) e);
                                       if(l.equals(Loadout.BERSERKER)||l.equals(Loadout.SKIRMISHER)||l.equals(Loadout.GLADIATOR)||l.equals(Loadout.FIGHTER)){
                                           ((Player)e).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1, 0, false, false));
                                       }
                                   }
                               }
                           }
                        }
                    }
                }
            }
        }, 1);
    }
}
