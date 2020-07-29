package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

public class SleightOfHand extends AbilityStyle {
    public SleightOfHand() {
        super("Sleight of Hand", new String[]{"Add extra projectiles",
                "to your loaded crossbow",
                "when you hit an opponent."
        }, "coronacraft.skirmisher.sleightofhand", 723458);
    }

    /***
     * @param p the player activating the ability
     * @param args args[0] instanceof EntityDamageByEntityEvent the event
     * @return
     */
    public int execute(Player p, Object... args) {
        if (p.getInventory().getItemInOffHand().getType() == Material.CROSSBOW) {
            addBolt(p, p.getInventory().getItemInOffHand());
            return 0;
        }
        if (!p.getInventory().contains(Material.CROSSBOW)) return 0;
        for (ItemStack itemStack : p.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == Material.CROSSBOW) {
                addBolt(p, itemStack);
            }
        }
        return 0;
    }

    private void addBolt(Player p, ItemStack crossbow) {
        CrossbowMeta meta = (CrossbowMeta) crossbow.getItemMeta();
        if (meta == null) return;
        if (meta.hasChargedProjectiles()) {
            if (meta.getChargedProjectiles().size() < 3) {
                meta.addChargedProjectile(new ItemStack(Material.SPECTRAL_ARROW, 1));
                crossbow.setItemMeta(meta);
                p.sendMessage(ChatColor.GREEN + "Loaded an extra projectile");
            }
        }
    }

}
