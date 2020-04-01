package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Map;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class NinjaLeap extends Ability {

    public static final int LEAP_COOL_DOWN = 6; // 3 Seconds

    public NinjaLeap() {
        super("Ninja Leap", Material.STONE_HOE);
    }

    @EventHandler
    public void onCoolDownTick(CoolDownTickEvent e) {
        AbilityUtil.setItemStackToCooldown(e.getPlayer(), getItem());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == Material.STONE_HOE && notInSpawn(p)) {
            Map<CoolDownKey, Integer> coolDowns = CoronaCraft.getPlayerCoolDowns();
            CoolDownKey key = new CoolDownKey(p, getItem());
            if (!coolDowns.containsKey(key)) {
                // TODO: Check ability map to see if leap is activated or an overloaded ability like blink
                coolDowns.put(key, leap(p));
            } else {
                p.sendMessage("That move is still on cooldown!");
            }
        }
    }

    private int leap(Player p) {
        p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.BLACK_WOOL);
        Vector v = p.getVelocity().add(p.getEyeLocation().getDirection().normalize().multiply(1.3));
        v.setY(v.getY() * 0.5);
        p.setVelocity(v);
        return LEAP_COOL_DOWN;
    }
}
