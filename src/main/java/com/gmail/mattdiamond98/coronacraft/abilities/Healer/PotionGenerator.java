package com.gmail.mattdiamond98.coronacraft.abilities.Healer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityKey;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionGenerator extends Ability {


    public PotionGenerator() {
        super("Potion Generator", Material.BREWING_STAND);
    }

    @Override
    public void initialize() {
    styles.add(new HealPotion());
    }


    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e){
        if(!e.hasItem())return;
        if(e.getItem().getType().equals(item)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void OnCoolDownTick(CoolDownTickEvent e){
        if (e.getItem() != item) return;
        if (e.getTicksRemaining() % (CoronaCraft.ABILITY_TICK_PER_SECOND * 8) != 0) return; // Once per eight seconds
        if (AbilityUtil.getTotalCount(e.getPlayer(), item) == 0) return;
        ItemStack i=new ItemStack(Material.SPLASH_POTION, 2);
        PotionMeta pm=(PotionMeta)i.getItemMeta();
        pm.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 0, 1), true);
        pm.setDisplayName(ChatColor.RESET+"Splash Potion of Healing");
        i.setItemMeta(pm);

        AbilityUtil.regenerateItem(e.getPlayer(), i, 2, 1);

    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        AbilityKey key = new AbilityKey(e.getPlayer(), item);
        if (e.getPlayer().getInventory().contains(item)) {
            if (!CoronaCraft.isOnCooldown(e.getPlayer(), item))
                CoronaCraft.getPlayerCoolDowns().put(key, 10_000);
        } else if (CoronaCraft.getPlayerCoolDowns().containsKey(key)){
            CoronaCraft.getPlayerCoolDowns().remove(key);
        }
    }




}
