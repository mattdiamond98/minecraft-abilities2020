package com.gmail.mattdiamond98.coronacraft.util;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.utility.LoadoutSelection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gmail.mattdiamond98.coronacraft.CoronaCraft.ABILITY_TICK_PER_SECOND;

public final class AbilityUtil {

    private static final Random rand = new Random();

    public static Set<Material> transparent = EnumSet.allOf(Material.class).stream()
            .filter(((Predicate<Material>)Material::isSolid).negate()).collect(Collectors.toSet());

    public static void setStackCount(Player player, Material item, int count) {
        if (player.getInventory().getItemInOffHand().getType().equals(item)) {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            offhand.setAmount(count);
            player.getInventory().setItemInOffHand(offhand);
        }
        boolean first = true;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().equals(item)) {
                if (first) {
                    itemStack.setAmount(count);
                    first = false;
                } else {
                    itemStack.setAmount(0);
                }
            }
        }
    }

    public static boolean inventoryContains(Player player, Material item) {
        return player.getInventory().getItemInOffHand().getType() == item
                || player.getItemOnCursor().getType() == item
                || player.getInventory().contains(item);
    }

    public static int getTotalCount(Player player, Material item) {
        if (player == null || item == null) return 0;
        int total_count = player.getInventory().all(item).values()
                .stream().map(ItemStack::getAmount).reduce(0, Integer::sum);
        if (player.getInventory().getItemInOffHand().getType() == item) {
            total_count += player.getInventory().getItemInOffHand().getAmount();
        }
        if (player.getItemOnCursor().getType() == item) {
            total_count += player.getItemOnCursor().getAmount();
        }
        return total_count;
}

    public static List<Player> getEnemies(Player player) {
        List<Player> enemies = new ArrayList<>();
        Warzone zone = Warzone.getZoneByPlayerName(player.getName());
        if (zone == null) return enemies;
        for (Team team : zone.getTeams()) {
            if (!team.getPlayers().contains(player)) enemies.addAll(team.getPlayers());
        }
        return enemies;
    }

    public static void applyStunEffect(Player player, int ticks) {
        new PotionEffect(PotionEffectType.JUMP, ticks, 200).apply(player);
        new PotionEffect(PotionEffectType.SLOW, ticks, 7).apply(player);
        new PotionEffect(PotionEffectType.BLINDNESS, ticks, 0).apply(player);
        new PotionEffect(PotionEffectType.WEAKNESS, ticks, 100).apply(player);
        new PotionEffect(PotionEffectType.SLOW_DIGGING, ticks, 100).apply(player);
        player.sendTitle("", ChatColor.RED + "STUNNED", 0, ticks, 0);
        player.sendMessage(ChatColor.RED + "You have been stunned!.");
    }

    public static void setItemStackToCooldown(Player player, Material item) {
        Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
        if (item == null || player == null || !player.isOnline()) return;
        if (player.getInventory().contains(item)) {
            int coolDown = coolDowns.getOrDefault(new AbilityKey(player, item), 1);
            setStackCount(player, item, (coolDown + 1) / ABILITY_TICK_PER_SECOND);
        }
    }

    public static boolean inWarzone(Player p) {
        return Warzone.getZoneByPlayerName(p.getName()) != null;
    }

    public static boolean inSpawn(Player p) {
        Warzone warzone = Warzone.getZoneByPlayerName(p.getName());
        if (warzone == null) return false;
        LoadoutSelection loadoutState = warzone.getLoadoutSelections().get(p.getName());
        return loadoutState != null && loadoutState.isStillInSpawn();
    }

    public static boolean notInSpawn(Player p) {
        return !inSpawn(p);
    }

    public static FallingBlock dropBlockRandomly(Block block) {
        return dropBlockWithVector(block, new Vector(rand.nextFloat() * 0.3 - 0.15, 0.5, rand.nextFloat() * 0.3 - 0.15));
    }

    public static Block getSolidBlock(Block block) {
        return getSolidBlock(block, 5);
    }

    public static Block getSolidBlock(Block block, int i) {
        if (i == 0 || block.getType().isSolid() && !block.getRelative(0, 1, 0).getType().isSolid()) {
            return block;
        }
        if (!block.getType().isSolid()) return getSolidBlock(block.getRelative(0, -1, 0), i - 1);
        return getSolidBlock(block.getRelative(0, 1, 0), i - 1);
    }

    public static FallingBlock dropBlockWithVector(Block block, Vector vector) {
        Material material = block.getType();
        block.setType(Material.AIR);
        FallingBlock fragment = block.getWorld().spawnFallingBlock(block.getLocation(), Bukkit.createBlockData(material));
        fragment.setDropItem(false);
        fragment.getWorld().playEffect(fragment.getLocation(), Effect.STEP_SOUND, material);
        fragment.setVelocity(vector);
        return fragment;
    }

    public static boolean validBlock(final Block block) {
        Warzone zone = Warzone.getZoneByLocation(block.getLocation());
        if (zone == null) return false;
        return zone.getTeams().stream().map(Team::getSpawnVolumes)
                .map(Map::values)
                .flatMap(Collection::stream)
                .peek(volume -> {
                    if (volume.getCornerTwo().getY() >= volume.getCornerOne().getY()) {
                        volume.setCornerTwo(volume.getCornerTwo().add(new Vector(0, 5, 0)));
                    } else {
                        volume.setCornerOne(volume.getCornerOne().add(new Vector(0, 5, 0)));
                    }
                })
                .noneMatch(volume -> volume.contains(block));
    }

    public static void surroundPlayer(Player p, Consumer<Block> blockConsumer) {
        p.teleport(p.getLocation().add(0.5, 0, 0.5));
        Block feet = p.getLocation().getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Arrays.asList(
                BlockFace.EAST,
                BlockFace.WEST,
                BlockFace.SOUTH,
                BlockFace.NORTH
        ).forEach(face -> {
                blockConsumer.accept(feet.getRelative(face));
                blockConsumer.accept(head.getRelative(face));
        });
        blockConsumer.accept(feet.getRelative(BlockFace.DOWN));
        blockConsumer.accept(head.getRelative(BlockFace.UP));
    }

    public static void toggleAbilityStyle(Player p, Material item) {
        Ability ability = CoronaCraft.getAbility(item);
        AbilityStyle currentStyle = ability.getStyle(p);
        int nextPosition = ability.getNextStylePosition(p);
        AbilityStyle nextStyle = ability.getStyle(nextPosition);
        if (!nextStyle.equals(currentStyle)) {
            CoronaCraft.getPlayerAbilities().put(new AbilityKey(p, item), nextPosition);
            sendAbilityStyle(p, nextStyle);
            Set<Material> keySet = CoronaCraft.getAbilities().keySet();
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () ->
                    Arrays.stream(p.getInventory().getContents())
                            .filter(Objects::nonNull)
                            .filter(i -> keySet.contains(i.getType()))
                            .forEach(i -> AbilityUtil.formatItem(p, i)), 1);

        }
    }

    public static void sendAbilityStyle(Player p, AbilityStyle style) {
        p.sendMessage(AbilityUtil.formatStyleName(style));
        for (String line : style.getDescription()) {
            p.sendMessage(ChatColor.GRAY + line);
        }
    }

    public static void regenerateItemPassive(Player player, Material eventItem, Material baseItem,
                                                   ItemStack givenItem, int maxCount, int coolDown) {
        if (eventItem.equals(baseItem)  && player.getInventory().contains(baseItem)) {
            int total_count = AbilityUtil.getTotalCount(player, givenItem.getType());
            if (total_count++ < maxCount) {
                player.getInventory().addItem(givenItem);
            }
            if (total_count < maxCount) {
                Map<AbilityKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
                coolDowns.put(new AbilityKey(player, baseItem), coolDown);
            }
        }
    }

    // TODO: there is a lot of overlap in these two methods, should clean up some past code.
    public static void regenerateItem(Player p, Material item, int max, int increment) {
        if (notInSpawn(p)) {
            int count = getTotalCount(p, item);
            int newCount = Math.min(count + increment, max);
            if (count < newCount) {
                p.getInventory().addItem(new ItemStack(item, newCount - count));
            }
        }
    }

    public static void sendTip(Player p, String message) {
        p.sendMessage(ChatColor.GOLD + "Tip: " + ChatColor.GREEN + message);
    }

    public static void notifyAbilityOnCooldown(Player p, Ability a) {
        p.sendMessage(ChatColor.RED + "Your " + a.getName() + " ability is on cooldown.");
    }

    public static void notifyAbilityChangeOnCooldown(Player p) {
        p.sendMessage(ChatColor.RED + "You cannot switch that ability while in combat.");
    }

    public static void notifyAbilityRequiresResources(Player p, Material m, int c) {
        notifyAbilityRequiresResources(p, m.name().toLowerCase(), c);
    }

    public static void notifyAbilityRequiresResources(Player p, String s, int c) {
        p.sendMessage(ChatColor.RED + "That requires " + c + " " + s);
    }

    public static void notifyAbilityRequiresResources(Player p, List<Material> m, List<Integer> c) {
        p.sendMessage(ChatColor.RED + "That requires " + IntStream
                .range(0, Math.min(m.size(), c.size()))
                .mapToObj(i -> c.get(i) + " " + m.get(i)).collect(Collectors.joining(", and ")));
    }

    public static void notifyAbilityRequiresResources(Player p, Map<Material, Integer> materials) {
        List<Material> m = new ArrayList<>(materials.size());
        List<Integer> c  = new ArrayList<>(materials.size());
        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            m.add(entry.getKey());
            c.add(entry.getValue());
        }
        notifyAbilityRequiresResources(p, m, c);
    }

    public static void removeCooldowns(Player p) {
        new HashSet<>(CoronaCraft.getPlayerCoolDowns().keySet()).stream()
                .filter(key -> key.getPlayerID().equals(p.getUniqueId()))
                .forEach(key -> CoronaCraft.setCooldown(key.getPlayer(), key.getItem(), 0));
    }

    public static String formatStyleName(AbilityStyle style) {
        return ChatColor.GREEN + "" + ChatColor.BOLD + style.getName();
    }

    public static String formatStyleName(Player p, Material m) {
        return formatStyleName(CoronaCraft.getAbility(m).getStyle(p));
    }

    public static String formatLoreLine(String line) {
        return ChatColor.AQUA + line;
    }

    public static ItemStack formatItem(Player p, ItemStack item) {
        Ability ability = CoronaCraft.getAbility(item.getType());
        if (ability.getStyles().size() == 0) return item;
        AbilityStyle style = ability.getStyle(p);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(formatStyleName(style));
        meta.setLore(Arrays.stream(style.getDescription()).map(AbilityUtil::formatLoreLine).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack formatUltimate(UltimateAbility ultimate) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ultimate.getName());
        meta.setLore(Arrays.asList(ChatColor.AQUA + "Right click to use your ultimate ability"));
        item.setItemMeta(meta);
        return item;
    }

    public static List<Location> getCircle(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2*Math.PI)/amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++){
            double angle = i*increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public static List<Location> advancedCircle(Location loc, int radius, int height, boolean hollow, boolean sphere, int plusY){
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();

        for(int x = cx - radius; x <= cx + radius; x++){
            for (int z = cz - radius; z <= cz + radius; z++){
                for(int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++){
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);

                    if(dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))){
                        Location l = new Location(loc.getWorld(), x, y + plusY, z);
                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static Object getMetadata(Metadatable d, String key) {
        return d.hasMetadata(key) ? d.getMetadata(key).get(0).value() : null;
    }

    public static List<Vector> unitVectors() {
        return Arrays.asList(new Vector[]{
                new Vector(1, 0, 0),
                new Vector(0, 1, 0),
                new Vector(0, 0, 1),
                new Vector(-1, 0, 0),
                new Vector(0, -1, 0),
                new Vector(0, 0, -1)
        });
    }

}
