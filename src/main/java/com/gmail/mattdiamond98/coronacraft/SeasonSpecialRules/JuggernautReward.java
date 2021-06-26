package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.event.CoronaCraftTickEvent;
import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class JuggernautReward extends SpecialRule {

    public JuggernautReward() {
        super("Juggernaut Reward");
    }
    public void OnPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e){
       if(e.getPlayer().hasPermission("tourney.reward.juggernaut")&&!War.war.isWarAdmin(e.getPlayer())){
           for(Player pl:Team.getTeamByPlayerName(e.getPlayer().getName()).getPlayers()){
            if(pl!=e.getPlayer()&& (Loadout.getLoadout(pl)==Loadout.TANK||Loadout.getLoadout(pl)==Loadout.HEALER)){
                e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()+4);
            }
           }

       }
    }
}
