package com.cybercultivation.block.custom;

import com.cybercultivation.item.ModItems;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpiritRiceCropBlock extends CropBlock {
    public SpiritRiceCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.SPIRIT_RICE_SEEDS;
    }
}
