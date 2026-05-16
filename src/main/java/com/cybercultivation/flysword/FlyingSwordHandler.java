package com.cybercultivation.flysword;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.cultivation.CultivationDiscipline;
import com.cybercultivation.util.FlyingSwordHelper;
import com.cybercultivation.util.ParticleColorHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class FlyingSwordHandler {
    private static final int MIN_QI_TO_START = 10;
    private static final int QI_COST_PER_SECOND = 2;
    private static final int TICKS_PER_SECOND = 20;
    private static final float FLYING_SWORD_SPEED = 0.035F;
    private static final float DEFAULT_FLYING_SPEED = 0.05F;
    private static final Component NEED_SWORD_MESSAGE = Component.literal("\u4f60\u9700\u8981\u624b\u6301\u4e00\u628a\u4fee\u4ed9\u5251\u624d\u80fd\u5fa1\u5251\u3002");
    private static final Component LOST_SWORD_MESSAGE = Component.literal("\u4f60\u5931\u53bb\u4e86\u5fa1\u5251\u6240\u9700\u7684\u6cd5\u5251\u3002");
    private static final Component WRONG_DAO_MESSAGE = Component.literal("\u4f60\u7684\u9053\u7edf\u65e0\u6cd5\u9a7e\u9a6d\u8fd9\u628a\u6b66\u5668\u3002");
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
        ItemStack sword = FlyingSwordHelper.getHeldFlyingSword(player);
        if (sword.isEmpty()) {
            player.sendSystemMessage(NEED_SWORD_MESSAGE);
            PlayerQiManager.syncToClient(player);
            return false;
        }
        if (!FlyingSwordHelper.canPathUseSword(data.getSelectedPath(), sword)) {
            player.sendSystemMessage(WRONG_DAO_MESSAGE);
            PlayerQiManager.syncToClient(player);
            return false;
        }
        if (!canUseFlyingSword(data)) {
            player.sendSystemMessage(Component.literal("\u4f60\u5c1a\u672a\u638c\u63e1\u5251\u4fee\u4e4b\u9053\uff0c\u65e0\u6cd5\u5fa1\u5251\u3002"));
            PlayerQiManager.syncToClient(player);
            return false;
        }

        data.setFlyingSword(true);
        data.setFlyingSwordItemId(FlyingSwordHelper.getItemId(sword));
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
        tickCounter = (tickCounter + 1) % TICKS_PER_SECOND;
        boolean drainQiThisTick = tickCounter == 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerQiData data = PlayerQiManager.get(player);
            if (data == null || !data.isFlyingSword()) {
                continue;
            }

            ItemStack sword = FlyingSwordHelper.getHeldFlyingSword(player);
            if (sword.isEmpty()) {
                player.sendSystemMessage(LOST_SWORD_MESSAGE);
                exit(player, false, true);
                continue;
            }
            if (!FlyingSwordHelper.canPathUseSword(data.getSelectedPath(), sword)) {
                player.sendSystemMessage(WRONG_DAO_MESSAGE);
                exit(player, false, true);
                continue;
            }
            data.setFlyingSwordItemId(FlyingSwordHelper.getItemId(sword));

            if (drainQiThisTick) {
                if (data.getCurrentQi() < QI_COST_PER_SECOND) {
                    player.sendSystemMessage(Component.literal("\u7075\u529b\u4e0d\u8db3\uff0c\u5fa1\u5251\u72b6\u6001\u5df2\u7ed3\u675f\u3002"));
                    exit(player, false, true);
                    continue;
                }

                data.setCurrentQi(data.getCurrentQi() - QI_COST_PER_SECOND);
                PlayerQiManager.syncToClient(player);
                PlayerQiManager.broadcastAnimationState(player);
            }
            if (player.tickCount % 2 == 0) {
                spawnFlyingSwordEffects(player, drainQiThisTick);
            }
        }
    }

    private static void exit(ServerPlayer player, boolean notify, boolean sync) {
        PlayerQiData data = PlayerQiManager.get(player);
        if (data == null || !data.isFlyingSword()) {
            return;
        }

        data.setFlyingSword(false);
        data.setFlyingSwordItemId(null);
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

    private static void spawnFlyingSwordEffects(ServerPlayer player, boolean playSound) {
        ServerLevel level = player.serverLevel();
        Vec3 velocity = player.getDeltaMovement();
        Vec3 direction = velocity.lengthSqr() > 1.0E-4D
                ? velocity.normalize()
                : player.getLookAngle().normalize();
        Vec3 horizontal = new Vec3(direction.x, 0.0D, direction.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = new Vec3(player.getLookAngle().x, 0.0D, player.getLookAngle().z);
        }
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = new Vec3(0.0D, 0.0D, 1.0D);
        }
        horizontal = horizontal.normalize();
        Vec3 right = new Vec3(-horizontal.z, 0.0D, horizontal.x);
        Vec3 origin = player.position().add(0.0D, 0.12D, 0.0D);
        Vec3 rear = origin.subtract(horizontal.scale(0.95D));

        ItemStack sword = FlyingSwordHelper.getHeldFlyingSword(player);
        Vector3f color = sword.isEmpty()
                ? new Vector3f(0.20F, 0.90F, 0.95F)
                : ParticleColorHelper.getFlyingSwordTrailColor(sword);
        DustParticleOptions trail = new DustParticleOptions(color, 1.05F);
        DustParticleOptions spark = new DustParticleOptions(new Vector3f(0.85F, 1.00F, 1.00F), 0.55F);

        for (int i = 0; i < 10; i++) {
            double progress = i / 9.0D;
            double side = Math.sin((player.tickCount + i) * 0.55D) * 0.38D;
            Vec3 pos = rear
                    .subtract(horizontal.scale(progress * 1.35D))
                    .add(right.scale(side))
                    .add(0.0D, 0.04D + progress * 0.16D, 0.0D);
            level.sendParticles(
                    trail,
                    pos.x, pos.y, pos.z,
                    1,
                    -horizontal.x * 0.03D,
                    0.015D,
                    -horizontal.z * 0.03D,
                    0.01D
            );
        }

        for (int i = 0; i < 4; i++) {
            double angle = player.tickCount * 0.22D + i * Math.PI * 0.5D;
            Vec3 pos = origin
                    .add(right.scale(Math.cos(angle) * 0.62D))
                    .add(0.0D, Math.sin(angle) * 0.10D, 0.0D);
            level.sendParticles(spark, pos.x, pos.y, pos.z, 1, 0.0D, 0.02D, 0.0D, 0.01D);
        }

        if (player.tickCount % 6 == 0) {
            level.sendParticles(ParticleTypes.END_ROD,
                    rear.x, rear.y + 0.08D, rear.z,
                    2, 0.18D, 0.05D, 0.18D, 0.01D);
        }

        if (playSound) {
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
}
