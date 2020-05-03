package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Pluck extends AbilityStyle {

    public Pluck() {
        super("Pluck", new String[] {
                "Pull your opponent toward you",
                "with great force, stunning them."
        }, "coronacraft.gladiator.pluck");
    }

    public int execute(Player p, Object... args) {
        FishHook hook = (FishHook) args[0];
        List<Player> enemies = AbilityUtil.getEnemies(p);
        Vector vector = p.getEyeLocation().toVector().subtract(hook.getLocation().toVector()).normalize().multiply(2);
        for (Player enemy : enemies) {
            if (enemy.getLocation().distanceSquared(hook.getLocation()) < 2 * 2 && notInSpawn(enemy)) {
                enemy.getLocation().getWorld().playEffect(enemy.getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
                enemy.setVelocity(vector);
                new PotionEffect(PotionEffectType.SLOW, 20, 3).apply(enemy);
                new PotionEffect(PotionEffectType.WEAKNESS, 20, 1).apply(enemy);
                new PotionEffect(PotionEffectType.BLINDNESS, 20, 0).apply(enemy);
                enemy.sendMessage(ChatColor.RED + "You have been stunned.");
            }
        }
        return 7 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
