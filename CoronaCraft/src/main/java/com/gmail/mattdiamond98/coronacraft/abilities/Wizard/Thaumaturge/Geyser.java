package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.gmail.mattdiamond98.coronacraft.util.MetadataKey;
import com.tommytony.war.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Geyser extends WizardStyle {

    public static final int MANA_COST = 20;

    public static final int COOLDOWN_SECONDS = 10;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    private static final int HEIGHT = 5;

    private static Set<Material> transparent = AbilityUtil.transparent;
    static {
        transparent.remove(Material.WATER);
    }
    private static Set<Vector> areaVectors = new HashSet<>();
    static {
        areaVectors.addAll(Arrays.asList(
                new Vector( 0, 0 , 0),
                new Vector( 1, 0,  0),
                new Vector(-1, 0,  0),
                new Vector( 0, 0,  1),
                new Vector( 0, 0, -1),
                new Vector( 1, 0,  1),
                new Vector( 1, 0, -1),
                new Vector(-1, 0,  1),
                new Vector(-1, 0, -1)
        ));
    }

    public Geyser() {
        super(
                "Geyser",
                new String[]{
                        "Geyser",
                        String.format("%d Mana", MANA_COST),
                        String.format("%ds Cooldown", COOLDOWN_SECONDS),
                        "",
                        "Crouch + left click with wand to cast"
                },
                new Ability("Geyser", Material.BLUE_DYE) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                    @EventHandler
                    public void onWaterFlow(BlockFromToEvent e) {
                        if (e.getBlock().getType() == Material.WATER) {
                            if (e.getBlock().hasMetadata(MetadataKey.NO_FLOW)) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
        );
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST)) {
            Block target = p.getTargetBlock(transparent, 30);
            if (target.getType().isSolid() || target.getType() == Material.WATER) {
                p.setLevel(p.getLevel() - MANA_COST);
                CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
                Team team = Team.getTeamByPlayerName(p.getName());
                for (int i = 0; i < HEIGHT; i++) {
                    final int fi = i;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                        Block step = target.getRelative(0, fi ,0);
                        step.getWorld().getNearbyEntities(step.getLocation(), 3.0, 3.0, 3.0).stream()
                                .filter(entity -> entity instanceof Player)
                                .map(entity -> (Player) entity)
                                .filter(member -> Team.getTeamByPlayerName(member.getName()) != null)
                                .filter(member -> !AbilityUtil.inSpawn(member))
                                .forEach(player -> {
                                    player.setVelocity(new Vector(player.getVelocity().getX(), 1.3, player.getVelocity().getZ()));
                                    if (Team.getTeamByPlayerName(player.getName()).equals(team)) {
                                        if (fi == HEIGHT - 1) {
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                                                p.setFallDistance(0);
                                            }, 35);
                                        }
                                    } else {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                                    }
                                });
                        step.getWorld().playSound(step.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 1F, ((float) fi) / HEIGHT);
                        areaAround(step).forEach(block -> {
                            block.setType(Material.WATER);
                            block.setMetadata(MetadataKey.NO_FLOW, new FixedMetadataValue(CoronaCraft.instance, true));
                        });
                        Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                            areaAround(step).forEach(block -> {
                                if (block.getType() == Material.WATER) {
                                    block.setType(Material.AIR);
                                }
                                if (block.hasMetadata(MetadataKey.NO_FLOW)) {
                                    block.removeMetadata(MetadataKey.NO_FLOW, CoronaCraft.instance);
                                }
                            });
                        }, HEIGHT - fi);
                    }, i);
                }
            }
        }
        return 0;
    }

    private Stream<Block> areaAround(Block block) {
        return areaVectors.stream()
                .map(v -> block.getRelative(v.getBlockX(), 0, v.getBlockZ()))
                .filter(b -> !b.getType().isSolid());
    }
}
