package com.gmail.mattdiamond98.coronacraft.tutorial;

import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Tip {

    private static Random rand = new Random();

    private static final List<String> tips = new ArrayList<>();

    public static void reloadTips() {
        // TODO: make this in a text file
        tips.addAll(Arrays.asList(
                "Large maps have monuments that teams can claim to heal them.",
                "Each class regenerates their pickaxe and food over time.",
                "Rangers can crouch while shooting to use a special arrow.",
                "Ranger special arrows cost different amounts of arrows.",
                "Rangers regenerate arrows every second.",
                "Slayer arrows cost 64 arrows - but they instantly kill on hit.",
                "Fighters can switch between sword styles with 'Q'.",
                "Tanks use their rally ability to buff allies or weaken enemies nearby.",
                "Berserkers get a bonus for killing enemies with their axe.",
                "Berserkers can pay life to gain bonuses for a time.",
                "Engineers gain additional materials over time.",
                "Engineers can place down large schematics, consuming materials.",
                "Gladiators regain their thrown trident over time.",
                "Gladiator tridents have special effects when thrown.",
                "Gladiator nets influence all enemies in an area.",
                "Ninja poison shurikens lengthen the effect on already poisoned enemies.",
                "Ninja void shurikens can be used to swap places with enemies or allies.",
                "Ninja toxic end deals more damage based on how poisoned the target is.",
                "Skirmishers can place a trap that harms enemies who trigger it.",
                "Skirmishers highlight enemies hit with their crossbow.",
                "Anarchists regenerate TNT over time.",
                "Anarchist abilities consume TNT as ammunition.",
                "Buy alternative ability styles at the Trade Bazaar.",
                "Winning a match gives more Corona Coins for larger games."
        ));
    }

    public static String getTip(int number) {
        if (number < 0 || number >= tips.size()) {
            return null;
        }
        return tips.get(number);
    }

    public static void sendTip(Player player, int number) {
        String tip = getTip(number);
        if (tip != null) AbilityUtil.sendTip(player, tip);
    }

    public static void sendRandomTip(Player player) {
        sendTip(player, rand.nextInt(tips.size()));
    }

}
