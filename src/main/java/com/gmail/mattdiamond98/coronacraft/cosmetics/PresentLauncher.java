package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.tournament.ItemSmith;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PresentLauncher extends Gadget {
    public PresentLauncher() {
        super("Present Launcher", Material.DIAMOND_HORSE_ARMOR);
    }

    @Override
    public void runEffect(Player p) {
        //Helper class, gets the Skull successfully
        ItemStack i=new ItemSmith("dca29a3a-76d3-4979-88a2-2da034b99212", "", new String[]{});
        //BlockStateMeta bsm= (BlockStateMeta) i.getItemMeta();
        BlockData bd=Material.PLAYER_HEAD.createBlockData();
        Skull s=(Skull) bd;
        s.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("dca29a3a-76d3-4979-88a2-2da034b99212")));
        FallingBlock fb=(FallingBlock) p.getWorld().spawnFallingBlock(p.getLocation().add(0, 1, 0), (BlockData) s);
        fb.setVelocity(p.getLocation().getDirection().multiply(2));
        fb.setMetadata(MetadataKey.IS_COSMETIC, new FixedMetadataValue(CoronaCraft.instance, "Present Launcher"));
    }
    @EventHandler
    public void OnProjectileHit(ProjectileHitEvent e){

        if(e.getHitEntity() != null &&e.getHitBlock() ==null&&e.getEntity().hasMetadata(MetadataKey.IS_COSMETIC)&&((String)e.getHitEntity().getMetadata(MetadataKey.IS_COSMETIC).get(0).value()).contains("Present Launcher")){
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
