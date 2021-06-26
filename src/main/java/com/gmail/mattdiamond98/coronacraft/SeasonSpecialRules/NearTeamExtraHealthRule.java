package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NearTeamExtraHealthRule extends SpecialRule {
    public NearTeamExtraHealthRule() {
        super("Near Team Extra Health", new BukkitRunnable() {
            @Override
            public void run() {
                for(Warzone w:War.war.getWarzones()){
                    for(Team t:w.getTeams()){
                        for(Player p:t.getPlayers()){
                           boolean addHealth=t.getPlayers().size()>=2;
                            for(Player pl:t.getPlayers()){
                                    if(pl.equals(p)){
                                        continue;
                                    }
                                    if(pl.getLocation().distanceSquared(p.getLocation())>2){
                                        addHealth=false;
                                    }

                            }
                            if ((AbilityUtil.notInSpawn(p))&&addHealth&&!(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()>20||(Loadout.getLoadout(p).name().toLowerCase().contains("berserker")&&p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()>24))) {

                                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()+4);
                            }
                            }
                        }

                    }
                }

        }, 1);
    }




}
