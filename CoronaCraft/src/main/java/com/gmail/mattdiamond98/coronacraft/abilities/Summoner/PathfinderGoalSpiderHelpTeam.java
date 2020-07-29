package com.gmail.mattdiamond98.coronacraft.abilities.Summoner;

import com.tommytony.war.Team;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntitySpider;

public class PathfinderGoalSpiderHelpTeam<T extends EntityLiving> extends PathfinderGoalHelpTeam<T> {
    public PathfinderGoalSpiderHelpTeam(EntitySpider entityspider, Class<T> oclass, Team t) {
        super(entityspider, oclass, true, t);
    }

    public boolean a() {
        float f = this.e.aI();
        return f >= 0.5F ? false : super.a();
    }
}

