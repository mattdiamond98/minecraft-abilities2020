package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ManaSteal extends SpecialRule {
    public ManaSteal() {
        super("Mana Steal");
    }
    @EventHandler
    public void OnDeath(WarPlayerDeathEvent e){
        if(e.getKiller() instanceof  Player && AbilityUtil.notInSpawn((Player) e.getKiller())&& Loadout.getLoadout((Player) e.getKiller()).equals(Loadout.WIZARD)){
            ((Player) e.getKiller()).setLevel(Math.max(((Player) e.getKiller()).getLevel()+25, 100));
        }
    }
}
