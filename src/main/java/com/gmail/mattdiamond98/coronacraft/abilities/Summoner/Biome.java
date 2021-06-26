package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.gmail.mattdiamond98.coronacraft.abilities.Ability;
import com.gmail.mattdiamond98.coronacraft.abilities.AbilityStyle;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.GiantSlime;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.OldGiantSlime;
import com.gmail.mattdiamond98.coronacraft.util.AbilityUtil;
import com.tommytony.war.Team;
import com.tommytony.war.Warzone;
import com.tommytony.war.event.WarBattleWinEvent;
import com.tommytony.war.event.WarPlayerDeathEvent;
import com.tommytony.war.event.WarPlayerLeaveEvent;
import com.tommytony.war.event.WarScoreCapEvent;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftSlime;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Biome extends Ability {
    public Biome() {
        super("Biome", Material.GRASS_BLOCK);
    }

    @Override
    public void initialize() {
        styles.add(new CaveBiome());
        styles.add(new NetherBiome());
    }




    /*@EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent e){
        if(e.hasItem()&&(e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
            if(e.getItem().getType().equals(item)&&e.getItem().getItemMeta().hasDisplayName()&&(e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("CAVE")||e.getItem().getItemMeta().getDisplayName().toUpperCase().contains("NETHER"))){
                e.setCancelled(true);
            }
        }
        if(e.hasItem()&&e.getItem().getType().equals(Material.BREWING_STAND)){
            for(Entity et:e.getPlayer().getNearbyEntities(5, 5, 5)){
                if(et instanceof Slime){
                    Location loc=e.getPlayer().getLocation();
                    ((EntityInsentient)(((CraftEntity)et).getHandle())).getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), ((Slime) et).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());}
            }
        }
    }*/

    @EventHandler
    public void OnEntityDamage(EntityDamageEvent e){

        if(e.getEntity().getCustomName()!=null){
      if(e.getEntity().getCustomName().contains("GIANT")&&e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)){

          e.setCancelled(true);
      }}
    }
    @EventHandler
    public void OnProjectileShoot(ProjectileLaunchEvent e){
        if(e.getEntity().getShooter() instanceof Wither){
            Wither w=(Wither) e.getEntity().getShooter();
            if(w.getCustomName()!=null &&w.getCustomName().contains("Fire Wither")){
                if(e.getEntity().getType().equals(EntityType.WITHER_SKULL)){
             //       w.launchProjectile(LargeFireball.class, e.getEntity().getVelocity());
                    e.setCancelled(true);


                }
            }
        }
    }
    @EventHandler
    public void OnWitherFireball(ProjectileHitEvent e){
        if(e.getEntity().getShooter() instanceof Wither){
            Wither w=(Wither) e.getEntity().getShooter();
            if(w.getCustomName()!=null &&w.getCustomName().contains("Fire Wither")){
                if(e.getEntity().getType().equals(EntityType.FIREBALL)){
                    e.getEntity().getWorld().createExplosion(e.getHitBlock().getLocation().add(0, 1, 0), 2, true, false);


                }
            }
        }
    }
    @EventHandler
    public void OnWarScoreCap(WarScoreCapEvent e){
        for(AbilityStyle s:styles){
            for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
                net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
                //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

                //Bukkit.broadcastMessage("Something cool died");
                if(Warzone.getZoneByLocation(ete.getBukkitEntity().getLocation()).equals(e.getWarzone())){
                ((SummonerBiome)s).removeEntity(ete);}
            }}
    }
    @EventHandler
    public void OnDeath(WarPlayerDeathEvent e){
        for(AbilityStyle s:styles){
            for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
                net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
                //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

                //Bukkit.broadcastMessage("Something cool died");
                if(((SummonerBiome) s).getPlayerEntities(e.getVictim()).contains(ete)){
                ((SummonerBiome)s).removeEntity(ete);
                ete.getBukkitEntity().remove();}
            }}
    }
    @EventHandler
    public void OnLeave(WarPlayerLeaveEvent e){
        for(AbilityStyle s:styles){
            for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
                net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
                //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

                //Bukkit.broadcastMessage("Something cool died");
                if(((SummonerBiome) s).getPlayerEntities(Bukkit.getPlayer(e.getQuitter())).contains(ete)){
                    ((SummonerBiome)s).removeEntity(ete);
                    ete.getBukkitEntity().remove();}
            }}
    }



    @EventHandler(priority=EventPriority.HIGHEST)
    public void SlimeSplit(SlimeSplitEvent e){


            if(e.getEntity().getCustomName()!=null){
                if(e.getEntity().getCustomName().toUpperCase().contains("GIANT")){

                        if(Team.getNonPlayerEntityTeam(e.getEntity())!=null){


                            for(int i=0; i<=e.getCount();i++ ){
                            GiantSlime g=new GiantSlime(e.getEntity().getLocation(), ((CustomEntity)((CraftEntity)e.getEntity()).getHandle()).getPlayer(), Team.getNonPlayerEntityTeam(e.getEntity()));
                            Slime s=(Slime) g.getBukkitEntity();
                            s.setSize((int) Math.floor(((Slime)e.getEntity()).getSize()/2));
                            s.setHealth(s.getSize()^2);
                            ((CraftWorld)e.getEntity().getLocation().getWorld()).getHandle().addEntity(g);
                              }  e.setCancelled(true);


                    }
                }
            }

    }

    @EventHandler
    public void OnEntityDeath(EntityDeathEvent e){
        //Bukkit.broadcastMessage("Something died");

        for(AbilityStyle s:styles){
          for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
              net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
              //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

              if(ete.getBukkitEntity().getEntityId()==e.getEntity().getEntityId()){
             //Bukkit.broadcastMessage("Something cool died");
              ((SummonerBiome)s).removeEntity(ete);}
           }}

    }
    @EventHandler
    public void onWarBattleWin(WarBattleWinEvent e){
        for(AbilityStyle s:styles){
            for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
                net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
                //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

                    //Bukkit.broadcastMessage("Something cool died");
                if(Warzone.getZoneByLocation(ete.getBukkitEntity().getLocation()).equals(e.getZone())){
                    ((SummonerBiome)s).removeEntity(ete);}
            }}
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if ((e.getItemDrop().getItemStack().getType() == item)) {
            AbilityStyle s=getStyle(e.getPlayer());
            for(int i=0; i<((SummonerBiome) s).getActivecreatures().size(); i++){
                net.minecraft.server.v1_16_R3.Entity ete=((SummonerBiome) s).getActivecreatures().get(i);
                //Bukkit.broadcastMessage("Apparently "+String.valueOf(ete.getBukkitEntity().getEntityId())+" does not equal "+String.valueOf(e.getEntity().getEntityId()));

                if(((CustomEntity)ete).getPlayer().equals(e.getPlayer())){
                    //Bukkit.broadcastMessage("Something cool died");
                    ete.getBukkitEntity().remove();
                    ((SummonerBiome)s).removeEntity(ete);}
            }
            AbilityUtil.toggleAbilityStyle(e.getPlayer(), item);

            e.setCancelled(true);
        }
    }
}
