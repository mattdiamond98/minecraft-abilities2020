package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;


import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.v1_15_R1.PathfinderGoal.Type;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class PathfinderGoalHelpTeam<T extends EntityLiving> extends PathfinderGoalTarget {
    protected final Class<T> a;
    protected final int b;
    protected EntityLiving c;
    protected PathfinderTargetCondition d;
    protected Team team;

    public PathfinderGoalHelpTeam(EntityInsentient entityinsentient, Class<T> oclass, boolean flag, Team t) {
        this(entityinsentient, oclass, flag, false, t);
    }

    public PathfinderGoalHelpTeam(EntityInsentient entityinsentient, Class<T> oclass, boolean flag, boolean flag1, Team t) {
        this(entityinsentient, oclass, 10, flag, flag1, (Predicate)null, t);
    }

    public PathfinderGoalHelpTeam(EntityInsentient entityinsentient, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable Predicate<EntityLiving> predicate, Team t) {
        super(entityinsentient, flag, flag1);
        Predicate<EntityLiving> predicatey=new Predicate<EntityLiving>() {
            @Override
            public boolean test(EntityLiving EL) {
              if(EL instanceof EntityPlayer) {
                  return team.getPlayers().contains((Player)EL.getBukkitEntity());
              }else{return false;}
            }
        };
         this.a = oclass;
        this.b = i;
        this.a(EnumSet.of(Type.TARGET));
        this.d = (new PathfinderTargetCondition()).a(this.k()).a(predicate);
        this.team=t;

    }

    public boolean a() {
        if (this.b > 0 && this.e.getRandom().nextInt(this.b) != 0) {
            return false;
        } else {
            this.g();
            return this.c != null;
        }
    }

    protected AxisAlignedBB a(double d0) {
        return this.e.getBoundingBox().grow(d0, 4.0D, d0);
    }

    protected void g() {
        if (this.a != EntityHuman.class && this.a != EntityPlayer.class) {
            this.c = this.e.world.b(this.a, this.d, this.e, this.e.locX(), this.e.getHeadY(), this.e.locZ(), this.a(this.k()));
        } else {
            this.c = this.e.world.a(this.d, this.e, this.e.locX(), this.e.getHeadY(), this.e.locZ());
        }

    }

    public void c() {
        this.e.setGoalTarget(this.c, this.c instanceof EntityPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
        super.c();
    }
}
