//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import java.util.Random;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity {
    private Player player;
    public CustomMagmaCube(Location loc, Player p, Team t, float power) {
        super(EntityTypes.MAGMA_CUBE, ((CraftWorld)loc.getWorld()).getHandle());
        this.player=p;


        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Magma Cube - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            Slime s=(Slime) this.getBukkitEntity();
            s.setSize(3);

            t.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Magma Cube"));
        }
    }
/*
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    public static boolean b(EntityTypes<EntityMagmaCube> var0, GeneratorAccess var1, EnumMobSpawn var2, BlockPosition var3, Random var4) {
        return var1.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean a(IWorldReader var0) {
        return var0.i(this) && !var0.containsLiquid(this.getBoundingBox());
    }

    public void setSize(int var0, boolean var1) {
        super.setSize(var0, var1);
        this.getAttributeInstance(GenericAttributes.ARMOR).setValue((double)(var0 * 3));
    }

    public float aI() {
        return 1.0F;
    }

    protected ParticleParam l() {
        return Particles.FLAME;
    }

    protected MinecraftKey getDefaultLootTable() {
        return this.ev() ? LootTables.a : this.getEntityType().h();
    }

    public boolean isBurning() {
        return false;
    }

    protected int eo() {
        return super.eo() * 4;
    }

    protected void ep() {
        this.b *= 0.9F;
    }

    protected void jump() {
        Vec3D var0 = this.getMot();
        this.setMot(var0.x, (double)(this.dp() + (float)this.getSize() * 0.1F), var0.z);
        this.impulse = true;
    }

    protected void c(Tag<FluidType> var0) {
        if (var0 == TagsFluid.LAVA) {
            Vec3D var1 = this.getMot();
            this.setMot(var1.x, (double)(0.22F + (float)this.getSize() * 0.05F), var1.z);
            this.impulse = true;
        } else {
            super.c(var0);
        }

    }

    public boolean b(float var0, float var1) {
        return false;
    }

    protected boolean eq() {
        return this.doAITick();
    }

    protected float er() {
        return super.er() + 2.0F;
    }

    protected SoundEffect getSoundHurt(DamageSource var0) {
        return this.ev() ? SoundEffects.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_HURT;
    }

    protected SoundEffect getSoundDeath() {
        return this.ev() ? SoundEffects.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_DEATH;
    }

    protected SoundEffect getSoundSquish() {
        return this.ev() ? SoundEffects.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_SQUISH;
    }

    protected SoundEffect getSoundJump() {
        return SoundEffects.ENTITY_MAGMA_CUBE_JUMP;
    }
*/
    @Override
    public Player getPlayer() {
        return this.player;
    }
}
