package com.gmail.mattdiamond98.coronacraft.abilities.Gladiator;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
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
        }, "coronacraft.gladiator.pluck", 0);
    }

    public int execute(Player p, Object... args) {
        FishHook hook = (FishHook) args[0];
        List<Player> enemies = AbilityUtil.getEnemies(p);
        Vector vector = p.getEyeLocation().toVector().subtract(hook.getLocation().toVector()).normalize().multiply(new Vector(2, 1.5, 2));
        for (Player enemy : enemies) {
            if (enemy.getLocation().distanceSquared(hook.getLocation()) < 2.5 * 2.5 && notInSpawn(enemy)) {
                enemy.getLocation().getWorld().playEffect(enemy.getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
                enemy.setVelocity(vector.setY(1));
                AbilityUtil.applyStunEffect(enemy, 20);
                enemy.sendMessage(ChatColor.RED + "You have been stunned.");
            }
        }
        for(Entity e: hook.getNearbyEntities(2.5, 2.5, 2.5)){
            if(e instanceof CustomEntity&& Team.getNonPlayerEntityTeam(e)!=Team.getTeamByPlayerName(p.getName())){
            e.getLocation().getWorld().playEffect(((LivingEntity)e).getEyeLocation(), Effect.STEP_SOUND, Material.COBWEB);
            e.setVelocity(vector.setY(1));
            AbilityUtil.applyStunEffect((LivingEntity) e, 20);}
        }
        return 10 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
