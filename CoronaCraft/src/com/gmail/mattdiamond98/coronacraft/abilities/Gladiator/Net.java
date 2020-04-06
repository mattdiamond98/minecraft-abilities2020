package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Net extends Ability {

    public Net() {
        super("Net", Material.FISHING_ROD);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerHitFishingRod(final PlayerFishEvent event) {
        final Player player = event.getPlayer();
        if (event.getCaught() instanceof Player) {
            final Player caught = (Player) event.getCaught();
            if (Team.getTeamByPlayerName(caught.getName()).getPlayers().contains(player)) return;
            if (caught.getPotionEffect(PotionEffectType.SLOW) == null) {
                new PotionEffect(PotionEffectType.WEAKNESS, 6 * 20, 0).apply(caught);
                new PotionEffect(PotionEffectType.SLOW, 6 * 20, 3).apply(caught);
                caught.getLocation().getWorld().playEffect(caught.getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
            }
        }
    }
}
