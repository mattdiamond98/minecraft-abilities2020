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

public class WizardsOnlySpecialRule extends SpecialRule {
    public WizardsOnlySpecialRule() {
        super("Wizards Only");
    }
    @EventHandler
    public void OnPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e){
        Warzone w=Warzone.getZoneByPlayerName(e.getPlayer().getName());
        boolean cancel=!Loadout.getLoadout(e.getPlayer()).equals(Loadout.WIZARD);


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
