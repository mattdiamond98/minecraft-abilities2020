package com.gmail.mattdiamond98.coronacraft.abilities.Engineer;

import com.gmail.mattdiamond98.coronacraft.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.config.WarzoneConfig;
import com.tommytony.war.volume.Volume;
import com.tommytony.war.volume.ZoneVolume;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Tower extends AbilityStyle {

    public static final int STEPS = 5;

    public static final Map<UUID, Location> REFERENCE_POINTS = new HashMap<>();

    public static final List<Clipboard> schematics = new ArrayList<>(STEPS);

    public static final int COST_PLANKS = 48;
    public static final int COST_COBBLE = 12;

    public Tower() {
        super("Tower", new String[]{
                "Create a defensive tower",
                "where you stand.",
                String.format("Cost: %d Planks, %d Cobblestone", COST_PLANKS, COST_COBBLE),
                "Construction Time: 5 seconds"
        });
        if (schematics.size() == 0) {
            for (int i = 1; i <= STEPS; i++) {
                String file = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit").getDataFolder()
                        .getAbsolutePath() + "/schematics/" + getName().toLowerCase() + "-" + i + ".schem";
                ClipboardFormat format = ClipboardFormats.findByFile(new File(file));
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    schematics.add(reader.read());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int execute(Player p, Object... args) {
        int state = (Integer) args[0];
        if (state == -1) {
            Warzone zone = Warzone.getZoneByPlayerName(p.getName());
            List<Volume> zoneVolumes = new ArrayList<>();
            zone.getTeams().stream().map(Team::getFlagVolume).filter(Objects::nonNull).collect(Collectors.toCollection(() -> zoneVolumes));
            zone.getTeams().stream().map(Team::getSpawnVolumes).map(x -> x.values())
                    .flatMap(Collection::stream).collect(Collectors.toCollection(() -> zoneVolumes));
            ZoneVolume fullVolume = zone.getVolume(); // get inside vol
            Clipboard schematic = schematics.get(0);
            Region schematicVolume = schematic.getRegion().clone();
            try {
                schematicVolume.shift(BukkitAdapter.asBlockVector(p.getLocation()).subtract(schematic.getOrigin()));
            } catch (RegionOperationException e) {
                e.printStackTrace();
            }
            if (zone.getWarzoneConfig().contains(WarzoneConfig.UNBREAKABLE) && zone.getWarzoneConfig().getBoolean(WarzoneConfig.UNBREAKABLE)) {
                return -3;
            }
            Location minSchematicLoc = BukkitAdapter.adapt(p.getWorld(), schematicVolume.getMinimumPoint());
            Location maxSchematicLoc = BukkitAdapter.adapt(p.getWorld(), schematicVolume.getMaximumPoint());
            if (!fullVolume.contains(minSchematicLoc)
                    || !fullVolume.contains(maxSchematicLoc)
                    || zoneVolumes.stream().anyMatch(v ->
                           v.contains(minSchematicLoc)
                        || v.contains(maxSchematicLoc)
                        || schematicVolume.contains(BukkitAdapter.asBlockVector(v.getCornerOne()))
                        || schematicVolume.contains(BukkitAdapter.asBlockVector(v.getCornerTwo()))
            )) {
                return -1;
            }
            int plankCount = AbilityUtil.getTotalCount(p, Material.OAK_PLANKS);
            int cobbleCount = AbilityUtil.getTotalCount(p, Material.COBBLESTONE);
            if (plankCount < COST_PLANKS || cobbleCount < COST_COBBLE) {
                AbilityUtil.notifyAbilityRequiresResources(p,
                        Arrays.asList(new Material[]{ Material.OAK_PLANKS, Material.COBBLESTONE}),
                        Arrays.asList(new Integer[]{COST_PLANKS, COST_COBBLE})
                );
                return -2;
            }
            AbilityUtil.setStackCount(p, Material.OAK_PLANKS, plankCount - COST_PLANKS);
            AbilityUtil.setStackCount(p, Material.COBBLESTONE, cobbleCount - COST_COBBLE);
            REFERENCE_POINTS.put(p.getUniqueId(), p.getLocation());
            return STEPS;
        } else if (state % CoronaCraft.ABILITY_TICK_PER_SECOND != 0){
            return 0;
        }
        state /= CoronaCraft.ABILITY_TICK_PER_SECOND;
        System.out.println("Handling state " + state);
        Clipboard schematic = schematics.get(state - 1);
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
                .getEditSession(BukkitAdapter.adapt(p.getWorld()), -1)) {
            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(editSession)
                    .to(BukkitAdapter.asBlockVector(REFERENCE_POINTS.get(p.getUniqueId())))
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 10, 1);
        return 0;
    }

    private boolean volumeContains(Volume v1, Volume v2) {
        int minX = v1.getMinX(), minY = v1.getMinY(), minZ = v1.getMinZ();
        return true;
    }
}
