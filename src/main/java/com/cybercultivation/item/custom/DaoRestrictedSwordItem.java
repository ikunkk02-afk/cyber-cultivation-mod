package com.cybercultivation.item.custom;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class DaoRestrictedSwordItem extends SwordItem {
    public static final Component WRONG_PATH_MESSAGE = Component.literal("你的道统无法驾驭这把武器。");

    private final CultivationPath requiredPath;

    public DaoRestrictedSwordItem(Tier tier, Properties properties, CultivationPath requiredPath) {
        super(tier, properties);
        this.requiredPath = requiredPath;
    }

    public CultivationPath getRequiredPath() {
        return requiredPath;
    }

    public boolean canUse(Player player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        return data.getSelectedPath() == requiredPath;
    }

    public static boolean isRestrictedWrongPath(Player player, ItemStack stack) {
        return stack.getItem() instanceof DaoRestrictedSwordItem swordItem && !swordItem.canUse(player);
    }
}
