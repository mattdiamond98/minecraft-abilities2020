package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FairyFountain extends AbilityStyle {
    public FairyFountain() {
        super("Fairy Fountain", new String[]{"Launches a lingering Instant Heath II potion", "which clears debuffs and lasts for 10s", "Cooldown 20s"}, "coronacraft.healer.fairyfountain", 0);
    }


    @Override
    public int execute(Player player, Object... data) {

        ItemStack i = new ItemStack(Material.LINGERING_POTION, 1);
        PotionMeta pm = (PotionMeta) i.getItemMeta();
        pm.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 10, 1), true);
        i.setItemMeta(pm);
        ThrownPotion tp = player.launchProjectile(ThrownPotion.class);
        tp.setVelocity(tp.getVelocity().multiply(1.5));
        tp.setMetadata(MetadataKey.REMOVES_DEBUFFS, new FixedMetadataValue(CoronaCraft.instance, player));

        tp.setItem(i);


        return 20*CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}