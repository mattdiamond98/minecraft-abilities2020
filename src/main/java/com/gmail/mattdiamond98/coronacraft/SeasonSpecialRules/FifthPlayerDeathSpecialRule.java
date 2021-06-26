package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FifthPlayerDeathSpecialRule extends SpecialRule {
    public FifthPlayerDeathSpecialRule() {
        super("Fifth Player Death");
    }
    @EventHandler
    public void OnPlayerDeath(WarPlayerDeathEvent e){
        if(Team.getTeamByPlayerName(e.getVictim().getName()).getRemainingLives()%5==0){
            for(Team t:e.getZone().getTeams()){
                if(!Team.getTeamByPlayerName(e.getVictim().getName()).equals(t)){
                    for(Player p:t.getPlayers()){
                        if(AbilityUtil.notInSpawn(p)){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false));}
                    }
                }
            }
        }
    }
}
