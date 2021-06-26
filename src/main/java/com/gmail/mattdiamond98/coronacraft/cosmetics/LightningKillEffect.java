package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

public class LightningKillEffect extends KillEffect {
    public LightningKillEffect() {
        super("Lightning Strike");

    }

    @Override
    public void runEffect(Location loc) {
        loc.getWorld().strikeLightningEffect(loc);
    }
}
