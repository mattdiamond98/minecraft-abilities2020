package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.CoronaCraft;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;

public class GiantSlime extends EntitySlime implements CustomEntity {
    Player player;
    Boolean inair=false;
    public GiantSlime(Location loc, Player p, Team t) {
        super(EntityTypes.SLIME, (((CraftWorld)loc.getWorld()).getHandle()));
        this.player=p;


        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"GIANT SLIME - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            Bukkit.broadcastMessage(this.getBukkitEntity().getType().name());
            Slime s=(Slime) this.getBukkitEntity();

            //java.lang.ClassCastException: org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity cannot be cast to org.bukkit.entity.Slime
            //21.08 21:42:39 [Server] INFO at com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities.GiantSlime.<init>(GiantSlime.java:50) ~[?:?]
            s.setSize(10);
            s.setHealth(50);
            t.addNonPlayerEntity(this.getBukkitEntity());}else{

            this.setCustomName(new ChatComponentText("GIANT SLIME"));
        }
        final GiantSlime gs=this;
        BukkitRunnable b=new BukkitRunnable() {
            @Override
            public void run() {
                if(!gs.getBukkitEntity().getLocation().add(0, -1, 0).getBlock().getType().isSolid()){
                    gs.setInair(true);
                }else if(gs.getInair()){
                    gs.setInair(false);
                    gs.getBukkitEntity().getWorld().createExplosion(gs.getBukkitEntity().getLocation(), 2);
                }
            }
        };

        b.runTaskTimer(CoronaCraft.instance, 0L, 1L);
    }
    public void setInair(Boolean b){
        inair=b;
    }
    public boolean getInair(){
        return inair;
    }
    @Override
    public void initPathfinder(){

        this.goalSelector.a(0, new GiantSlime.PathfinderGoalSlimeHelpTeam(this));
       // this.goalSelector.a(1, new GiantSlime.PathfinderGoalSlimeRandomDirection(this));
        //this.targetSelector.a(0, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));
      //  this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }



    static class PathfinderGoalSlimeRandomJump
            extends PathfinderGoal {
        private final EntitySlime a;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().d(true);
        }

        @Override
        public boolean a() {
            return (this.a.isInWater() || this.a.aH());
        }
    }
    static class PathfinderGoalSlimeRandomDirection
            extends PathfinderGoal {
        private final EntitySlime a;
        private float b;
        private int c;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.a = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return false;
        }
    }
   static class PathfinderGoalSlimeHelpTeam extends PathfinderGoal{
        private final EntitySlime a;

       PathfinderGoalSlimeHelpTeam(EntitySlime a) {
           this.a = a;
       }


       @Override
       public boolean a() {

           return Team.getNonPlayerEntityTeam(this.a.getBukkitEntity())!=null;
       }
       @Override
       public void c(){
          Entity et = null;
          double distance=1000F;

          for(Entity e: this.a.getBukkitEntity().getNearbyEntities(30, 30, 30)){
              if(e instanceof Player){
                  if(Team.getTeamByPlayerName(e.getName())!=null&&Team.getTeamByPlayerName(e.getName())!=Team.getNonPlayerEntityTeam(this.a.getBukkitEntity())&&e.getLocation().distanceSquared(this.a.getBukkitEntity().getLocation())<distance){
                      et=e;
                      distance=e.getLocation().distanceSquared(this.a.getBukkitEntity().getLocation());


                  }
              }else{
                  if(Team.getNonPlayerEntityTeam(e)!=null&&Team.getNonPlayerEntityTeam(e)!=Team.getNonPlayerEntityTeam(this.a.getBukkitEntity())&&e.getLocation().distanceSquared(this.a.getBukkitEntity().getLocation())<distance){
                      et=e;
                      distance=e.getLocation().distanceSquared(this.a.getBukkitEntity().getLocation());
                  }

              }

          }
          if(et!=null){
              this.a.setGoalTarget((EntityLiving) ((CraftEntity)et).getHandle());
          }




       }
       public boolean b(){

           return !this.a.getNavigation().m();
       }
   }


}
