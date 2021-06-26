package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.tournament.ItemSmith;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PumpkinGun extends Gadget {

    public PumpkinGun() {
        super("Pumpkin Gun", Material.IRON_HORSE_ARMOR);
    }

    @Override
    public void runEffect(Player p) {

        FallingBlock fb=(FallingBlock) p.getWorld().spawnFallingBlock(p.getLocation().add(0, 1, 0), Material.CARVED_PUMPKIN.createBlockData());
        fb.setVelocity(p.getLocation().getDirection().multiply(2));
        fb.setMetadata(MetadataKey.IS_COSMETIC, new FixedMetadataValue(CoronaCraft.instance, "Pumpkin Gun"));
    }
    @EventHandler
    public void OnProjectileHit(ProjectileHitEvent e){

        if(e.getHitEntity() != null &&e.getHitBlock() ==null&&e.getEntity().hasMetadata(MetadataKey.IS_COSMETIC)&&((String)e.getHitEntity().getMetadata(MetadataKey.IS_COSMETIC).get(0).value()).contains("Pumpkin Gun")){
           // Bukkit.broadcastMessage("I AM A PERSON");
            if(e.getHitEntity() instanceof Player){
                e.getEntity().remove();
                ItemStack is=new ItemStack(Material.CARVED_PUMPKIN, 1);
                is.addEnchantment(Enchantment.BINDING_CURSE, 1);
               final ItemStack is2;
                if(((Player) e.getHitEntity()).getInventory().getHelmet()!=null){

                    is2=((Player) e.getHitEntity()).getInventory().getHelmet();
                }else{
                    is2=null;
                }
                ((Player) e.getHitEntity()).getInventory().setHelmet(is);

                BukkitRunnable br= new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(is2!=null){
                        ((Player) e.getHitEntity()).getInventory().setHelmet(is2);}
                        else{
                            ((Player) e.getHitEntity()).getInventory().setHelmet(null);
                        }
                    }
                };
                br.runTaskLater(CoronaCraft.instance, 200L);
            }
        }else if(e.getEntity().hasMetadata(MetadataKey.IS_COSMETIC)&&e.getEntity().getMetadata(MetadataKey.IS_COSMETIC) instanceof PumpkinGun){

            ((FallingBlock)e.getEntity()).setDropItem(false);
            ((FallingBlock)e.getEntity()).remove();

        }


    }
}
