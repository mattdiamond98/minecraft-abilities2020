//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import java.util.EnumSet;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Utility.PathfinderGoalHelpTeam;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

public class CustomBlaze extends EntityBlaze implements CustomEntity {
    private float b = 0.5F;
    private int c;
    private static final DataWatcherObject<Byte> d;
    private Player player;
    public CustomBlaze(Location loc, Player p, Team t, float power) {
        super(EntityTypes.BLAZE, ((CraftWorld)loc.getWorld()).getHandle());
        this.player=p;
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 8.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
        this.f = 10;
        this.setPosition(loc.getX(),loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Blaze - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            t.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Blaze"));
        }
    }

    protected void initPathfinder() {
        this.goalSelector.a(4, new CustomBlaze.PathfinderGoalBlazeFireball(this));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalHelpTeam<EntityLiving>(this, EntityLiving.class, true));
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eR().a(GenericAttributes.ATTACK_DAMAGE, 6.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(d, (byte)0);
    }

    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_BLAZE_AMBIENT;
    }

    protected SoundEffect getSoundHurt(DamageSource var0) {
        return SoundEffects.ENTITY_BLAZE_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_BLAZE_DEATH;
    }

    public float aR() {
        return 1.0F;
    }

    public void movementTick() {
        if (!this.onGround && this.getMot().y < 0.0D) {
            this.setMot(this.getMot().d(1.0D, 0.6D, 1.0D));
        }

        if (this.world.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.world.a(this.locX() + 0.5D, this.locY() + 0.5D, this.locZ() + 0.5D, SoundEffects.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int var0 = 0; var0 < 2; ++var0) {
                this.world.addParticle(Particles.LARGE_SMOKE, this.d(0.5D), this.cF(), this.g(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.movementTick();
    }

    public boolean dO() {
        return true;
    }

    protected void mobTick() {
        --this.c;
        if (this.c <= 0) {
            this.c = 100;
            this.b = 0.5F + (float)this.random.nextGaussian() * 3.0F;
        }

        EntityLiving var0 = this.getGoalTarget();
        if (var0 != null && var0.getHeadY() > this.getHeadY() + (double)this.b && this.c(var0)) {
            Vec3D var1 = this.getMot();
            this.setMot(this.getMot().add(0.0D, (0.30000001192092896D - var1.y) * 0.30000001192092896D, 0.0D));
            this.impulse = true;
        }

        super.mobTick();
    }

    public boolean b(float var0, float var1) {
        return false;
    }

    public boolean isBurning() {
        return this.eK();
    }

    private boolean eK() {
        return ((Byte)this.datawatcher.get(d) & 1) != 0;
    }

    private void t(boolean var0) {
        byte var1 = (Byte)this.datawatcher.get(d);
        if (var0) {
            var1 = (byte)(var1 | 1);
        } else {
            var1 &= -2;
        }

        this.datawatcher.set(d, var1);
    }

    static {
        d = DataWatcher.a(EntityBlaze.class, DataWatcherRegistry.a);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    static class PathfinderGoalBlazeFireball extends PathfinderGoal {
        private final CustomBlaze a;
        private int b;
        private int c;
        private int d;

        public PathfinderGoalBlazeFireball(CustomBlaze var0) {
            this.a = var0;
            this.a(EnumSet.of(Type.MOVE, Type.LOOK));
        }

        public boolean a() {
            EntityLiving var0 = this.a.getGoalTarget();
            return var0 != null && var0.isAlive() && this.a.c(var0);
        }

        public void c() {
            this.b = 0;
        }

        public void d() {
            this.a.t(false);
            this.d = 0;
        }

        public void e() {
            --this.c;
            EntityLiving var0 = this.a.getGoalTarget();
            if (var0 != null) {
                boolean var1 = this.a.getEntitySenses().a(var0);
                if (var1) {
                    this.d = 0;
                } else {
                    ++this.d;
                }

                double var2 = this.a.h(var0);
                if (var2 < 4.0D) {
                    if (!var1) {
                        return;
                    }

                    if (this.c <= 0) {
                        this.c = 20;
                        this.a.attackEntity(var0);
                    }

                    this.a.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
                } else if (var2 < this.g() * this.g() && var1) {
                    double var4 = var0.locX() - this.a.locX();
                    double var6 = var0.e(0.5D) - this.a.e(0.5D);
                    double var8 = var0.locZ() - this.a.locZ();
                    if (this.c <= 0) {
                        ++this.b;
                        if (this.b == 1) {
                            this.c = 60;
                            this.a.t(true);
                        } else if (this.b <= 4) {
                            this.c = 6;
                        } else {
                            this.c = 100;
                            this.b = 0;
                            this.a.t(false);
                        }

                        if (this.b > 1) {
                            float var10 = MathHelper.c(MathHelper.sqrt(var2)) * 0.5F;
                            if (!this.a.isSilent()) {
                                this.a.world.a((EntityHuman)null, 1018, this.a.getChunkCoordinates(), 0);
                            }

                            for(int var11 = 0; var11 < 1; ++var11) {
                                EntitySmallFireball var12 = new EntitySmallFireball(this.a.world, this.a, var4 + this.a.getRandom().nextGaussian() * (double)var10, var6, var8 + this.a.getRandom().nextGaussian() * (double)var10);
                                var12.setPosition(var12.locX(), this.a.e(0.5D) + 0.5D, var12.locZ());
                                this.a.world.addEntity(var12);
                            }
                        }
                    }

                    this.a.getControllerLook().a(var0, 10.0F, 10.0F);
                } else if (this.d < 5) {
                    this.a.getControllerMove().a(var0.locX(), var0.locY(), var0.locZ(), 1.0D);
                }

                super.e();
            }
        }

        private double g() {
            return this.a.b(GenericAttributes.FOLLOW_RANGE);
        }
    }
}
