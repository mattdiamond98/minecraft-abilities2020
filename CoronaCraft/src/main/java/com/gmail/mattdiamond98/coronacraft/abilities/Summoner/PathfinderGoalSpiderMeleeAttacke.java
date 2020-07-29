package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySpider;
import net.minecraft.server.v1_15_R1.PathfinderGoalMeleeAttack;

public class PathfinderGoalSpiderMeleeAttacke extends PathfinderGoalMeleeAttack {
    public PathfinderGoalSpiderMeleeAttacke(EntitySpider entityspider) {
        super(entityspider, 1.0D, true);
    }

    public boolean a() {
        return super.a() && !this.a.isVehicle();
    }

    public boolean b() {
        float f = this.a.aI();
        if (f >= 0.5F && this.a.getRandom().nextInt(100) == 0) {
            this.a.setGoalTarget((EntityLiving)null);
            return false;
        } else {
            return super.b();
        }
    }

    protected double a(EntityLiving entityliving) {
        return (double)(4.0F + entityliving.getWidth());
    }
}