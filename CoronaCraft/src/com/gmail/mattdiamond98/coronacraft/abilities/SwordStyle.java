package com.gmail.mattdiamond98.coronacraft.abilities;

import com.gmail.mattdiamond98.coronacraft.Ability;
import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.tommytony.war.Team;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Random;

import static com.gmail.mattdiamond98.coronacraft.util.AbilityUtil.notInSpawn;

public class SwordStyle extends Ability {

    private Random random = new Random();

    public SwordStyle() {
        super("Sword Style", Material.DIAMOND_SWORD);
    }

    @Override
    public void initialize() {
        // TODO: We need a better way of mapping to different sub-styles and toggling between them.
        // TODO: Include the item's custom title and lore
        // TODO: Load this from YAML files
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.hasItem() && e.getItem().getType() == getItem()) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                // TOGGLE BETWEEN SWORDS
                Map<CoolDownKey, Integer> abilities = CoronaCraft.getPlayerAbilities();
                CoolDownKey key = new CoolDownKey(p, getItem());
                int currentStyle = abilities.getOrDefault(key, 0);
                currentStyle = (currentStyle + 1) % 3;
                if (currentStyle == 2 && !p.hasPermission("coronacraft.swiftblade")) currentStyle = 0;
                switch (currentStyle) {
                    case 0: p.sendMessage("Switched to Confusion Blade - 25% chance to distort opponent's view on hit"); break;
                    case 1: p.sendMessage("Switched to Flame Blade - Catch enemies on fire"); break;
                    case 2: p.sendMessage("Switched to Swift Blade - Gain a brief boost of speed on hit");
                }
                abilities.put(key, currentStyle);
            }
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            if (Team.getTeamByPlayerName(((LivingEntity) e.getEntity()).getName()).getPlayers().contains((Player) e.getDamager())) return;

            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD && notInSpawn(p)) {
                switch (CoronaCraft.getPlayerAbilities().getOrDefault(new CoolDownKey(p, getItem()), 0)) {
                    case 0: confusionBlade(p, (LivingEntity) e.getEntity(), e.getFinalDamage() * 0.5);
                    case 1: flameBlade(p, (LivingEntity) e.getEntity());
                    case 2: swiftBlade(p);
                }
            }
        }
    }

    private void confusionBlade(Player p, LivingEntity e, double damage) {
        if (random.nextFloat() < 0.25) {
            Location loc = e.getLocation();
            loc.setPitch((random.nextFloat() * 180) - 90);
            loc.setYaw((random.nextFloat() * 360) - 180);
            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, Material.GLOWSTONE);
            e.teleport(loc);
            e.damage(damage);
        }
    }

    private void flameBlade(Player p , LivingEntity e) {
        if (e.getFireTicks() == 0) {
            e.setFireTicks(40);
            e.getWorld().playEffect(e.getLocation(), Effect.MOBSPAWNER_FLAMES, 20);
        }
    }

    private void swiftBlade(Player p) {
        new PotionEffect(PotionEffectType.SPEED, 40, 0).apply(p);
    }
}
