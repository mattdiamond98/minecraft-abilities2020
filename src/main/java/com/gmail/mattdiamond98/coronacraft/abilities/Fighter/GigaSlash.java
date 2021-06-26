package com.gmail.mattdiamond98.coronacraft.abilities.Fighter;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.Loadout;
import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.Anarchist.BuddyCreeper;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateAbility;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateListener;
import com.gmail.mattdiamond98.coronacraft.abilities.UltimateTracker;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownEndEvent;
import com.gmail.mattdiamond98.coronacraft.event.CoolDownTickEvent;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarPlayerDeathEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class GigaSlash extends UltimateAbility {
    public static final int DURATION = 25 * CoronaCraft.ABILITY_TICK_PER_SECOND;
    public float chargeAmount=0.0F;
    public boolean wasPlayeronGround=false;
    public GigaSlash() {
        super("Gigaslash", "coronacraft.ultimates.gigaslash");
    }

    @Override
    public void activate(Player player){
        UltimateListener.sendUltimateMessage(player);
        CoronaCraft.setCooldown(player, Material.NETHER_STAR, DURATION);
         player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, DURATION/CoronaCraft.ABILITY_TICK_PER_SECOND * 20, 2, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, DURATION/CoronaCraft.ABILITY_TICK_PER_SECOND * 20, 11, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, DURATION/CoronaCraft.ABILITY_TICK_PER_SECOND * 20, 0, false, false));
    }
    @EventHandler
    public void onPlayerDeath(WarPlayerDeathEvent e) {
        if (UltimateTracker.isUltimateActive(e.getVictim()) && UltimateTracker.getLoadout(e.getVictim()) == Loadout.ANARCHIST) {
            UltimateTracker.removeProgress(e.getVictim());
        }
    }
    @EventHandler
    public void onTick(CoolDownTickEvent e) {
        if (e.getPlayer() == null || !e.getPlayer().isOnline()) return;

        if (e.getItem() == Material.NETHER_STAR &&UltimateTracker.isUltimateActive(e.getPlayer(), this)) {
            e.getPlayer().setLevel((int)chargeAmount*10);
            //e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getPlayer().getLocation(), 1, Bukkit.createBlockData(Material.REDSTONE_WIRE));
            if(AbilityUtil.isPlayerInAir(e.getPlayer())){
                if(wasPlayeronGround){
                    chargeAmount=0;
                }
                chargeAmount+=1;
            wasPlayeronGround=false;
            }else{
                wasPlayeronGround=true;
            }
        }
        if (Warzone.getZoneByPlayerName(e.getPlayer().getName()) == null) {
            UltimateTracker.removeProgress(e.getPlayer());
        }
        else if (e.getItem() == Material.NETHER_STAR && UltimateTracker.isUltimateActive(e.getPlayer(), this)) {
            float remaining = (e.getTicksRemaining() * 1.0F) / DURATION;
            if (remaining < 0.0) remaining = 0.0F;
            if (remaining > 1.0) remaining = 1.0F;
            e.getPlayer().setExp(remaining);}
    }
    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e){
        if(UltimateTracker.isUltimateActive(e.getPlayer(), this)&&(e.getAction().equals(Action.LEFT_CLICK_AIR)||e.getAction().equals(Action.LEFT_CLICK_BLOCK))&&e.hasItem()&&e.getItem().getType().equals(Material.DIAMOND_SWORD)){
            for(Location l:AbilityUtil.getCircle(e.getPlayer().getLocation(), chargeAmount, (int) (chargeAmount*10))){
                if(Math.toDegrees(l.toVector().angle(e.getPlayer().getLocation().toVector()))<=45){
                    l.getWorld().spawnParticle(Particle.SWEEP_ATTACK, l, 1);

                }
            }
        for(Entity et:e.getPlayer().getNearbyEntities(10, 10, 10)){
            if(Warzone.getZoneByLocation(et.getLocation())!=null && Warzone.getZoneByLocation(et.getLocation()).getName().equals(Warzone.getZoneByLocation(e.getPlayer()).getName())&&e.getPlayer().getLocation().distanceSquared(et.getLocation())<=chargeAmount&&Math.toDegrees(e.getPlayer().getEyeLocation().toVector().angle(et.getLocation().toVector()))<=45){
                if(et instanceof Player && !Team.getTeamByPlayerName(((Player) et).getName()).equals(Team.getTeamByPlayerName(e.getPlayer().getName()))){
                    ((Player) et).damage(7+(chargeAmount));

                }else if(et instanceof Damageable&& (Team.getNonPlayerEntityTeam(et)==null || !Team.getNonPlayerEntityTeam(et).equals(Team.getTeamByPlayerName(e.getPlayer().getName())))){
                    ((Damageable)et).damage(7+(chargeAmount));

                }

            }
        }
        chargeAmount=0;
        }
    }

    @EventHandler
    public void onEnd(CoolDownEndEvent e) {
        if (e.getItem() == Material.NETHER_STAR &&UltimateTracker.isUltimateActive(e.getPlayer(), this)) {

            chargeAmount=0;
            UltimateTracker.removeProgress(e.getPlayer());

            e.getPlayer().sendMessage(ChatColor.YELLOW + "Your ultimate has ended.");
            e.getPlayer().setLevel(0);
        }
    }

}
