package com.cybercultivation.block.entity;

import com.cybercultivation.alchemy.AlchemyCauldronRecipes;
import com.cybercultivation.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AlchemyCauldronBlockEntity extends BlockEntity {
    public static final int SLOT_COUNT = 4;
    private static final int BREW_TIME_TICKS = 20 * 8;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int brewTicks;

    public AlchemyCauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ALCHEMY_CAULDRON, pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemyCauldronBlockEntity blockEntity) {
        if (!blockEntity.isBrewing()) {
            return;
        }

        blockEntity.brewTicks--;
        if (level instanceof ServerLevel serverLevel && blockEntity.brewTicks % 10 == 0) {
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                    6, 0.25D, 0.20D, 0.25D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    3, 0.20D, 0.10D, 0.20D, 0.01D);
        }

        if (blockEntity.brewTicks <= 0) {
            blockEntity.finishBrewing(level, pos);
        }
        setChanged(level, pos, state);
    }

    public boolean isBrewing() {
        return brewTicks > 0;
    }

    public boolean canAccept(ItemStack stack) {
        return !stack.isEmpty()
                && !isBrewing()
                && hasFreeSlot()
                && AlchemyCauldronRecipes.canUseAsIngredient(stack.getItem(), items);
    }

    public boolean addIngredient(ItemStack stack) {
        if (!canAccept(stack)) {
            return false;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copyWithCount(1));
                setChanged();
                startBrewingIfReady();
                return true;
            }
        }
        return false;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void clearIngredients() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        brewTicks = 0;
        setChanged();
    }

    private void startBrewingIfReady() {
        if (AlchemyCauldronRecipes.match(items) != null) {
            brewTicks = BREW_TIME_TICKS;
        }
    }

    private void finishBrewing(Level level, BlockPos pos) {
        AlchemyCauldronRecipes.AlchemyRecipe recipe = AlchemyCauldronRecipes.match(items);
        if (recipe == null) {
            brewTicks = 0;
            return;
        }

        ItemStack result = recipe.result().copy();
        clearIngredients();
        ItemEntity entity = new ItemEntity(level,
                pos.getX() + 0.5D,
                pos.getY() + 1.15D,
                pos.getZ() + 0.5D,
                result,
                0.0D,
                0.08D,
                0.0D);
        level.addFreshEntity(entity);
    }

    private boolean hasFreeSlot() {
        for (ItemStack stack : items) {
            if (stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        ContainerHelper.loadAllItems(tag, items, registries);
        brewTicks = tag.getInt("BrewTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("BrewTicks", brewTicks);
    }

    public List<ItemStack> getItemView() {
        return List.copyOf(items);
    }
}
