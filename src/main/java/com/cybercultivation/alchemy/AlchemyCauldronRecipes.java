package com.cybercultivation.alchemy;

import com.cybercultivation.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class AlchemyCauldronRecipes {
    private static final List<AlchemyRecipe> RECIPES = List.of(
            recipe(ModItems.QI_RECOVERY_PILL, 2, ModItems.SPIRIT_HERB, ModItems.LOW_GRADE_SPIRIT_STONE, ModItems.ALCHEMY_ESSENCE, Items.GLOW_BERRIES),
            recipe(ModItems.QI_GATHERING_PILL, 1, ModItems.SPIRIT_HERB, ModItems.SPIRIT_STONE, Items.AMETHYST_SHARD, ModItems.ALCHEMY_ESSENCE),
            recipe(ModItems.FOUNDATION_PILL, 1, ModItems.EARTH_ROOT_GINSENG, ModItems.MIDDLE_GRADE_SPIRIT_STONE, ModItems.ALCHEMY_ESSENCE, Items.GOLDEN_APPLE),
            recipe(ModItems.HEART_CLEANSING_PILL, 1, ModItems.MOONLIGHT_LOTUS, ModItems.TALISMAN_PAPER, Items.HONEY_BOTTLE, ModItems.ALCHEMY_ESSENCE),
            recipe(ModItems.BODY_TEMPERING_PILL, 1, ModItems.EARTH_ROOT_GINSENG, ModItems.BEAST_CORE, Items.IRON_INGOT, ModItems.ALCHEMY_ESSENCE),
            recipe(ModItems.DEMON_BLOOD_PILL, 1, ModItems.DEMON_BLOOD_VINE, ModItems.DEMON_CORE, Items.BLAZE_POWDER, ModItems.ALCHEMY_ESSENCE),
            recipe(ModItems.HEALING_PILL, 2, ModItems.SPIRIT_HERB, Items.GLISTERING_MELON_SLICE, ModItems.ALCHEMY_ESSENCE),
            recipe(ModItems.DETOX_PILL, 2, ModItems.FROST_FLOWER, Items.SUGAR, ModItems.ALCHEMY_ESSENCE)
    );

    private AlchemyCauldronRecipes() {
    }

    public static boolean canUseAsIngredient(Item item, List<ItemStack> currentItems) {
        for (AlchemyRecipe recipe : RECIPES) {
            if (recipe.canAccept(item, currentItems)) {
                return true;
            }
        }
        return false;
    }

    public static AlchemyRecipe match(List<ItemStack> items) {
        for (AlchemyRecipe recipe : RECIPES) {
            if (recipe.matches(items)) {
                return recipe;
            }
        }
        return null;
    }

    private static AlchemyRecipe recipe(Item result, int count, Item... ingredients) {
        return new AlchemyRecipe(new ItemStack(result, count), List.of(ingredients));
    }

    public record AlchemyRecipe(ItemStack result, List<Item> ingredients) {
        public boolean canAccept(Item item, List<ItemStack> currentItems) {
            if (!ingredients.contains(item)) {
                return false;
            }
            int currentCount = 0;
            for (ItemStack stack : currentItems) {
                if (!stack.isEmpty() && stack.is(item)) {
                    currentCount++;
                }
            }
            return currentCount < requiredCount(item);
        }

        public boolean matches(List<ItemStack> items) {
            int nonEmpty = 0;
            for (ItemStack stack : items) {
                if (!stack.isEmpty()) {
                    nonEmpty++;
                    if (!ingredients.contains(stack.getItem())) {
                        return false;
                    }
                }
            }
            if (nonEmpty != ingredients.size()) {
                return false;
            }
            for (Item ingredient : ingredients) {
                if (countItems(items, ingredient) != requiredCount(ingredient)) {
                    return false;
                }
            }
            return true;
        }

        private int requiredCount(Item item) {
            int count = 0;
            for (Item ingredient : ingredients) {
                if (ingredient == item) {
                    count++;
                }
            }
            return count;
        }

        private static int countItems(List<ItemStack> items, Item item) {
            int count = 0;
            for (ItemStack stack : items) {
                if (!stack.isEmpty() && stack.is(item)) {
                    count++;
                }
            }
            return count;
        }
    }
}
