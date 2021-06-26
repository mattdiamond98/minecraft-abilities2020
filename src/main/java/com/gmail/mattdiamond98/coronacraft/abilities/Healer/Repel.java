package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Repel extends AbilityStyle {
    public Repel() {
        super("Repel", new String[]{"Knocks back all enemies in a, ", "4 block radius and", " disables your sword for 10 secs"}, 0);

    }

    @Override
    public int execute(Player player, Object... data) {

        ItemStack i=(ItemStack)data[0];
        ItemMeta im=i.getItemMeta();

        AttributeModifier am=new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", -4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier am2=new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -1.6, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, am);
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, am2);
        i.setItemMeta(im);
        player.setCooldown(Material.GOLDEN_SWORD, 200);
        BukkitRunnable b=new BukkitRunnable() {
            @Override
            public void run() {
                im.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                im.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);


                i.setItemMeta(im);

            }
        };
        b.runTaskLater(CoronaCraft.instance, 200L);
        for(Entity e:player.getNearbyEntities(4, 4, 4)){
            Boolean bl=true;

            if(e instanceof Player){
                if(Team.getTeamByPlayerName(((Player)e).getName()).equals(Team.getTeamByPlayerName(player.getName()))||Warzone.getZoneByLocation(player).isRespawning((Player) e)) bl=false;
            }else{
                if(Team.getTeamByPlayerName(player.getName()).equals(Team.getNonPlayerEntityTeam(e))) bl=false;
            }
            if(bl){
            Vector v= AbilityUtil.getVectorBetweenTwoLocations(player.getLocation(), e.getLocation());
            Vector next=new Vector(0, 1, 0);
            e.setVelocity(v.setY(1).multiply(0.5));}
        }

        return 10*CoronaCraft.ABILITY_TICK_PER_SECOND;
    }
}
