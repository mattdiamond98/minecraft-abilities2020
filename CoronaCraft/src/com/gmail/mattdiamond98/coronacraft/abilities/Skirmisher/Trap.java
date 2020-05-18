package com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.ProjectileAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.TrapAbilityStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;

public class Trap extends Ability {

    public Trap() {
        super("Trap", Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
    }

    @Override
    public void initialize() {
        styles.add(new BearTrap());
    }

    @EventHandler
    public void cooldownTick(CoolDownTickEvent event) {
        if (item.equals(event.getItem())) {
            if (getStyle(event.getPlayer()) instanceof TrapAbilityStyle) {
                if (!event.getPlayer().isOnline() || Team.getTeamByPlayerName(event.getPlayer().getName()) == null || !event.getPlayer().getInventory().contains(Material.IRON_SWORD)) {
                    CoronaCraft.setCooldown(event.getPlayer(), item, 0);
                    return;
                }
                TrapAbilityStyle style = (TrapAbilityStyle) getStyle(event.getPlayer());
                if (!TrapAbilityStyle.getPlayerTraps().containsKey(event.getPlayer().getName())) {
                    if (!event.getPlayer().getInventory().contains(item)) {
                        event.getPlayer().getInventory().addItem(AbilityUtil.formatItem(event.getPlayer(), new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)));
                    }
                    AbilityUtil.setItemStackToCooldown(event.getPlayer(), item);
                } else {
                    Metadatable trap = TrapAbilityStyle.getPlayerTraps().get(event.getPlayer().getName());
                    if (trap instanceof Block) {
                        Block block = (Block) trap;
                        if (block.getType() != item) {
                            // Trap is broken
                            CoronaCraft.setCooldown(event.getPlayer(), item, style.onDestroy(event.getPlayer()));
                        } else {
                            // Trap not broken
                            CoronaCraft.setCooldown(event.getPlayer(), item, 100);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCooldownEnd(CoolDownEndEvent event) {
        if (event.getItem() == item) {
            if (getStyle(event.getPlayer()) instanceof TrapAbilityStyle) {
                TrapAbilityStyle style = (TrapAbilityStyle) getStyle(event.getPlayer());
                if (TrapAbilityStyle.getPlayerTraps().containsKey(event.getPlayer().getName())) {
                    Metadatable trap = TrapAbilityStyle.getPlayerTraps().get(event.getPlayer().getName());
                    if (trap instanceof Block) {
                        if (((Block) trap).getType() == item) {
                            ((Block) trap).setType(Material.AIR);
                            style.onDestroy(event.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == item && !event.isCancelled() && event.canBuild()) {
            if (Team.getTeamByPlayerName(event.getPlayer().getName()) != null) {
                if (!CoronaCraft.isOnCooldown(event.getPlayer(), item)) {
                    if (TrapAbilityStyle.getPlayerTraps().containsKey(event.getPlayer().getName())) {
                        ((Block) TrapAbilityStyle.getPlayerTraps().get(event.getPlayer().getName())).setType(Material.AIR);
                        TrapAbilityStyle.getPlayerTraps().remove(event.getPlayer().getName());
                    }
                    AbilityStyle style = getStyle(event.getPlayer());
                    if (style instanceof TrapAbilityStyle) {
                        TrapAbilityStyle.getPlayerTraps().put(event.getPlayer().getName(), event.getBlockPlaced());
                        CoronaCraft.setCooldown(event.getPlayer(), item, ((TrapAbilityStyle) style).onPlace(event.getPlayer(), event.getBlockPlaced()));
                    }
                } else {
                    AbilityUtil.notifyAbilityOnCooldown(event.getPlayer(), this);
                    event.setCancelled(true);
                }
            }
        }
    }
}
