package com.gmail.mattdiamond98.coronacraft.SeasonSpecialRules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class LessVelocityRule extends SpecialRule {
    public LessVelocityRule() {
        super("Reduced projectile velocity");
    }
    @EventHandler
    public void OnProjectileShoot(ProjectileLaunchEvent e){
        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(0.5));
    }
}
