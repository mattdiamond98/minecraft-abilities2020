package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Fatalis extends ProjectileAbilityStyle {

    public Fatalis() {
        super("Fatalis", new String[]{
                "Instantly kill enemies if",
                "they have less than half health"
        }, "coronacraft.gladiator.fatalis");
    }

    @Override
    public int execute(Player shooter, Object... args) {
        ProjectileHitEvent event = (ProjectileHitEvent) args[0];
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player) {
            Player hit = (Player) event.getHitEntity();
            Team team = Team.getTeamByPlayerName(hit.getName());
            if (team != null) {
                if (!team.getPlayers().contains(shooter)) {
                    if (hit.getHealth() < 10.0) {
                        hit.damage(100, shooter);
                        hit.getWorld().playEffect(hit.getEyeLocation(), Effect.SMOKE, 10);
                        hit.getWorld().playSound(hit.getEyeLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, 10, 1);
                    }
                }
            }
        } else {
            event.getEntity().remove();
        }
        return 0;
    }

    @Override
    public int onShoot(Projectile projectile) {
        projectile.setMetadata(MetadataKey.ON_HIT, new FixedMetadataValue(CoronaCraft.instance, this));
        return 6 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
