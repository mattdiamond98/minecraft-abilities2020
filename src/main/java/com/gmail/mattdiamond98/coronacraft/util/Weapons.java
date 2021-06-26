package com.gmail.mattdiamond98.coronacraft.util;


import com.sun.istack.internal.NotNull;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.rmi.CORBA.Util;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class Weapons {
    public void damagePlayer(Player p, double damage) {
        double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = getEPF(p.getInventory());

        p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
    }

    public double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
        return withEnchants;
    }
    public double unCalculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage / (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness / (1 - (resistance * 0.2));
        double withEnchants = withResistance / (1 - (Math.min(20.0, epf) / 25));
        return withEnchants;
    }

    public static int getEPF(PlayerInventory inv) {
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
    }

    public final static Map<Material,Property> ALL_WEAPONS = new EnumMap<>(Material.class);

    static {
        add(Material.WOODEN_SWORD, 3, -2.4);
        add(Material.STONE_SWORD, 4, -2.4);
        add(Material.IRON_SWORD, 5, -2.4);
        add(Material.GOLDEN_SWORD, 3, -2.4);
        add(Material.DIAMOND_SWORD, 6, -2.4);

        add(Material.WOODEN_AXE, 6, -3.0);
        add(Material.STONE_AXE, 8, -3.0);
        add(Material.IRON_AXE, 8, -3.1);
        add(Material.GOLDEN_AXE, 6, -3.0);
        add(Material.DIAMOND_AXE, 8, -3.0);

        add(Material.WOODEN_HOE, 0, -3.0);
        add(Material.STONE_HOE, 1, -2.0);
        add(Material.IRON_HOE, 2, -1.0);
        add(Material.GOLDEN_HOE, 0, -3.0);
        add(Material.DIAMOND_HOE, 3, 0.0);

        add(Material.WOODEN_PICKAXE, 1, -2.8);
        add(Material.STONE_PICKAXE, 2, -2.8);
        add(Material.IRON_PICKAXE, 3, -2.8);
        add(Material.GOLDEN_PICKAXE, 1, -2.8);
        add(Material.DIAMOND_PICKAXE, 4, -2.8);

        add(Material.WOODEN_SHOVEL, 1.5, -3.0);
        add(Material.STONE_SHOVEL, 2.5, -3.0);
        add(Material.IRON_SHOVEL, 3.5, -3.0);
        add(Material.GOLDEN_SHOVEL, 1.5, -3.0);
        add(Material.DIAMOND_SHOVEL, 4.5, -3.0);

        add(Material.TRIDENT, 8, -2.9);
    }

    private static void add(Material material, double attackDamage, double attackSpeed) {
        ALL_WEAPONS.put(material, new Property(attackDamage, attackSpeed));
    }

    public static Optional<Double> getDefaultDamage(@NotNull Material material) {
        return Optional.ofNullable(ALL_WEAPONS.get(material)).map(Property::getAttackDamage);
    }

    public static Optional<Double> getDefaultAttackSpeed(@NotNull Material type) {
        return Optional.ofNullable(ALL_WEAPONS.get(type)).map(Property::getAttackSpeed);
    }

    private static class Property {
        private final double attackDamage;
        private final double attackSpeed;

        Property(double attackDamage, double attackSpeed) {
            this.attackDamage = attackDamage;
            this.attackSpeed = attackSpeed;
        }

        public double getAttackDamage() {
            return attackDamage;
        }

        public double getAttackSpeed() {
            return attackSpeed;
        }
    }
}