package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;
import sun.jvm.hotspot.oops.Metadata;

public class ExplosionUltActivateEffect extends UltimateActivateEffect {
    public ExplosionUltActivateEffect() {
        super("Explosion");
    }

    @Override
    public void runEffect(Player p) {
    p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, p.getLocation(), 3);

    }
}
