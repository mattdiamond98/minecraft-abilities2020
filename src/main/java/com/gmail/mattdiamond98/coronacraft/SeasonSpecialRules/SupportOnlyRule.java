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

public class SupportOnlyRule extends SpecialRule {
    public SupportOnlyRule() {
        super("Support Classes only");
    }
    @EventHandler
    public void OnPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e){
        Warzone w=Warzone.getZoneByPlayerName(e.getPlayer().getName());
        boolean cancel=AbilityUtil.notInSpawn(e.getPlayer())&&(Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("ninja")||Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("anarchist")||Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("engineer")||Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("ranger"));
        for(Player p: Team.getTeamByPlayerName(e.getPlayer().getName()).getPlayers()){
            if(AbilityUtil.notInSpawn(p)&& !Loadout.getLoadout(p).name().toLowerCase().contains("tank")&&!Loadout.getLoadout(p).name().toLowerCase().contains("healer")&&!p.equals(e.getPlayer())&& !Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("tank")&&!Loadout.getLoadout(e.getPlayer()).name().toLowerCase().contains("healer")){
                cancel=true;

            }
        }


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
