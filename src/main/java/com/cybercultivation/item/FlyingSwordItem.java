package com.cybercultivation.item;

import com.cybercultivation.flysword.FlyingSwordHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlyingSwordItem extends Item {
    public FlyingSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            FlyingSwordHandler.toggle(serverPlayer, true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
