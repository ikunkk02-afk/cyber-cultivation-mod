package com.cybercultivation.block.custom;

import com.cybercultivation.item.ModItems;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpiritHerbCropBlock extends CropBlock {
    public SpiritHerbCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos) {
        return state.is(Blocks.FARMLAND) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.PODZOL);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.SPIRIT_HERB_SEEDS;
    }
}
