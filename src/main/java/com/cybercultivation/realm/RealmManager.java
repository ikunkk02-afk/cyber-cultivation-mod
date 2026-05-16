package com.cybercultivation.realm;

import com.cybercultivation.component.PlayerQiData;
import com.cybercultivation.component.PlayerQiManager;
import com.cybercultivation.dimension.ModDimensions;
import com.cybercultivation.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Set;

public final class RealmManager {
    public static final int REALM_DURATION_TICKS = 48_000;
    public static final int ENTRY_QI_COST = 30;
    private static final Set<net.minecraft.world.entity.RelativeMovement> NO_RELATIVE_MOVEMENT = Set.of();

    private RealmManager() {
    }

    public static boolean tryEnter(ServerPlayer player, ItemStack tokenStack) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        if (isInHerbalRealm(player)) {
            player.sendSystemMessage(Component.literal("你已经在灵药秘境中。"));
            PlayerQiManager.syncToClient(player);
            return false;
        }
        if (!data.isAptitudeTested()) {
            player.sendSystemMessage(Component.literal("你尚未完成修仙资质检测，无法开启灵药秘境。"));
            return false;
        }
        if (data.getCurrentQi() < ENTRY_QI_COST) {
            player.sendSystemMessage(Component.literal("灵力不足，需要至少 30 点灵力才能进入灵药秘境。"));
            PlayerQiManager.syncToClient(player);
            return false;
        }
        if (tokenStack.isEmpty() || tokenStack.getItem() != ModItems.REALM_TOKEN) {
            player.sendSystemMessage(Component.literal("你需要手持秘境令牌。"));
            return false;
        }

        ServerLevel realm = player.getServer().getLevel(ModDimensions.HERBAL_SECRET_REALM);
        if (realm == null) {
            player.sendSystemMessage(Component.literal("灵药秘境尚未加载，无法进入。"));
            return false;
        }

        ResourceLocation returnDimension = player.level().dimension().location();
        double returnX = player.getX();
        double returnY = player.getY();
        double returnZ = player.getZ();
        float returnYRot = player.getYRot();
        float returnXRot = player.getXRot();

        BlockPos arrival = findOrCreateSafeArrival(realm);
        data.setCurrentQi(data.getCurrentQi() - ENTRY_QI_COST);
        data.startHerbalRealm(returnDimension, returnX, returnY, returnZ, returnYRot, returnXRot, REALM_DURATION_TICKS);
        boolean teleported = player.teleportTo(
                realm,
                arrival.getX() + 0.5D,
                arrival.getY(),
                arrival.getZ() + 0.5D,
                NO_RELATIVE_MOVEMENT,
                player.getYRot(),
                player.getXRot()
        );
        if (!teleported) {
            data.setCurrentQi(data.getCurrentQi() + ENTRY_QI_COST);
            data.clearHerbalRealm();
            player.sendSystemMessage(Component.literal("灵药秘境传送失败。"));
            PlayerQiManager.syncToClient(player);
            return false;
        }
        if (!player.getAbilities().instabuild) {
            tokenStack.shrink(1);
        }
        player.sendSystemMessage(Component.literal("你踏入了灵药秘境。秘境将在两天后关闭。"));
        PlayerQiManager.syncToClient(player);
        return true;
    }

    public static boolean leaveByCommand(ServerPlayer player) {
        if (!isInHerbalRealm(player)) {
            player.sendSystemMessage(Component.literal("你当前不在灵药秘境中。"));
            return false;
        }
        return returnFromRealm(player, false, Component.literal("你离开了灵药秘境。"));
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            tickPlayer(player);
        }
    }

    public static void clearForDeath(ServerPlayer player) {
        PlayerQiData data = PlayerQiManager.get(player);
        if (data != null && data.isInHerbalRealm()) {
            data.clearHerbalRealm();
            PlayerQiManager.syncToClient(player);
        }
    }

    private static void tickPlayer(ServerPlayer player) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        boolean inRealmDimension = isInHerbalRealm(player);

        if (!inRealmDimension) {
            if (data.isInHerbalRealm()) {
                data.clearHerbalRealm();
                PlayerQiManager.syncToClient(player);
            }
            return;
        }

        if (!data.isInHerbalRealm()) {
            data.startHerbalRealm(null, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, REALM_DURATION_TICKS);
        }

        int remaining = data.decrementHerbalRealmTick();
        if (remaining <= 0) {
            returnFromRealm(player, true, Component.literal("秘境即将关闭，你被送回了原来的世界。"));
            return;
        }

        if (player.tickCount % 20 == 0) {
            PlayerQiManager.syncToClient(player);
        }
    }

    private static boolean returnFromRealm(ServerPlayer player, boolean clearFirst, Component message) {
        PlayerQiData data = PlayerQiManager.getOrCreate(player);
        MinecraftServer server = player.getServer();
        ReturnTarget target = resolveReturnTarget(server, data);
        if (message != null) {
            player.sendSystemMessage(message);
        }
        if (clearFirst) {
            data.clearHerbalRealm();
        }
        player.teleportTo(target.level(), target.x(), target.y(), target.z(), NO_RELATIVE_MOVEMENT, target.yRot(), target.xRot());
        data.clearHerbalRealm();
        PlayerQiManager.syncToClient(player);
        return true;
    }

    private static ReturnTarget resolveReturnTarget(MinecraftServer server, PlayerQiData data) {
        ServerLevel targetLevel = null;
        if (data.hasHerbalRealmReturnPoint()) {
            ResourceLocation dimensionId = data.getHerbalRealmReturnDimension();
            targetLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
            if (targetLevel != null) {
                BlockPos pos = BlockPos.containing(data.getHerbalRealmReturnX(), data.getHerbalRealmReturnY(), data.getHerbalRealmReturnZ());
                if (isSafeStandPosition(targetLevel, pos)) {
                    return new ReturnTarget(
                            targetLevel,
                            data.getHerbalRealmReturnX(),
                            data.getHerbalRealmReturnY(),
                            data.getHerbalRealmReturnZ(),
                            data.getHerbalRealmReturnYRot(),
                            data.getHerbalRealmReturnXRot()
                    );
                }
            }
        }

        targetLevel = server.overworld();
        BlockPos spawn = targetLevel.getSharedSpawnPos();
        BlockPos safeSpawn = findOrCreateSafeOverworldSpawn(targetLevel, spawn);
        return new ReturnTarget(
                targetLevel,
                safeSpawn.getX() + 0.5D,
                safeSpawn.getY(),
                safeSpawn.getZ() + 0.5D,
                targetLevel.getSharedSpawnAngle(),
                0.0F
        );
    }

    private static BlockPos findOrCreateSafeArrival(ServerLevel level) {
        int x = 0;
        int z = 0;
        level.getChunkAt(new BlockPos(x, 80, z));
        int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        int y = Mth.clamp(Math.max(80, height + 1), level.getMinBuildHeight() + 8, level.getMaxBuildHeight() - 8);
        BlockPos center = new BlockPos(x, y, z);
        createSafePlatform(level, center);
        return center;
    }

    private static BlockPos findOrCreateSafeOverworldSpawn(ServerLevel level, BlockPos spawn) {
        int y = Math.max(spawn.getY(), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ()) + 1);
        BlockPos pos = new BlockPos(spawn.getX(), y, spawn.getZ());
        if (!isSafeStandPosition(level, pos)) {
            createSafePlatform(level, pos);
        }
        return pos;
    }

    private static void createSafePlatform(ServerLevel level, BlockPos feetPos) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos floor = feetPos.offset(dx, -1, dz);
                level.setBlock(floor, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                level.setBlock(floor.below(), Blocks.DIRT.defaultBlockState(), 3);
                level.setBlock(floor.above(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(floor.above(2), Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    private static boolean isSafeStandPosition(ServerLevel level, BlockPos feetPos) {
        if (feetPos.getY() <= level.getMinBuildHeight() || feetPos.getY() >= level.getMaxBuildHeight() - 2) {
            return false;
        }
        BlockState floor = level.getBlockState(feetPos.below());
        BlockState feet = level.getBlockState(feetPos);
        BlockState head = level.getBlockState(feetPos.above());
        return floor.isSolid()
                && !level.getFluidState(feetPos.below()).is(FluidTags.LAVA)
                && feet.getCollisionShape(level, feetPos).isEmpty()
                && head.getCollisionShape(level, feetPos.above()).isEmpty()
                && level.getFluidState(feetPos).isEmpty()
                && level.getFluidState(feetPos.above()).isEmpty();
    }

    public static boolean isInHerbalRealm(ServerPlayer player) {
        return player.level().dimension().equals(ModDimensions.HERBAL_SECRET_REALM);
    }

    private record ReturnTarget(ServerLevel level, double x, double y, double z, float yRot, float xRot) {
    }
}
