package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import com.tommytony.war.utility.LoadoutSelection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class OneReaperRule extends SpecialRule {
    public OneReaperRule() {
        super("One Reaper");
    }
    @EventHandler
    public void OnPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e){
        if(Team.getTeamByPlayerName(e.getPlayer().getName()).getPlayers().size()<=1) return;
        Warzone w=Warzone.getZoneByPlayerName(e.getPlayer().getName());
        boolean cancel= true;
        for(Player p: Team.getTeamByPlayerName(e.getPlayer().getName()).getPlayers()){
            if(AbilityUtil.notInSpawn(p)&&( Loadout.getLoadout(p).name().toLowerCase().contains("reaper")|| Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("reaper"))&&!p.equals(e.getPlayer())){
                cancel=false;

            }
            if(AbilityUtil.inSpawn(p)&&AbilityUtil.inSpawn(e.getPlayer())){
                cancel=false;

            }        }


        if(cancel){
            e.getPlayer().teleport(Team.getTeamByPlayerName(e.getPlayer().getName()).getRandomSpawn());
            if (!w.getLoadoutSelections().keySet().contains(e.getPlayer().getName())) {

                w.getLoadoutSelections().put(e.getPlayer().getName(), new LoadoutSelection(true, 0));
            }else{
                w.getLoadoutSelections().get(e.getPlayer().getName()).setStillInSpawn(true);
            }
            e.getPlayer().sendMessage(ChatColor.RED+"That class isn't allowed right now!");
        }
    }
}
