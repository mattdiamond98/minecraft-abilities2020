package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Thaumaturge;

import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Spellbook;

public class ThaumaturgeSpellbook extends Spellbook {

    public ThaumaturgeSpellbook() {
        super(
                "Thaumaturgy",
                new String[] {
                        "Command water and ice to",
                        "render your enemies helpless."
                },
                new Deluge(),
                new IceWall(),
                new Geyser(),
                new Blizzard(),
                new Typhoon(), 223458
        );
    }

}
