package com.cybercultivation.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum ModToolMaterials implements Tier {
    HEAVENLY_JUDGEMENT(2300, 10.0F, 6.0F, 18, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    HUMAN_MERIT(2400, 9.5F, 5.0F, 20, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    DEMON_SOUL(2100, 10.5F, 7.0F, 16, () -> Ingredient.of(ModItems.DEMON_CORE));

    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterials(int uses,
                     float speed,
                     float attackDamageBonus,
                     int enchantmentValue,
                     Supplier<Ingredient> repairIngredient) {
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
