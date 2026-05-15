package com.cybercultivation.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum ModToolMaterials implements Tier {
    XUAN_IRON(320, 6.5F, 2.5F, 14, () -> Ingredient.of(ModItems.MIDDLE_GRADE_SPIRIT_STONE)),
    SPIRIT_STONE(900, 7.5F, 3.5F, 16, () -> Ingredient.of(ModItems.SPIRIT_STONE)),
    MYSTIC_JADE(1320, 8.0F, 4.5F, 18, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    STARFALL(1800, 8.5F, 5.0F, 17, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    FLAME_SPIRIT(1100, 8.0F, 4.0F, 16, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    FROST_SPIRIT(1050, 8.0F, 3.5F, 16, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    THUNDER_SPIRIT(1150, 8.5F, 4.5F, 17, () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)),
    WIND_SPIRIT(850, 9.0F, 3.0F, 18, () -> Ingredient.of(ModItems.MIDDLE_GRADE_SPIRIT_STONE)),
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
