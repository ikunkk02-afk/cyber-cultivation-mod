package com.cybercultivation.item;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.cultivation.CultivationPath;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class CultivationManualItem extends Item {
    public enum ManualType {
        PATH,
        DISCIPLINE
    }

    public static final int STUDY_REQUIRED_TICKS = 20 * 60;

    private final ManualType manualType;
    private final CultivationPath path;
    private final CultivationDiscipline discipline;
    private final CultivationManualRank rank;

    public CultivationManualItem(CultivationPath path, CultivationManualRank rank, Properties properties) {
        super(properties.stacksTo(1));
        this.manualType = ManualType.PATH;
        this.path = path;
        this.discipline = null;
        this.rank = rank;
    }

    public CultivationManualItem(CultivationDiscipline discipline, CultivationManualRank rank, Properties properties) {
        super(properties.stacksTo(1));
        this.manualType = ManualType.DISCIPLINE;
        this.path = null;
        this.discipline = discipline;
        this.rank = rank;
    }

    public ManualType getManualType() {
        return manualType;
    }

    public CultivationPath getPath() {
        return path;
    }

    public CultivationDiscipline getDiscipline() {
        return discipline;
    }

    public CultivationManualRank getRank() {
        return rank;
    }

    public ResourceLocation getManualId() {
        return BuiltInRegistries.ITEM.getKey(this);
    }

    public Component getStudyDisplayName() {
        String target = manualType == ManualType.PATH ? path.getChineseName() : discipline.getChineseName();
        return Component.literal(rank.getChineseName() + target + "宝典");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        PlayerQiData data = PlayerQiManager.getOrCreate(serverPlayer);
        if (!data.isAptitudeTested()) {
            serverPlayer.sendSystemMessage(Component.literal("§e你尚未检测修仙资质，无法参悟宝典。"));
            return InteractionResultHolder.fail(stack);
        }
        if (data.hasManualStudy()) {
            serverPlayer.sendSystemMessage(Component.literal(String.format(
                    "§e你正在参悟 %s，进度 %d/60 秒。先完成当前宝典。",
                    data.getStudyingManualDisplayName(),
                    data.getStudyingManualProgressSeconds()
            )));
            return InteractionResultHolder.fail(stack);
        }

        Component rejection = getStartRejection(data);
        if (rejection != null) {
            serverPlayer.sendSystemMessage(rejection);
            return InteractionResultHolder.fail(stack);
        }

        data.startManualStudy(getManualId(), manualType, path, discipline, rank, getStudyDisplayName().getString());
        serverPlayer.sendSystemMessage(Component.literal("§a你开始参悟 " + getStudyDisplayName().getString() + "。保持打坐 60 秒即可习得。"));
        PlayerQiManager.syncToClient(serverPlayer);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        String target = manualType == ManualType.PATH ? path.getChineseName() : discipline.getChineseName();
        String category = manualType == ManualType.PATH ? "道统" : "职业";
        tooltip.add(Component.literal(category + "：" + target).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("阶位：" + rank.getChineseName()).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("右键开始参悟，打坐 60 秒后习得。").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("不可合成，只能从遗迹宝箱中寻得。").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static boolean consumeManual(ServerPlayer player, ResourceLocation manualId) {
        if (player.isCreative()) {
            return true;
        }
        Item item = BuiltInRegistries.ITEM.get(manualId);
        if (item == null) {
            return false;
        }
        Inventory inventory = player.getInventory();
        if (consumeFromList(inventory.items, item)) {
            return true;
        }
        return consumeFromList(inventory.offhand, item);
    }

    private Component getStartRejection(PlayerQiData data) {
        if (manualType == ManualType.PATH && data.getSelectedPath() != null && data.getSelectedPath() != path) {
            return Component.literal("§c你已有不同道统，无法参悟此道统宝典。");
        }
        if (manualType == ManualType.DISCIPLINE
                && data.getMainDiscipline() != null
                && data.getMainDiscipline() != discipline
                && !data.getSubDisciplines().contains(discipline)
                && data.getSubDisciplines().size() >= PlayerQiData.MAX_SUB_DISCIPLINES) {
            return Component.literal("§c你的副业已满，无法参悟新的职业宝典。");
        }
        return null;
    }

    private static boolean consumeFromList(List<ItemStack> stacks, Item item) {
        for (ItemStack stack : stacks) {
            if (stack.is(item)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }
}
