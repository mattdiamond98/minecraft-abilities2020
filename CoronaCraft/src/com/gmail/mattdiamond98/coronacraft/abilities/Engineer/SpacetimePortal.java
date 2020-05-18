package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Warzone;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpacetimePortal extends UltimateAbility {

    private static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isSolid).negate()).collect(Collectors.toSet());

    public static final int DURATION = 25 * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final Map<UUID, Block[]> PORTALS = new HashMap<>();

    public SpacetimePortal() {
        super("Spacetime Portal");
    }

    @Override
    public void activate(Player player) {
        Block target = player.getTargetBlock(transparent, 100);

        Warzone zone = Warzone.getZoneByLocation(target.getLocation());
        if (zone == null || !zone.equals(Warzone.getZoneByPlayerName(player.getName()))) {
            player.sendMessage(ChatColor.RED + "Location invalid: target must be in zone");
            player.getInventory().addItem(AbilityUtil.formatUltimate(this));
            return;
        }
        target = target.getRelative(0, 2, 0);
        zone = Warzone.getZoneByLocation(target.getLocation());
        if (target.getType().isSolid() || zone == null || !zone.equals(Warzone.getZoneByPlayerName(player.getName()))) {
            player.sendMessage(ChatColor.RED + "Location invalid: target must have space above it");
            player.getInventory().addItem(AbilityUtil.formatUltimate(this));
            return;
        }
        target = target.getRelative(0, -1, 0);
        zone = Warzone.getZoneByLocation(target.getLocation());
        if (target.getType().isSolid() || zone == null || !zone.equals(Warzone.getZoneByPlayerName(player.getName()))) {
            player.sendMessage(ChatColor.RED + "Location invalid: target must have space above it");
            player.getInventory().addItem(AbilityUtil.formatUltimate(this));
            return;
        }

        Block[] blocks = new Block[4];
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
        target.setType(Material.END_GATEWAY);
        target.getRelative(0, 1, 0).setType(Material.END_GATEWAY);
        blocks[0] = target;
        blocks[1] = target.getRelative(0, 1, 0);

        for (Location loc : new Location[]{player.getLocation(), player.getEyeLocation()}) {
            Block block = loc.getBlock();
            block.setType(Material.END_GATEWAY);
            EndGateway gate = (EndGateway) block.getState();
            gate.setExactTeleport(true);
            gate.setExitLocation(target.getLocation());
            gate.update(true);
            if (blocks[2] == null) blocks[2] = block;
            else blocks[3] = block;
        }
        PORTALS.put(player.getUniqueId(), blocks);
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.ENGINEER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.ENGINEER) {
            UltimateTracker.removeProgress(e.getPlayer());
            if (PORTALS.containsKey(e.getPlayer().getUniqueId())) {
                Block[] blocks = PORTALS.remove(e.getPlayer().getUniqueId());
                for (Block block : blocks) {
                    if (block.getType() == Material.END_GATEWAY)
                        block.setType(Material.AIR);
                }
            }
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }

}
