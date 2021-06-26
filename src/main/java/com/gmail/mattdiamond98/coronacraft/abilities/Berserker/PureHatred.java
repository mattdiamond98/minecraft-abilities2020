package com.gmail.mattdiamond98.coronacraft.abilities.Berserker;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.Weapons;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import net.minecraft.server.v1_16_R3.Items;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static com.gmail.mattdiamond98.coronacraft.util.Weapons.getEPF;

public class PureHatred extends UltimateAbility {

    public static final int DURATION = 25 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public PureHatred() {
        super("Pure Hatred", "coronacraft.ultimates.purehatred");
    }

    @Override
    public void activate(Player player) {
        UltimateListener.sendUltimateMessage(player);

        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (DURATION / CoronaCraft.ABILITY_TICK_PER_SECOND) * 20, 0, true, true));
    }

    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.BERSERKER) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR && UltimateTracker.getLoadout(e.getPlayer()) == Loadout.BERSERKER) {
            UltimateTracker.removeProgress(e.getPlayer());
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
        }
    }
    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.BERSERKER) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }
    @EventHandler
    public void OnEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && Warzone.getZoneByLocation(e.getDamager().getLocation())!=null&&Warzone.getZoneByLocation(e.getDamager().getLocation()).getPlayers().contains((Player) e.getDamager())&&UltimateTracker.isUltimateActive((Player) e.getDamager(), this)){
            Player p=(Player) e.getEntity();
            double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
            double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            int resistance = effect == null ? 0 : effect.getAmplifier();
            int epf = getEPF(p.getInventory());
            ((Damageable)e.getEntity()).damage(new Weapons().unCalculateDamageApplied(e.getDamage(), points, toughness, resistance, epf)*0.8);
            e.setCancelled(true);

        }
    }


}
