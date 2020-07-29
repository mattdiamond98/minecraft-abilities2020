package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.ChargeableCapstoneWizardStyle;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Blizzard extends ChargeableCapstoneWizardStyle {

    public static final int MANA_COST = 3;

    public static final int MAX_CHARGE = 20;
    public static final int MIN_CHARGE = 10;

    public static final int COOLDOWN_SECONDS = 60;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public Blizzard() {
        super(
                "Blizzard",
                new String[]{
                        "Blizzard",
                        String.format("%d Mana", MANA_COST),
                        String.format("%ds Cooldown", COOLDOWN_SECONDS),
                        "",
                        "Crouch + hold right click with wand to cast"
                },
                new Ability("Blizzard", Material.WHITE_DYE) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                }, MANA_COST, MAX_CHARGE, COOLDOWN_ABILITY_TICKS
        );
        ability.getStyles().add(this);
    }

    @Override
    public void onRelease(final Player p, int finalCharge) {
        Team team = Team.getTeamByPlayerName(p.getName());
        if (team == null) return;
        Set<Player> frozen = new HashSet<>();
        for (int i = 1; i < finalCharge; i++) {
            Set<Vector> arc = getArcOffset(i * 2, p.getLocation().getYaw() + 45);
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                Set<Block> blocks = new HashSet<>();
                Set<Player> affectedPlayers = new HashSet<>();
                arc.stream()
                        .map(v -> p.getLocation().add(v).getBlock())
                        .forEach(prior -> {
                                    Block block = AbilityUtil.getSolidBlock(prior);
                                    Arrays.stream(BlockFace.values())
                                            .filter(blockFace -> blockFace.getModY() == 0)
                                            .forEach(blockFace -> blocks.add(block.getRelative(blockFace)));
                                    blocks.add(block);
                                    block.getWorld().getNearbyEntities(block.getLocation(), 2, 3, 2).stream()
                                            .filter(entity -> entity instanceof Player)
                                            .map(entity -> (Player) entity)
                                            .filter(member -> Team.getTeamByPlayerName(member.getName()) != null)
                                            .filter(member -> !Team.getTeamByPlayerName(member.getName()).equals(team))
                                            .filter(member -> !AbilityUtil.inSpawn(member))
                                            .forEach(enemy -> {
                                                if (!frozen.contains(enemy)) {
                                                    affectedPlayers.add(enemy);
                                                }
                                            });
                                    Location effect = block.getRelative(BlockFace.UP).getLocation().add(0.5, 0.5, 0.5);
                                    effect.getWorld().playEffect(effect, Effect.STEP_SOUND, Material.SNOW);
                                    effect.getWorld().spawnParticle(Particle.SWEEP_ATTACK, effect,3, 1, 1, 1);
                                }
                        );
                blocks.stream()
                        .map(AbilityUtil::getSolidBlock)
                        .map(block -> block.getType().isSolid() ? block.getRelative(BlockFace.UP) : block)
                        .filter(AbilityUtil::validBlock)
                        .filter(block -> !block.getType().isSolid())
                        .filter(block -> block.getRelative(BlockFace.DOWN).getType().isSolid())
                        .forEach(block ->  block.setType(Material.SNOW));
                affectedPlayers.forEach(enemy -> freezePlayer(enemy, p));
                frozen.addAll(affectedPlayers);
            }, i * 3);

        }
    }

    private void freezePlayer(Player enemy, Player p) {
        float id = new Random().nextFloat();
        if (enemy.hasPotionEffect(PotionEffectType.SLOW)) {
            enemy.damage(16, p);
            AbilityUtil.surroundPlayer(enemy, block -> {
                if (AbilityUtil.validBlock(block) && !block.getType().isSolid()) {
                    block.setType(Material.PACKED_ICE);
                }
            });
        } else {
            enemy.damage(8, p);
            enemy.setVelocity(new Vector(0, 0.5, 0));
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
            AbilityUtil.surroundPlayer(enemy, block -> {
                if (AbilityUtil.validBlock(block) && (!block.getType().isSolid() || block.getType() == Material.SNOW)) {
                    block.setType(Material.SNOW_BLOCK);
                }
            });
        }
    }

    @Override
    public void onCharge(final Player p, int charge) {
        if (charge == MAX_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SNOW_GOLEM_AMBIENT, 1F, 1.0F);
        } else {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, ((float) charge) / ((float) MAX_CHARGE));
        }
        if (charge == MIN_CHARGE) {
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_SNOW_GOLEM_AMBIENT, 0.5F, 0.5F);
        }
        p.getWorld().playEffect(p.getEyeLocation(), Effect.STEP_SOUND, Material.SNOW_BLOCK);
        CHARGE_DATA_MAP.put(p.getUniqueId(), charge);
        p.setLevel(p.getLevel() - MANA_COST);
    }

    // 90 degree arc
    private Set<Vector> getArcOffset(int r, double theta) {
        Set<Vector> vectors = new HashSet<>();
        double cosTheta = Math.cos(Math.toRadians(theta));
        double sinTheta = Math.sin(Math.toRadians(theta));
        int x = -r, y = 0, err = 2 - 2 * r;
        do {
            double x0 = -x;
            double y0 = y;
            double x1 = x0 * cosTheta - y0 * sinTheta;
            double y1 = x0 * sinTheta + y0 * cosTheta;
            vectors.add(new Vector(x1, 0, y1));
            r = err;
            if (r <= y) err += ++y*2+1;
            if (r > x || err > y) err += ++x*2+1;
        } while (x < 0);
        return vectors;
    }
}
