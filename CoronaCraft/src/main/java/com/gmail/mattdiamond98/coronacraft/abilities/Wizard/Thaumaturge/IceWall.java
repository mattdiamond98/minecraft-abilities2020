package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUtil;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class IceWall extends WizardStyle {

    public static final int MANA_COST = 20;

    public static final int WALL_LENGTH = 4; //half length

    public static final int COOLDOWN_SECONDS = 30;
    public static final int COOLDOWN_ABILITY_TICKS = COOLDOWN_SECONDS * CoronaCraft.ABILITY_TICK_PER_SECOND;

    public IceWall() {
        super(
                "Ice Wall",
                new String[]{
                        "Ice Wall",
                        String.format("%d Mana", MANA_COST),
                        String.format("%ds Cooldown", COOLDOWN_SECONDS),
                        "",
                        "Right click with wand to cast"
                },
                new Ability("Ice Wall", Material.PHANTOM_MEMBRANE) {
                    @EventHandler
                    public void onCooldownTick(CoolDownTickEvent e) {
                        AbilityUtil.setItemStackToCooldown(e.getPlayer(), item);
                    }
                }
        );
        ability.getStyles().add(this);
    }

    @Override
    public int execute(Player p, Object... args) {
        if (WizardUtil.handleManaCostAndCooldown(p, this, MANA_COST, true)) {
            p.setLevel(p.getLevel() - MANA_COST);
            CoronaCraft.setCooldown(p, ability.getItem(), COOLDOWN_ABILITY_TICKS);
            Vector direction = p.getLocation().getDirection().setY(0).normalize();
            Vector wallDirection = new Vector(direction.getZ(), 0, -direction.getX());
            Block target = p.getTargetBlock(AbilityUtil.transparent, 5);
            Set<Block> blocks = new HashSet<>();
            for (int i = 0; i < WALL_LENGTH; i++) {
                Vector v = wallDirection.clone().multiply(i);
                blocks.add(target.getRelative(v.getBlockX(), 0, v.getBlockZ()));
                blocks.add(target.getRelative(-v.getBlockX(), 0, -v.getBlockZ()));
            }
            blocks.forEach(IceWall::getSolidBlock);
            blocks.forEach(block -> {
                for (int i = 0; i < 4; i++) {
                    Block ice = block.getRelative(0, i, 0);
                    if (!ice.getType().isSolid() && AbilityUtil.validBlock(ice)) {
                        ice.setType(Material.PACKED_ICE);
                        ice.getWorld().playEffect(ice.getLocation(), Effect.STEP_SOUND, Material.PACKED_ICE);
                    }
                }
            });
            Bukkit.getScheduler().scheduleSyncDelayedTask(CoronaCraft.instance, () -> {
                blocks.forEach(block -> {
                    for (int i = 0; i < 4; i++) {
                        Block ice = block.getRelative(0, i, 0);
                        if (ice.getType() == Material.PACKED_ICE && AbilityUtil.validBlock(ice)) {
                            ice.setType(Material.AIR);
                            ice.getWorld().playEffect(ice.getLocation(), Effect.STEP_SOUND, Material.PACKED_ICE);
                        }
                    }
                });
            }, 200);
        }
        return 0;
    }

    private static Block getSolidBlock(Block block) {
        return AbilityUtil.getSolidBlock(block, 5);
    }

}
