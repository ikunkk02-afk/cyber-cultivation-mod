package com.cybercultivation.flysword;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.item.ModItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public final class FlyingSwordHandler {
    private static final int MIN_QI_TO_START = 10;
    private static final int QI_COST_PER_SECOND = 2;
    private static final int TICKS_PER_SECOND = 20;
    private static final float FLYING_SWORD_SPEED = 0.035F;
    private static final float DEFAULT_FLYING_SPEED = 0.05F;
    private static final DustParticleOptions FLYING_SWORD_PARTICLE =
            new DustParticleOptions(new Vector3f(0.20F, 0.90F, 0.95F), 0.85F);

    private static int tickCounter = 0;

    private FlyingSwordHandler() {
    }

    public static void toggle(ServerPlayer player, boolean requireItem) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (data.isFlyingSword()) {
            exit(player);
            return;
        }
        start(player, requireItem);
    }

    public static boolean start(ServerPlayer player, boolean requireItem) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (data.isFlyingSword()) {
            return true;
        }
        if (requireItem && !isHoldingFlyingSword(player)) {
            return false;
        }
        if (!canUseFlyingSword(data)) {
            player.sendSystemMessage(Component.literal("\u4f60\u5c1a\u672a\u638c\u63e1\u5251\u4fee\u4e4b\u9053\uff0c\u65e0\u6cd5\u5fa1\u5251\u3002"));
            PlayerQiManager.syncToClient(player);
            return false;
        }

        data.setFlyingSword(true);
        Abilities abilities = player.getAbilities();
        abilities.mayfly = true;
        abilities.flying = true;
        abilities.setFlyingSpeed(FLYING_SWORD_SPEED);
        player.onUpdateAbilities();
        player.sendSystemMessage(Component.literal("\u4f60\u8e0f\u5251\u800c\u8d77\uff0c\u8fdb\u5165\u5fa1\u5251\u72b6\u6001\u3002"));
        PlayerQiManager.syncToClient(player);
        PlayerQiManager.broadcastAnimationState(player);
        return true;
    }

    public static void exit(ServerPlayer player) {
        exit(player, true, true);
    }

    public static void exitSilently(ServerPlayer player) {
        exit(player, false, true);
    }

    public static void exitForDisconnect(ServerPlayer player) {
        exit(player, false, false);
    }

    public static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < TICKS_PER_SECOND) {
            return;
        }
        tickCounter = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerQiData data = PlayerQiManager.get(player);
            if (data == null || !data.isFlyingSword()) {
                continue;
            }

            if (data.getCurrentQi() < QI_COST_PER_SECOND) {
                player.sendSystemMessage(Component.literal("\u7075\u529b\u4e0d\u8db3\uff0c\u5fa1\u5251\u72b6\u6001\u5df2\u7ed3\u675f\u3002"));
                exit(player, false, true);
                continue;
            }

            data.setCurrentQi(data.getCurrentQi() - QI_COST_PER_SECOND);
            spawnFlyingSwordEffects(player);
            PlayerQiManager.syncToClient(player);
        }
    }

    private static void exit(ServerPlayer player, boolean notify, boolean sync) {
        PlayerQiData data = PlayerQiManager.get(player);
        if (data == null || !data.isFlyingSword()) {
            return;
        }

        data.setFlyingSword(false);
        Abilities abilities = player.getAbilities();
        if (!player.isCreative() && !player.isSpectator()) {
            abilities.mayfly = false;
            abilities.flying = false;
        }
        abilities.setFlyingSpeed(DEFAULT_FLYING_SPEED);
        player.onUpdateAbilities();

        if (notify) {
            player.sendSystemMessage(Component.literal("\u4f60\u6536\u8d77\u98de\u5251\uff0c\u9000\u51fa\u5fa1\u5251\u72b6\u6001\u3002"));
        }
        if (sync) {
            PlayerQiManager.syncToClient(player);
            PlayerQiManager.broadcastAnimationState(player);
        }
    }

    private static boolean canUseFlyingSword(PlayerQiData data) {
        return data.isAptitudeTested()
                && data.getCurrentQi() > MIN_QI_TO_START
                && (data.getMainDiscipline() == CultivationDiscipline.SWORD
                || data.getSubDisciplines().contains(CultivationDiscipline.SWORD));
    }

    private static boolean isHoldingFlyingSword(ServerPlayer player) {
        return isFlyingSword(player.getMainHandItem()) || isFlyingSword(player.getOffhandItem());
    }

    private static boolean isFlyingSword(ItemStack stack) {
        return stack.is(ModItems.FLYING_SWORD);
    }

    private static void spawnFlyingSwordEffects(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        double x = player.getX();
        double y = player.getY() + 0.08;
        double z = player.getZ();

        for (int i = 0; i < 5; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 0.75;
            double offsetZ = (level.random.nextDouble() - 0.5) * 0.75;
            level.sendParticles(
                    FLYING_SWORD_PARTICLE,
                    x + offsetX,
                    y,
                    z + offsetZ,
                    1,
                    0.0,
                    0.02,
                    0.0,
                    0.01
            );
        }

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                0.18F,
                1.55F
        );
    }
}
