package com.gmail.mattdiamond98.coronacraft.abilities.Summoner.Entities;

import com.gmail.mattdiamond98.coronacraft.abilities.Summoner.CustomEntity;
import com.tommytony.war.Team;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityPigZombie;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomZombiePigman extends EntityPigZombie implements CustomEntity {
    private Team team;
    private Player hi;
    private Float power;
    public CustomZombiePigman(Location loc, Player p, Team t) {
        super(EntityTypes.ZOMBIFIED_PIGLIN, ((CraftWorld)loc.getWorld()).getHandle());
        this.team=t;
        this.hi=p;

        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        if(t!=null){
            this.setCustomName(new ChatComponentText(t.getKind().getColor() +"Zombie Pigman - "+t.getName()+" Team"));
            this.setCustomNameVisible(true);
            Zombie nicezombie=(Zombie) this.getBukkitEntity();
            ItemStack helmet=t.getKind().getHat();
            ItemMeta im=helmet.getItemMeta();
            im.setUnbreakable(true);
            helmet.setItemMeta(im);

            nicezombie.getEquipment().setHelmet(helmet);
            nicezombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD, 1));
            nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(nicezombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()*1.1);
            this.team.addNonPlayerEntity(this.getBukkitEntity());}else{
            this.setCustomName(new ChatComponentText("Zombie Pigman"));
        }
    }

    @Override
    public Player getPlayer() {
        return this.hi;
    }


}
