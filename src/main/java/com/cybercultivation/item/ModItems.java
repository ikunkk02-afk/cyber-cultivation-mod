package com.cybercultivation.item;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.block.ModBlocks;
import com.cybercultivation.cultivation.CultivationPath;
import com.cybercultivation.item.custom.DaoRestrictedArmorItem;
import com.cybercultivation.item.custom.DaoRestrictedSwordItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public final class ModItems {
    public static final ResourceKey<CreativeModeTab> CYBER_CULTIVATION_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            id("cyber_cultivation")
    );

    public static final Item CULTIVATION_ALTAR = register(
            "cultivation_altar",
            new BlockItem(ModBlocks.CULTIVATION_ALTAR, new Item.Properties())
    );

    public static final Item SPIRIT_STONE = register("spirit_stone", new Item(new Item.Properties()));
    public static final Item LOW_GRADE_SPIRIT_STONE = register("low_grade_spirit_stone", new Item(new Item.Properties()));
    public static final Item MIDDLE_GRADE_SPIRIT_STONE = register("middle_grade_spirit_stone", new Item(new Item.Properties()));
    public static final Item HIGH_GRADE_SPIRIT_STONE = register("high_grade_spirit_stone", new Item(new Item.Properties()));
    public static final Item SPIRIT_HERB = register("spirit_herb", new Item(new Item.Properties()));
    public static final Item DEMON_CORE = register("demon_core", new Item(new Item.Properties()));
    public static final Item BEAST_CORE = register("beast_core", new Item(new Item.Properties()));
    public static final Item TALISMAN_PAPER = register("talisman_paper", new Item(new Item.Properties()));
    public static final Item ARRAY_CORE = register("array_core", new Item(new Item.Properties()));
    public static final Item ALCHEMY_ESSENCE = register("alchemy_essence", new Item(new Item.Properties()));

    public static final Item SPIRIT_PEACH = register("spirit_peach", new Item(foodItem(4, 0.4F)));
    public static final Item SPIRIT_RICE = register("spirit_rice", new Item(foodItem(3, 0.3F)));
    public static final Item SPIRIT_MEAT = register("spirit_meat", new Item(foodItem(8, 0.8F)));
    public static final Item ROASTED_SPIRIT_MEAT = register("roasted_spirit_meat", new Item(foodItem(10, 1.0F)));

    public static final Item QI_RECOVERY_PILL = register("qi_recovery_pill", new Item(foodItem(1, 0.1F)));
    public static final Item QI_GATHERING_PILL = register("qi_gathering_pill", new Item(foodItem(1, 0.1F)));
    public static final Item FOUNDATION_PILL = register("foundation_pill", new Item(foodItem(1, 0.1F)));
    public static final Item HEART_CLEANSING_PILL = register("heart_cleansing_pill", new Item(foodItem(1, 0.1F)));
    public static final Item BODY_TEMPERING_PILL = register("body_tempering_pill", new Item(foodItem(1, 0.1F)));
    public static final Item DEMON_BLOOD_PILL = register("demon_blood_pill", new Item(foodItem(1, 0.1F)));
    public static final Item HEALING_PILL = register("healing_pill", new Item(foodItem(1, 0.1F)));
    public static final Item DETOX_PILL = register("detox_pill", new Item(foodItem(1, 0.1F)));

    public static final Item FLYING_SWORD = register(
            "flying_sword",
            new FlyingSwordItem(new Item.Properties().stacksTo(1))
    );

    public static final Item XUAN_IRON_HELMET = registerArmor("xuan_iron_helmet", ModArmorMaterials.XUAN_IRON, ArmorItem.Type.HELMET, 18);
    public static final Item XUAN_IRON_CHESTPLATE = registerArmor("xuan_iron_chestplate", ModArmorMaterials.XUAN_IRON, ArmorItem.Type.CHESTPLATE, 18);
    public static final Item XUAN_IRON_LEGGINGS = registerArmor("xuan_iron_leggings", ModArmorMaterials.XUAN_IRON, ArmorItem.Type.LEGGINGS, 18);
    public static final Item XUAN_IRON_BOOTS = registerArmor("xuan_iron_boots", ModArmorMaterials.XUAN_IRON, ArmorItem.Type.BOOTS, 18);

    public static final Item SPIRIT_STONE_HELMET = registerArmor("spirit_stone_helmet", ModArmorMaterials.SPIRIT_STONE, ArmorItem.Type.HELMET, 26);
    public static final Item SPIRIT_STONE_CHESTPLATE = registerArmor("spirit_stone_chestplate", ModArmorMaterials.SPIRIT_STONE, ArmorItem.Type.CHESTPLATE, 26);
    public static final Item SPIRIT_STONE_LEGGINGS = registerArmor("spirit_stone_leggings", ModArmorMaterials.SPIRIT_STONE, ArmorItem.Type.LEGGINGS, 26);
    public static final Item SPIRIT_STONE_BOOTS = registerArmor("spirit_stone_boots", ModArmorMaterials.SPIRIT_STONE, ArmorItem.Type.BOOTS, 26);

    public static final Item MYSTIC_JADE_HELMET = registerArmor("mystic_jade_helmet", ModArmorMaterials.MYSTIC_JADE, ArmorItem.Type.HELMET, 34);
    public static final Item MYSTIC_JADE_CHESTPLATE = registerArmor("mystic_jade_chestplate", ModArmorMaterials.MYSTIC_JADE, ArmorItem.Type.CHESTPLATE, 34);
    public static final Item MYSTIC_JADE_LEGGINGS = registerArmor("mystic_jade_leggings", ModArmorMaterials.MYSTIC_JADE, ArmorItem.Type.LEGGINGS, 34);
    public static final Item MYSTIC_JADE_BOOTS = registerArmor("mystic_jade_boots", ModArmorMaterials.MYSTIC_JADE, ArmorItem.Type.BOOTS, 34);

    public static final Item STARFALL_HELMET = registerArmor("starfall_helmet", ModArmorMaterials.STARFALL, ArmorItem.Type.HELMET, 39);
    public static final Item STARFALL_CHESTPLATE = registerArmor("starfall_chestplate", ModArmorMaterials.STARFALL, ArmorItem.Type.CHESTPLATE, 39);
    public static final Item STARFALL_LEGGINGS = registerArmor("starfall_leggings", ModArmorMaterials.STARFALL, ArmorItem.Type.LEGGINGS, 39);
    public static final Item STARFALL_BOOTS = registerArmor("starfall_boots", ModArmorMaterials.STARFALL, ArmorItem.Type.BOOTS, 39);

    public static final Item HEAVENLY_DAO_HELMET = registerDaoArmor("heavenly_dao_helmet", ModArmorMaterials.HEAVENLY_DAO, ArmorItem.Type.HELMET, 45, CultivationPath.HEAVENLY_DAO);
    public static final Item HEAVENLY_DAO_CHESTPLATE = registerDaoArmor("heavenly_dao_chestplate", ModArmorMaterials.HEAVENLY_DAO, ArmorItem.Type.CHESTPLATE, 45, CultivationPath.HEAVENLY_DAO);
    public static final Item HEAVENLY_DAO_LEGGINGS = registerDaoArmor("heavenly_dao_leggings", ModArmorMaterials.HEAVENLY_DAO, ArmorItem.Type.LEGGINGS, 45, CultivationPath.HEAVENLY_DAO);
    public static final Item HEAVENLY_DAO_BOOTS = registerDaoArmor("heavenly_dao_boots", ModArmorMaterials.HEAVENLY_DAO, ArmorItem.Type.BOOTS, 45, CultivationPath.HEAVENLY_DAO);

    public static final Item HUMAN_DAO_HELMET = registerDaoArmor("human_dao_helmet", ModArmorMaterials.HUMAN_DAO, ArmorItem.Type.HELMET, 48, CultivationPath.HUMAN_DAO);
    public static final Item HUMAN_DAO_CHESTPLATE = registerDaoArmor("human_dao_chestplate", ModArmorMaterials.HUMAN_DAO, ArmorItem.Type.CHESTPLATE, 48, CultivationPath.HUMAN_DAO);
    public static final Item HUMAN_DAO_LEGGINGS = registerDaoArmor("human_dao_leggings", ModArmorMaterials.HUMAN_DAO, ArmorItem.Type.LEGGINGS, 48, CultivationPath.HUMAN_DAO);
    public static final Item HUMAN_DAO_BOOTS = registerDaoArmor("human_dao_boots", ModArmorMaterials.HUMAN_DAO, ArmorItem.Type.BOOTS, 48, CultivationPath.HUMAN_DAO);

    public static final Item DEMON_DAO_HELMET = registerDaoArmor("demon_dao_helmet", ModArmorMaterials.DEMON_DAO, ArmorItem.Type.HELMET, 42, CultivationPath.DEMON_DAO);
    public static final Item DEMON_DAO_CHESTPLATE = registerDaoArmor("demon_dao_chestplate", ModArmorMaterials.DEMON_DAO, ArmorItem.Type.CHESTPLATE, 42, CultivationPath.DEMON_DAO);
    public static final Item DEMON_DAO_LEGGINGS = registerDaoArmor("demon_dao_leggings", ModArmorMaterials.DEMON_DAO, ArmorItem.Type.LEGGINGS, 42, CultivationPath.DEMON_DAO);
    public static final Item DEMON_DAO_BOOTS = registerDaoArmor("demon_dao_boots", ModArmorMaterials.DEMON_DAO, ArmorItem.Type.BOOTS, 42, CultivationPath.DEMON_DAO);

    public static final Item HEAVENLY_JUDGEMENT_SWORD = registerDaoSword(
            "heavenly_judgement_sword", ModToolMaterials.HEAVENLY_JUDGEMENT, 5, -2.4F, CultivationPath.HEAVENLY_DAO
    );
    public static final Item HUMAN_MERIT_SWORD = registerDaoSword(
            "human_merit_sword", ModToolMaterials.HUMAN_MERIT, 4, -2.2F, CultivationPath.HUMAN_DAO
    );
    public static final Item DEMON_SOUL_SWORD = registerDaoSword(
            "demon_soul_sword", ModToolMaterials.DEMON_SOUL, 6, -2.6F, CultivationPath.DEMON_DAO
    );

    public static final CreativeModeTab CYBER_CULTIVATION_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            CYBER_CULTIVATION_TAB_KEY,
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(SPIRIT_STONE))
                    .title(Component.translatable("itemGroup.cyber-cultivation-mod.cyber_cultivation"))
                    .displayItems((context, entries) -> {
                        entries.accept(CULTIVATION_ALTAR);

                        entries.accept(SPIRIT_STONE);
                        entries.accept(LOW_GRADE_SPIRIT_STONE);
                        entries.accept(MIDDLE_GRADE_SPIRIT_STONE);
                        entries.accept(HIGH_GRADE_SPIRIT_STONE);
                        entries.accept(SPIRIT_HERB);
                        entries.accept(DEMON_CORE);
                        entries.accept(BEAST_CORE);
                        entries.accept(TALISMAN_PAPER);
                        entries.accept(ARRAY_CORE);
                        entries.accept(ALCHEMY_ESSENCE);

                        entries.accept(SPIRIT_PEACH);
                        entries.accept(SPIRIT_RICE);
                        entries.accept(SPIRIT_MEAT);
                        entries.accept(ROASTED_SPIRIT_MEAT);

                        entries.accept(QI_RECOVERY_PILL);
                        entries.accept(QI_GATHERING_PILL);
                        entries.accept(FOUNDATION_PILL);
                        entries.accept(HEART_CLEANSING_PILL);
                        entries.accept(BODY_TEMPERING_PILL);
                        entries.accept(DEMON_BLOOD_PILL);
                        entries.accept(HEALING_PILL);
                        entries.accept(DETOX_PILL);

                        entries.accept(FLYING_SWORD);

                        entries.accept(XUAN_IRON_HELMET);
                        entries.accept(XUAN_IRON_CHESTPLATE);
                        entries.accept(XUAN_IRON_LEGGINGS);
                        entries.accept(XUAN_IRON_BOOTS);
                        entries.accept(SPIRIT_STONE_HELMET);
                        entries.accept(SPIRIT_STONE_CHESTPLATE);
                        entries.accept(SPIRIT_STONE_LEGGINGS);
                        entries.accept(SPIRIT_STONE_BOOTS);
                        entries.accept(MYSTIC_JADE_HELMET);
                        entries.accept(MYSTIC_JADE_CHESTPLATE);
                        entries.accept(MYSTIC_JADE_LEGGINGS);
                        entries.accept(MYSTIC_JADE_BOOTS);
                        entries.accept(STARFALL_HELMET);
                        entries.accept(STARFALL_CHESTPLATE);
                        entries.accept(STARFALL_LEGGINGS);
                        entries.accept(STARFALL_BOOTS);

                        entries.accept(HEAVENLY_DAO_HELMET);
                        entries.accept(HEAVENLY_DAO_CHESTPLATE);
                        entries.accept(HEAVENLY_DAO_LEGGINGS);
                        entries.accept(HEAVENLY_DAO_BOOTS);
                        entries.accept(HUMAN_DAO_HELMET);
                        entries.accept(HUMAN_DAO_CHESTPLATE);
                        entries.accept(HUMAN_DAO_LEGGINGS);
                        entries.accept(HUMAN_DAO_BOOTS);
                        entries.accept(DEMON_DAO_HELMET);
                        entries.accept(DEMON_DAO_CHESTPLATE);
                        entries.accept(DEMON_DAO_LEGGINGS);
                        entries.accept(DEMON_DAO_BOOTS);

                        entries.accept(HEAVENLY_JUDGEMENT_SWORD);
                        entries.accept(HUMAN_MERIT_SWORD);
                        entries.accept(DEMON_SOUL_SWORD);
                    })
                    .build()
    );

    private ModItems() {
    }

    public static void registerModItems() {
        Item.BY_BLOCK.put(ModBlocks.CULTIVATION_ALTAR, CULTIVATION_ALTAR);
        CyberCultivationMod.LOGGER.info("Registering Cyber Cultivation items");
    }

    private static Item.Properties foodItem(int nutrition, float saturationModifier) {
        return new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationModifier)
                .build());
    }

    private static Item registerArmor(String name, Holder<ArmorMaterial> material, ArmorItem.Type type, int durabilityMultiplier) {
        return register(name, new ArmorItem(material, type, armorProperties(type, durabilityMultiplier)));
    }

    private static Item registerDaoArmor(String name,
                                         Holder<ArmorMaterial> material,
                                         ArmorItem.Type type,
                                         int durabilityMultiplier,
                                         CultivationPath requiredPath) {
        return register(name, new DaoRestrictedArmorItem(material, type, armorProperties(type, durabilityMultiplier), requiredPath));
    }

    private static Item registerDaoSword(String name,
                                         Tier tier,
                                         int attackDamage,
                                         float attackSpeed,
                                         CultivationPath requiredPath) {
        return register(name, new DaoRestrictedSwordItem(
                tier,
                new Item.Properties()
                        .durability(tier.getUses())
                        .attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed)),
                requiredPath
        ));
    }

    private static Item.Properties armorProperties(ArmorItem.Type type, int durabilityMultiplier) {
        Item.Properties properties = new Item.Properties().durability(type.getDurability(durabilityMultiplier));
        if (durabilityMultiplier >= 39) {
            properties.fireResistant();
        }
        return properties;
    }

    private static Item register(String name, Item item) {
        return Registry.register(
                BuiltInRegistries.ITEM,
                id(name),
                item
        );
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, name);
    }
}
