package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Incendiary extends AbilityStyle {
    public Incendiary() {
        super("Incendiary", new String[] {
                "Set fire to an area with an",
                "fiery explosion.",
                "Cost: 1 TNT",
                "Cooldown: 10 seconds"
        }, "coronacraft.anarchist.incendiary");
    }

    public int execute(Player p, Object... args) {
        Egg egg = (Egg) args[0];
        egg.getWorld().createExplosion(egg.getLocation(),2.0F, true, false, (Player) egg.getShooter());
        List<Team> teams = new ArrayList<Team>(Warzone.getZoneByLocation(egg.getLocation()).getTeams());
        teams.remove(Team.getTeamByPlayerName(((Player) egg.getShooter()).getName()));
        for (Player enemy : teams.get(0).getPlayers()) {
            if (AbilityUtil.notInSpawn(enemy)) {
                if (enemy.getLocation().distanceSquared(egg.getLocation()) < 3 * 3) {
                    int tickTime = 4 * 20;
                    if (enemy.getLocation().distanceSquared(egg.getLocation()) < 2 * 2) {
                        tickTime = 6 * 20;
                    }
                    enemy.setFireTicks(tickTime);
                }
            }
        }
        return 0;
    }
}
