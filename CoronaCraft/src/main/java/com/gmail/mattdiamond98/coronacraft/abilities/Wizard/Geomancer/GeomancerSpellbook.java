package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Geomancer;

import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Spellbook;

public class GeomancerSpellbook extends Spellbook {

    public GeomancerSpellbook() {
        super(
                "Geomancy",
                new String[] {
                        "Weild the earth to",
                        "crush enemies underfoot"
                },
                new EarthSurge(),
                new StoneWard(),
                new Catapult(),
                new Earthquake(),
                new MeteorRain()
        );
    }

}
