package com.gmail.mattdiamond98.coronacraft.abilities.Wizard;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer.GeomancerSpellbook;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer.Fireball;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer.PyromancerSpellbook;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge.ThaumaturgeSpellbook;
import com.gmail.mattdiamond98.coronacraft.event.CoronaCraftTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.event.WarPlayerLeaveSpawnEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class Wand extends Ability {

    public Wand() {
        super("Wand", Material.BLAZE_ROD);
    }

    @Override
    public void initialize() {
        styles.add(new PyromancerSpellbook());
        styles.add(new GeomancerSpellbook());
        styles.add(new ThaumaturgeSpellbook());

        for (AbilityStyle style : styles) {
            if (style instanceof Spellbook) {
                Spellbook spellbook = (Spellbook) style;
                CoronaCraft.instance.initializeAbilities(
                        spellbook.getLightStyle().getAbility(),
                        spellbook.getMediumStyle().getAbility(),
                        spellbook.getHeavyStyle().getAbility(),
                        spellbook.getCapstoneStyle().getAbility()
                );
                CoronaCraft.instance.getServer().getPluginManager().registerEvents(
                        spellbook.getUltimate(),
                        CoronaCraft.instance
                );
            }
        }
    }

    @EventHandler
    public void onGametick(CoronaCraftTickEvent e) {
        for (Warzone zone : War.war.getActiveWarzones()) {
            for (Player player : zone.getPlayers()) {
                if (player.isOnline()) {
                    if (AbilityUtil.notInSpawn(player)
                            && !zone.isFlagThief(player)
                            && AbilityUtil.inventoryContains(player, item)) {
                        player.setLevel(Math.min(100, player.getLevel() + 1));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == Material.BOOK) && notInSpawn(e.getPlayer())) {
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (AbilityUtil.inventoryContains(e.getVictim(), item)) {
            e.getVictim().setLevel(0);
        }
    }

    @EventHandler
    public void onPlayerLeaveSpawn(WarPlayerLeaveSpawnEvent e) {
        if (AbilityUtil.inventoryContains(e.getPlayer(), item)) {
            e.getPlayer().setLevel(50);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (
                e.hasItem()
                && e.getAction() != Action.PHYSICAL
                && Warzone.getZoneByPlayerName(e.getPlayer().getName()) != null
        ) {
            if (e.getItem().getType() == item) getStyle(e.getPlayer()).execute(e.getPlayer(), e);
            else {
                getStyles().stream()
                        .filter(style -> style instanceof Spellbook)
                        .map(style -> (Spellbook) style)
                        .map(Spellbook::getStyles)
                        .flatMap(Arrays::stream)
                        .forEach(style -> {
                            if (style.ability.getItem() == e.getItem().getType()) {
                                AbilityUtil.sendAbilityStyle(e.getPlayer(), style);
                            }
                        });

            }
        }
    }

}
