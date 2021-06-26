package com.gmail.mattdiamond98.coronacraft.abilities.Reaper;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Scythe extends Ability {
    public static short hoeMaxDurability = Material.NETHERITE_HOE.getMaxDurability();
    public static short hoeMinDurability = 1;
    public static ArrayList<UUID> disabledPlayers=new ArrayList<UUID>();
    public Scythe() {
        super("Scythe", Material.NETHERITE_HOE);
    }



    @Override
    public void initialize() {
        styles.add(new GhastlyScythe());
        styles.add(new InfernalScythe());
        styles.add(new SoulScythe());
        styles.add(new ChainScythe());
    }

    /**
     * Handles switching scythe abilities.  Empties scythe fuel whenever an ability is switched.
     * @param e The PlayerDropItemEvent event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        // Make sure the correct item was dropped
        if ((e.getItemDrop().getItemStack().getType() == item)) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.getItemDrop().getItemStack().setDurability((short) (hoeMaxDurability - hoeMinDurability));
            e.setCancelled(true);
        }
    }

    /**
     * Gives all reapers "permanent" slowness 1 when they leave spawn.
     * @param event
     */
    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent event) {
        if (AbilityUtil.inventoryContains(event.getPlayer(), item)) {
            //event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()-0.25);
            AttributeModifier am=new AttributeModifier(UUID.randomUUID(), "generic.movement", event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue()*-0.25, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
           ItemMeta im= event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).getItemMeta();
            im.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
            event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST).setItemMeta(im);
            for (ItemStack current : event.getPlayer().getInventory()) {
                if (current != null && current.getType() == item) {
                    ItemMeta meta = current.getItemMeta();
//                    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
//                    meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(),"generic.attackSpeed", -3.3, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                    meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),"generic.attackDamage", 11, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                    current.setItemMeta(meta);
                }
            }
        }
    }

    /**
     * Handles triggering scythe abilities.
     * @param event The PlayerInteractEvent event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // check that the item was a right click
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(
                Action.RIGHT_CLICK_BLOCK)) {
            // Check that player has the correct item
            if (event.hasItem() && event.getItem().getType() == item) {
                // Check that the player is not currently on cooldown
                if (!ReaperCooldownTracker.isOnCooldown(player)) {
                    // Check that there is enough fuel left
                    HoeStyle style = (HoeStyle) getStyle(player);
                    if (style instanceof ChainScythe || consumeDurability(player, style)) {
                        ReaperCooldownTracker.setCooldown(player, style);
                        style.execute(player, event);
                    }
                }
                event.setCancelled(true);
            }
        }

    }


    @EventHandler
    public void OnInventoryClick(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player){
            Player p= (Player) e.getWhoClicked();
            if(Team.getTeamByPlayerName(((Player) e.getWhoClicked()).getName())!=null&&AbilityUtil.notInSpawn((Player) e.getWhoClicked())&&Loadout.getLoadout(p).equals(Loadout.REAPER) && e.getCurrentItem() != null && e.getCurrentItem().getType().equals(Material.LEATHER_CHESTPLATE)){
                e.setCancelled(true);
            }
        }
    }
    /**
     * Regenerates a quarter of the player's fuel when they hit an enemy.
     * @param event The EntityDamageByEntityEvent event
     */
    @EventHandler
    public void regenerateDurability(EntityDamageByEntityEvent event) {
        // Check that a player hit someone while holding the correct item
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            ItemStack heldItem = attacker.getInventory().getItemInMainHand();
            if (heldItem != null && heldItem.getType() == item && heldItem.getDurability() > 0 && !disabledPlayers.contains(attacker.getUniqueId())) {
                // Give back a quarter of the original durability
                heldItem.setDurability((short) Math.max(0, heldItem.getDurability() - hoeMaxDurability / 4));
                disabledPlayers.add(attacker.getUniqueId());
                BukkitRunnable br=new BukkitRunnable() {
                    @Override
                    public void run() {
                        disabledPlayers.remove(attacker.getUniqueId());
                    }
                };
                br.runTaskLater(CoronaCraft.instance, 28);
            }
        }
    }


    @EventHandler
    public void OnPlayerDeath(WarPlayerDeathEvent e){

    }
    /**
     * Regenerates all of the player's fuel when they kill an enemy.
     * @param event The EntityDamageByEntity eventt
     */
    @EventHandler
    public void fullyRegenerateDurability(WarPlayerDeathEvent event) {
        // Check that a player killed someone while holding the correct item
        if (event.getKiller() != null && event.getKiller() instanceof Player) {
            Player attacker = (Player) event.getKiller();
            ItemStack heldItem = attacker.getInventory().getItemInMainHand();
            if (heldItem != null && heldItem.getType() == item && heldItem.getDurability() > 0) {
                // Reset the durability of the hoe
                heldItem.setDurability((short) Math.max(0, heldItem.getDurability() - hoeMaxDurability / 2));
            }
        }
    }

    /**
     * Verifies that enough fuel is available and, if so, uses it.
     * @param player The player who is attempting to trigger a scythe ability
     * @param style The scythe ability
     * @return True if enough fuel was available and was consumed successfully, false otherwise
     */
    public boolean consumeDurability(Player player, HoeStyle style) {
        // Check that the item held by the player is a valid item and that the player is not in an invalid
        // condition
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() != item) { return false; }
        if (AbilityUtil.inSpawn(player) || !AbilityUtil.inWarzone(player)) { return false; }

        // Check that there is durability left to remove from the item
        if (heldItem.getDurability() >= hoeMaxDurability - hoeMinDurability) { player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10, 1); return false; }

        // Remove the durability specified
        heldItem.setDurability((short) Math.min(Scythe.hoeMaxDurability - Scythe.hoeMinDurability,
                heldItem.getDurability() + style.fuelCost));
        return true; // Lets caller know that an ability can be executed
    }
    public static boolean consumeDurability(Player player, RoseStyle style) {
        // Check that the item held by the player is a valid item and that the player is not in an invalid
        // condition
        ItemStack heldItem=null;
        if(player.getInventory().first(Material.NETHERITE_HOE)!=-1){
            heldItem=player.getInventory().getItem(player.getInventory().first(Material.NETHERITE_HOE));
        }else{
            return false;
        }
        if (heldItem == null || heldItem.getType() != Material.NETHERITE_HOE) { return false; }
        if (AbilityUtil.inSpawn(player) || !AbilityUtil.inWarzone(player)) { return false; }

        // Check that there is durability left to remove from the item
        if (heldItem.getDurability() >= hoeMaxDurability - hoeMinDurability) { player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10, 1); return false; }

        // Remove the durability specified
        heldItem.setDurability((short) Math.min(Scythe.hoeMaxDurability - Scythe.hoeMinDurability,
                heldItem.getDurability() + (Scythe.hoeMaxDurability-Scythe.hoeMinDurability)/((int)100/style.fuelCostpercent)));
        return true; // Lets caller know that an ability can be executed
    }

}
