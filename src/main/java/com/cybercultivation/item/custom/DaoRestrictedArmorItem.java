package com.cybercultivation.item.custom;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DaoRestrictedArmorItem extends ArmorItem {
    public static final Component WRONG_PATH_MESSAGE = Component.literal("你的道统无法承载这件装备。");

    private final CultivationPath requiredPath;

    public DaoRestrictedArmorItem(Holder<ArmorMaterial> material,
                                  Type type,
                                  Properties properties,
                                  CultivationPath requiredPath) {
        super(material, type, properties);
        this.requiredPath = requiredPath;
    }

    public CultivationPath getRequiredPath() {
        return requiredPath;
    }

    public boolean canWear(Player player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        return data.getSelectedPath() == requiredPath;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && !canWear(player)) {
            player.displayClientMessage(WRONG_PATH_MESSAGE, true);
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }

    public static boolean isAllowedFor(ServerPlayer player, ItemStack stack) {
        return !(stack.getItem() instanceof DaoRestrictedArmorItem armorItem) || armorItem.canWear(player);
    }
}
