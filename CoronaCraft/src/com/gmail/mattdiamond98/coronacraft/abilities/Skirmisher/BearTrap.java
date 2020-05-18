package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.TrapAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BearTrap extends TrapAbilityStyle {

    public static final int COOLDOWN = 8 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public static final int ACTIVATE_DELAY = 2 * 20;

    public BearTrap() {
        super("Bear Trap", new String[]{
                "Place a trap down that damages",
                "and roots enemies that walk on it."
        });
    }

    /**
     * @param player the player who placed the trap
     * @param args args[0] instanceof PlayerInteractEvent, the event
     */
    public int execute(Player player, Object... args) {
        if (player == null || !player.isOnline()) return 0;
        PlayerInteractEvent event = (PlayerInteractEvent) args[0];
        if (event.getClickedBlock() != null) {
            event.getClickedBlock().setType(Material.AIR);
            event.getClickedBlock().removeMetadata(MetadataKey.ON_STEP, CoronaCraft.instance);
            event.getClickedBlock().removeMetadata(MetadataKey.PLAYER, CoronaCraft.instance);
        }
        player.sendMessage(ChatColor.GREEN + "Your trap has been triggered.");
        event.getPlayer().sendMessage(ChatColor.RED + "You triggered a bear trap!");
        event.getClickedBlock().setType(Material.AIR);
        new PotionEffect(PotionEffectType.JUMP, 5 * 20, 200).apply(event.getPlayer());
        new PotionEffect(PotionEffectType.SLOW, 5 * 20, 7).apply(event.getPlayer());
        event.getPlayer().damage(7.0, player);
        event.getPlayer().setVelocity(new Vector());
        event.getPlayer().getWorld().playEffect(event.getPlayer().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
        event.getPlayer().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 3, 1);
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PILLAGER_HURT, 5, 1);
        return COOLDOWN;
    }

    public int onDestroy(Player player) {
        if (player == null || !player.isOnline()) return 0;
        player.sendMessage(ChatColor.GREEN + "Your trap has been destroyed.");
        if (TrapAbilityStyle.getPlayerTraps().containsKey(player.getName())) {
            Metadatable block = TrapAbilityStyle.getPlayerTraps().remove(player.getName());
            if (block instanceof Block) {
                block.removeMetadata(MetadataKey.PLAYER, CoronaCraft.instance);
                block.removeMetadata(MetadataKey.ON_STEP, CoronaCraft.instance);
                ((Block) block).setType(Material.AIR);
            }
        }

        return COOLDOWN;
    }

    public int onPlace(Player player, Metadatable trap) {
        if (!(trap instanceof Block)) return 0;
        trap.setMetadata(MetadataKey.PLAYER, new FixedMetadataValue(CoronaCraft.instance, player));
        player.getWorld().playSound(((Block) trap).getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 3, 1);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
            if (player == null || !player.isOnline()) return;
            if (player.getWorld().getBlockAt(((Block) trap).getLocation()).getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                player.getWorld().playSound(((Block) trap).getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 3, 1);
                trap.setMetadata(MetadataKey.ON_STEP, new FixedMetadataValue(CoronaCraft.instance, this));
            }
        }, ACTIVATE_DELAY);
        return 100;
    }

}
