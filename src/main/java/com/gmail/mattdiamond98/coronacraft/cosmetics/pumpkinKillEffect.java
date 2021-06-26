package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class pumpkinKillEffect extends KillEffect {
    public pumpkinKillEffect() {
        super("Pumpkin Rain");
    }

    @Override
    public void runEffect(Location loc) {
        /*ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("c6f635dd-7770-40e5-9743-39d64016edbf")));
        skull.setItemMeta(skullMeta);
    for(int i=0; i<=3; i++){

    }*/
        final Random r=new Random();
        final ItemStack pumpkin=new ItemStack(Material.PUMPKIN, 1);
        ArrayList<Item> spawnedItems=new ArrayList<Item>();
        BukkitRunnable br=new BukkitRunnable() {
            @Override
            public void run() {
                for(Item i:spawnedItems){
                    if(i.getLocation().getY()<=loc.getY()-1){
                    i.remove();}
                }
              Item item=  loc.getWorld().dropItem(loc.add(new Location(loc.getWorld(), (r.nextFloat()*4)-2, loc.getY()+1, (r.nextFloat()*4)-2)), pumpkin);
                item.setPickupDelay(99999999);
                spawnedItems.add(item)
;



            }
        };
        br.runTaskTimer(CoronaCraft.instance, 0L, 5L);
        BukkitRunnable br2=new BukkitRunnable() {
            @Override
            public void run() {
                for(Item i:spawnedItems){

                        i.remove();
                }
                br.cancel();
            }
        };
        br2.runTaskLater(CoronaCraft.instance, 100L);

    }
}
