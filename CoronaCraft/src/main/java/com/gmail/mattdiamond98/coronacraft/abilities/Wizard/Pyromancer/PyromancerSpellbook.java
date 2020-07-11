package com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Pyromancer;

import com.gmail.mattdiamond98.coronacraft.abilities.Wizard.Spellbook;

public class PyromancerSpellbook extends Spellbook {

    public PyromancerSpellbook() {
        super(
                "Pyromancy",
                new String[] {
                    "Harness the power of flames to",
                    "immolate your enemies."
                },
                new Firebolt(),
                new FlameRune(),
                new Fireball(),
                new CombustiveBurst(),
                new DragonfireHelix()
        );
    }
}
