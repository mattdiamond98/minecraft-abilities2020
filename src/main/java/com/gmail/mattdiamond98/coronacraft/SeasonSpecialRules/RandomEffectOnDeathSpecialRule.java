package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Random;

public class RandomEffectOnDeathSpecialRule extends SpecialRule {
    public RandomEffectOnDeathSpecialRule() {
        super("Random Debuff On Death");
    }
    public void OnWarPlayerDeath(WarPlayerDeathEvent e){
        for(Player p: Team.getTeamByPlayerName(e.getVictim().getName()).getPlayers()){
            if(AbilityUtil.notInSpawn(p)){
                Random r=new Random();
                p.addPotionEffect(new PotionEffect(AbilityUtil.debuffs.get(r.nextInt(AbilityUtil.debuffs.size()-1)), 4, r.nextInt(1), false, false));
            }
        }
    }
}
