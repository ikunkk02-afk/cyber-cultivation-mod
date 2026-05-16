package com.cybercultivation.block.custom;

import com.cybercultivation.block.ModBlockEntities;
import com.cybercultivation.block.entity.AlchemyCauldronBlockEntity;
import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AlchemyCauldronBlock extends Block implements EntityBlock {
    public AlchemyCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemyCauldronBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.ALCHEMY_CAULDRON) {
            return null;
        }
        return (tickLevel, pos, tickState, blockEntity) ->
                AlchemyCauldronBlockEntity.serverTick(tickLevel, pos, tickState, (AlchemyCauldronBlockEntity) blockEntity);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack,
                                              BlockState state,
                                              Level level,
                                              BlockPos pos,
                                              Player player,
                                              InteractionHand hand,
                                              BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof AlchemyCauldronBlockEntity cauldron)) {
            return ItemInteractionResult.FAIL;
        }
        if (!canUseAlchemy(player)) {
            player.displayClientMessage(Component.literal("§c只有丹修主业或副业才能使用炼药锅。"), true);
            return ItemInteractionResult.FAIL;
        }
        if (cauldron.isBrewing()) {
            player.displayClientMessage(Component.literal("§e炼药锅正在成丹，稍候再投入材料。"), true);
            return ItemInteractionResult.FAIL;
        }
        if (!cauldron.canAccept(stack)) {
            player.displayClientMessage(Component.literal("§c此材料无法加入当前炼药锅配方。"), true);
            return ItemInteractionResult.FAIL;
        }

        cauldron.addIngredient(stack);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.displayClientMessage(Component.literal("§a材料入炉。"), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof AlchemyCauldronBlockEntity cauldron) {
            if (cauldron.isBrewing()) {
                player.displayClientMessage(Component.literal("§c炼制过程中无法清空炼药锅。"), true);
                return InteractionResult.FAIL;
            }
            Containers.dropContents(level, pos, cauldron.getItems());
            cauldron.clearIngredients();
            player.displayClientMessage(Component.literal("§e炼药锅已清空。"), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AlchemyCauldronBlockEntity cauldron) {
            Containers.dropContents(level, pos, cauldron.getItems());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) != 0) {
            return;
        }
        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
        double y = pos.getY() + 1.05D + random.nextDouble() * 0.12D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
        level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0.0D, 0.04D, 0.0D);
    }

    private static boolean canUseAlchemy(Player player) {
        if (player.isCreative()) {
            return true;
        }
        PlayerQiData data = PlayerQiManager.get(player);
        return data != null
                && (data.getMainDiscipline() == CultivationDiscipline.ALCHEMY
                || data.getSubDisciplines().contains(CultivationDiscipline.ALCHEMY));
    }
}
