package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TargetPlayerCustom extends AbilityStyle {
    public TargetPlayerCustom() {
        super("Follow Player", new String[]{"Your entities will follow", "the player you right clicked on", "for 20 secs", "Cooldown 35 secs"}, 0);
    }

    @Override
    public int execute(Player player, Object... data) {
        Player p=(Player) data[0];
        Boolean b= Team.getTeamByPlayerName(player.getName()).equals(Team.getTeamByPlayerName(p.getName()));
        BukkitRunnable br=new BukkitRunnable() {
            @Override
            public void run() {
                for(Entity e:((SummonerBiome) CoronaCraft.getAbility(Material.GRASS_BLOCK).getStyle(player)).getPlayerEntities(player)){
                    if(b&&player.hasMetadata(MetadataKey.LAST_ATTACKED_BY)){
                        ((EntityInsentient) e).setGoalTarget((EntityLiving) ((CraftPlayer)(Player)player.getMetadata(MetadataKey.LAST_ATTACKED_BY).get(0).value()).getHandle());

                    }else{
                        ((EntityInsentient) e).setGoalTarget((EntityLiving)((CraftPlayer) p).getHandle());


                    }

                }
            }
        };
        br.runTaskTimer(CoronaCraft.instance, 0L, 1L);
        BukkitRunnable br2=new BukkitRunnable() {
            @Override
            public void run() {
                br.cancel();
            }
        };
        br2.runTaskLater(CoronaCraft.instance, 400L);





        return 35*20;
    }
}
