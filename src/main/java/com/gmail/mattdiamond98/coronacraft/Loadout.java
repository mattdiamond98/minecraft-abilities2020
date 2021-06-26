package com.gmail.mattdiamond98.coronacraft;


import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.AnarchistUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.TNTTrail;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.BerserkerUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.UndyingFrenzy;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.EngineerUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.SpacetimePortal;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.FighterUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.Omnislash;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.GladiatorUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.StormGodsWrath;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.DivineAura;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.HealerUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.NinjaUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShadowStride;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.ArrowStorm;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.RangerUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.SlayingArrow;
import com.gmail.mattdiamond98.coronacraft.abilities.Reaper.DeathsCarnage;
import com.gmail.mattdiamond98.coronacraft.abilities.Reaper.ReaperUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.InstinctiveHunter;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.SkirmisherUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.BossAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.SummonerUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.DesolationFist;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.TankUltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateManager;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUltimateManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum Loadout {

    FIGHTER(Material.DIAMOND_SWORD, new Omnislash(), new FighterUltimateManager()),
    RANGER(Material.BOW, new ArrowStorm(), new RangerUltimateManager()),
    ENGINEER(Material.IRON_PICKAXE, new SpacetimePortal(), new EngineerUltimateManager()),
    BERSERKER(Material.DIAMOND_AXE, new UndyingFrenzy(), new BerserkerUltimateManager()),
    SKIRMISHER(Material.CROSSBOW, new InstinctiveHunter(), new SkirmisherUltimateManager()),
    NINJA(Material.SHEARS, new ShadowStride(), new NinjaUltimateManager()),
    GLADIATOR(Material.TRIDENT, new StormGodsWrath(), new GladiatorUltimateManager()),
    TANK(Material.SHIELD, new DesolationFist(),new TankUltimateManager()),
    ANARCHIST(Material.FLINT_AND_STEEL, new TNTTrail(), new AnarchistUltimateManager()),
    WIZARD(Material.BLAZE_ROD, new WizardUltimateAbility(), new WizardUltimateManager()),
    SUMMONER(Material.STICK, new BossAbility(), new SummonerUltimateManager()),
    HEALER(Material.GOLDEN_SWORD, new DivineAura(), new HealerUltimateManager()),
    REAPER(Material.NETHERITE_HOE, new DeathsCarnage(), new ReaperUltimateManager());
    private Material item;
    private UltimateAbility ultimate;
    private UltimateManager manager;

    Loadout(Material item, UltimateAbility ultimate, UltimateManager um) {
        this.item = item;
        this.ultimate = ultimate;
        this.manager=um;
    }

    public Material getItem() {
        return item;
    }

    public UltimateAbility getUltimate() {
        return ultimate;
    }
    public UltimateManager getManager(){
        return manager;
    }
    public static Loadout loadoutFromItem(Material material) {
        for (Loadout loadout : Loadout.values()) {
            if (material == loadout.item) return loadout;
        }
        return null;
    }

    public static Loadout getLoadout(Player p) {
        Loadout offhand = loadoutFromItem(p.getInventory().getItemInOffHand().getType());
        if (offhand != null) return offhand;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null) {
                Loadout loadout = loadoutFromItem(itemStack.getType());
                if (loadout != null) return loadout;
            }
        }
        return null;
    }
}
