package com.gmail.mattdiamond98.coronacraft.cosmetics;

import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.TNTTrail;
import com.gmail.mattdiamond98.coronacraft.abilities.Berserker.UndyingFrenzy;
import com.gmail.mattdiamond98.coronacraft.abilities.Engineer.SpacetimePortal;
import com.gmail.mattdiamond98.coronacraft.abilities.Fighter.Omnislash;
import com.gmail.mattdiamond98.coronacraft.abilities.Gladiator.StormGodsWrath;
import com.gmail.mattdiamond98.coronacraft.abilities.Healer.DivineAura;
import com.gmail.mattdiamond98.coronacraft.abilities.Ninja.ShadowStride;
import com.gmail.mattdiamond98.coronacraft.abilities.Ranger.ArrowStorm;
import com.gmail.mattdiamond98.coronacraft.abilities.Skirmisher.InstinctiveHunter;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.BossAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.Tank.DesolationFist;
import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.WizardUltimateAbility;
import org.bukkit.Material;

public class KillEffectType {
    public static String LIGHTNING_STRIKE=CosmeticManager.killEffectPermHeader+"lightningstrike";
    public static String PUMPKIN_RAIN=CosmeticManager.gameWinEffectPermHeader+"pumpkinrain";
    public static KillEffect[] ALL_EFFECTS=new KillEffect[]{new LightningKillEffect(), new pumpkinKillEffect(), new BatKillEffect()};

}
