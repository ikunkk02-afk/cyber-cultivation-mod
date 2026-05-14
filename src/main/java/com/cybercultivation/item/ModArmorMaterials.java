package com.cybercultivation.item;

import com.cybercultivation.CyberCultivationMod;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ModArmorMaterials {
    public static final Holder<ArmorMaterial> XUAN_IRON = register(
            "xuan_iron", 18, 12, 0.5F, 0.0F,
            3, 7, 6, 3,
            () -> Ingredient.of(ModItems.SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> SPIRIT_STONE = register(
            "spirit_stone", 26, 16, 1.8F, 0.0F,
            3, 8, 6, 3,
            () -> Ingredient.of(ModItems.SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> MYSTIC_JADE = register(
            "mystic_jade", 34, 20, 2.6F, 0.0F,
            4, 9, 7, 4,
            () -> Ingredient.of(ModItems.MIDDLE_GRADE_SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> STARFALL = register(
            "starfall", 39, 16, 3.0F, 0.08F,
            4, 9, 7, 4,
            () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> HEAVENLY_DAO = register(
            "heavenly_dao", 45, 24, 4.0F, 0.08F,
            5, 10, 8, 5,
            () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> HUMAN_DAO = register(
            "human_dao", 48, 22, 3.5F, 0.08F,
            5, 10, 8, 5,
            () -> Ingredient.of(ModItems.HIGH_GRADE_SPIRIT_STONE)
    );
    public static final Holder<ArmorMaterial> DEMON_DAO = register(
            "demon_dao", 42, 20, 4.0F, 0.1F,
            4, 10, 8, 4,
            () -> Ingredient.of(ModItems.DEMON_CORE)
    );

    private ModArmorMaterials() {
    }

    private static Holder<ArmorMaterial> register(String name,
                                                  int durabilityMultiplier,
                                                  int enchantmentValue,
                                                  float toughness,
                                                  float knockbackResistance,
                                                  int helmetDefense,
                                                  int chestplateDefense,
                                                  int leggingsDefense,
                                                  int bootsDefense,
                                                  Supplier<Ingredient> repairIngredient) {
        Map<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.HELMET, helmetDefense);
            map.put(ArmorItem.Type.CHESTPLATE, chestplateDefense);
            map.put(ArmorItem.Type.LEGGINGS, leggingsDefense);
            map.put(ArmorItem.Type.BOOTS, bootsDefense);
            map.put(ArmorItem.Type.BODY, chestplateDefense);
        });
        ResourceLocation layerId = ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name);
        ArmorMaterial material = new ArmorMaterial(
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(layerId)),
                toughness,
                knockbackResistance
        );
        return Holder.direct(material);
    }

    public static ItemDurability durability(ArmorItem.Type type, int multiplier) {
        return new ItemDurability(type.getDurability(multiplier));
    }

    public record ItemDurability(int value) {
    }
}
