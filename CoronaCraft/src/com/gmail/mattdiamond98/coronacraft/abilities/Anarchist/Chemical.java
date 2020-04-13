package com.gmail.mattdiamond98.coronacraft.abilities.Anarchist;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.ChatColor;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Chemical extends AbilityStyle {
    public Chemical() {
        super("Chemical", new String[] {
                "Cover an area with dangerous,",
                "damaging chemicals.",
                "Cost: 1 TNT",
                "Cooldown: 10 seconds"
        }, "coronacraft.anarchist.chemical");
    }

    public int execute(Player p, Object... args) {
        Egg egg = (Egg) args[0];
        egg.getWorld().createExplosion(egg.getLocation(),2.0F, false, false, (Player) egg.getShooter());
        if (Warzone.getZoneByLocation(egg.getLocation()) == null ||
                Warzone.getZoneByLocation(egg.getLocation())
                        .getTeams().stream()
                        .map(Team::getTeamSpawns)
                        .flatMap(List::stream).anyMatch(loc -> loc.toVector().distanceSquared(egg.getLocation().toVector()) < 5 * 5)) {
            p.sendMessage(ChatColor.RED + "Cannot splash so close to a spawn point!");
        } else {
            AreaEffectCloud cloud = (AreaEffectCloud) egg.getWorld().spawnEntity(egg.getLocation(), EntityType.AREA_EFFECT_CLOUD);
            cloud.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 20 * 20, 1), true);
        }
        return 0;
    }
}
