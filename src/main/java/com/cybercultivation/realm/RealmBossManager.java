package com.cybercultivation.realm;

import com.cybercultivation.dimension.ModDimensions;
import com.cybercultivation.item.ModItems;
import com.cybercultivation.particle.SpiritQiParticleOptions;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RealmBossManager {
    public static final String BOSS_TAG = "cyber_cultivation_realm_boss";
    public static final String MINION_TAG = "cyber_cultivation_realm_minion";
    private static final String SUMMONED_MINIONS_TAG = "cyber_cultivation_realm_boss_summoned_minions";
    private static final String ENRAGED_TAG = "cyber_cultivation_realm_boss_enraged";
    private static final int QI_SHOCK_INTERVAL = 160;
    private static final int POISON_FIELD_INTERVAL = 240;
    private static final int BOSS_BAR_RANGE = 64;
    private static final int BOSS_SCAN_RANGE = 128;
    private static final int BOSS_AREA_LIMIT_RANGE = 96;
    private static final int NATURAL_SPAWN_INTERVAL = 600;
    private static final int NATURAL_SPAWN_CHANCE = 18;
    private static final Map<UUID, ServerBossEvent> BOSS_BARS = new ConcurrentHashMap<>();

    private RealmBossManager() {
    }

    public static void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register(RealmBossManager::afterDeath);
    }

    public static void onServerTick(MinecraftServer server) {
        ServerLevel realm = server.getLevel(ModDimensions.HERBAL_SECRET_REALM);
        if (realm == null) {
            clearBossBars();
            return;
        }

        Set<UUID> seenBosses = new HashSet<>();
        for (ServerPlayer player : realm.players()) {
            maybeSpawnNaturalBoss(realm, player);
            for (WitherSkeleton boss : realm.getEntitiesOfClass(
                    WitherSkeleton.class,
                    player.getBoundingBox().inflate(BOSS_SCAN_RANGE),
                    RealmBossManager::isRealmBoss)) {
                seenBosses.add(boss.getUUID());
                tickBoss(realm, boss);
            }
        }

        BOSS_BARS.entrySet().removeIf(entry -> {
            if (seenBosses.contains(entry.getKey())) {
                return false;
            }
            entry.getValue().removeAllPlayers();
            return true;
        });
    }

    public static WitherSkeleton spawnBossNear(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos spawnPos = findSafeSpawnPos(level, player.blockPosition().offset(2, 0, 2));
        WitherSkeleton boss = EntityType.WITHER_SKELETON.create(level);
        if (boss == null) {
            return null;
        }
        configureBoss(boss);
        boss.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.getYRot(), 0.0F);
        level.addFreshEntity(boss);
        return boss;
    }

    private static void maybeSpawnNaturalBoss(ServerLevel level, ServerPlayer player) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (player.tickCount % NATURAL_SPAWN_INTERVAL != 0 || level.getRandom().nextInt(NATURAL_SPAWN_CHANCE) != 0) {
            return;
        }
        if (countNearbyBosses(level, player.blockPosition(), BOSS_AREA_LIMIT_RANGE) > 0) {
            return;
        }

        double angle = level.getRandom().nextDouble() * Math.PI * 2.0D;
        int distance = 32 + level.getRandom().nextInt(32);
        int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
        int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
        BlockPos candidate = findSafeSpawnPos(level, new BlockPos(x, player.blockPosition().getY(), z));

        WitherSkeleton boss = EntityType.WITHER_SKELETON.create(level);
        if (boss == null) {
            return;
        }
        configureBoss(boss);
        boss.moveTo(candidate.getX() + 0.5D, candidate.getY(), candidate.getZ() + 0.5D, level.getRandom().nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(boss);
    }

    private static void configureBoss(WitherSkeleton boss) {
        boss.addTag(BOSS_TAG);
        boss.setCustomName(Component.literal("秘境守护者"));
        boss.setCustomNameVisible(true);
        boss.setPersistenceRequired();
        boss.setCanPickUpLoot(false);

        setAttribute(boss, Attributes.MAX_HEALTH, 300.0D);
        setAttribute(boss, Attributes.ATTACK_DAMAGE, 16.0D);
        setAttribute(boss, Attributes.ARMOR, 16.0D);
        setAttribute(boss, Attributes.ARMOR_TOUGHNESS, 8.0D);
        setAttribute(boss, Attributes.MOVEMENT_SPEED, 0.32D);
        setAttribute(boss, Attributes.KNOCKBACK_RESISTANCE, 0.9D);
        setAttribute(boss, Attributes.FOLLOW_RANGE, 48.0D);
        boss.setHealth(boss.getMaxHealth());

        boss.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.STARFALL_HELMET));
        boss.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.STARFALL_CHESTPLATE));
        boss.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.STARFALL_LEGGINGS));
        boss.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.STARFALL_BOOTS));
        boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.STARFALL_SWORD));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            boss.setDropChance(slot, 0.0F);
        }
    }

    private static void tickBoss(ServerLevel level, WitherSkeleton boss) {
        if (boss.isRemoved() || !boss.isAlive()) {
            removeBossBar(boss.getUUID());
            return;
        }

        updateBossBar(level, boss);
        if (boss.tickCount % QI_SHOCK_INTERVAL == 0) {
            castQiShock(level, boss);
        }
        if (boss.tickCount % POISON_FIELD_INTERVAL == 40) {
            castPoisonField(level, boss);
        }
        if (boss.getHealth() <= boss.getMaxHealth() * 0.5F && !boss.getTags().contains(SUMMONED_MINIONS_TAG)) {
            boss.addTag(SUMMONED_MINIONS_TAG);
            summonMinions(level, boss);
        }
        if (boss.getHealth() <= boss.getMaxHealth() * 0.25F && !boss.getTags().contains(ENRAGED_TAG)) {
            boss.addTag(ENRAGED_TAG);
            enrage(boss);
        }
    }

    private static void updateBossBar(ServerLevel level, WitherSkeleton boss) {
        ServerBossEvent bar = BOSS_BARS.computeIfAbsent(boss.getUUID(), uuid -> new ServerBossEvent(
                Component.literal("秘境守护者"),
                BossEvent.BossBarColor.GREEN,
                BossEvent.BossBarOverlay.PROGRESS
        ));
        bar.setProgress(Mth.clamp(boss.getHealth() / boss.getMaxHealth(), 0.0F, 1.0F));

        Set<ServerPlayer> nearby = new HashSet<>(level.getEntitiesOfClass(
                ServerPlayer.class,
                boss.getBoundingBox().inflate(BOSS_BAR_RANGE),
                player -> !player.isSpectator()
        ));
        for (ServerPlayer player : nearby) {
            bar.addPlayer(player);
        }
        for (ServerPlayer tracked : Set.copyOf(bar.getPlayers())) {
            if (!nearby.contains(tracked)) {
                bar.removePlayer(tracked);
            }
        }
    }

    private static void castQiShock(ServerLevel level, WitherSkeleton boss) {
        level.sendParticles(new SpiritQiParticleOptions(0x55E6C1), boss.getX(), boss.getY() + 1.1D, boss.getZ(),
                42, 2.6D, 0.8D, 2.6D, 0.03D);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(6.0D))) {
            if (player.isSpectator() || player.isCreative()) {
                continue;
            }
            player.hurt(boss.damageSources().magic(), 6.0F);
            Vec3 push = player.position().subtract(boss.position()).normalize().scale(1.15D);
            player.push(push.x, 0.45D, push.z);
        }
    }

    private static void castPoisonField(ServerLevel level, WitherSkeleton boss) {
        level.sendParticles(new SpiritQiParticleOptions(0x42D957), boss.getX(), boss.getY() + 0.7D, boss.getZ(),
                64, 4.0D, 0.35D, 4.0D, 0.01D);
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(8.0D))) {
            if (player.isSpectator() || player.isCreative()) {
                continue;
            }
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0), boss);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0), boss);
        }
    }

    private static void summonMinions(ServerLevel level, WitherSkeleton boss) {
        int count = 2 + level.getRandom().nextInt(2);
        for (int i = 0; i < count; i++) {
            Mob minion = level.getRandom().nextBoolean() ? EntityType.ZOMBIE.create(level) : EntityType.SKELETON.create(level);
            if (minion == null) {
                continue;
            }
            minion.addTag(MINION_TAG);
            minion.setPersistenceRequired();
            double angle = (Math.PI * 2.0D / count) * i;
            double x = boss.getX() + Math.cos(angle) * 2.0D;
            double z = boss.getZ() + Math.sin(angle) * 2.0D;
            minion.moveTo(x, boss.getY(), z, boss.getYRot(), 0.0F);
            if (minion instanceof Zombie zombie) {
                zombie.setCanPickUpLoot(false);
            } else if (minion instanceof Skeleton skeleton) {
                skeleton.setCanPickUpLoot(false);
            }
            level.addFreshEntity(minion);
        }
    }

    private static void enrage(WitherSkeleton boss) {
        boss.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, 1), boss);
        boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 60, 1), boss);
        boss.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60, 0), boss);
    }

    private static void afterDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        if (!(entity instanceof WitherSkeleton boss) || !isRealmBoss(boss) || !(boss.level() instanceof ServerLevel level)) {
            return;
        }
        removeBossBar(boss.getUUID());
        Vec3 pos = boss.position();
        drop(level, pos, new ItemStack(ModItems.HIGH_GRADE_SPIRIT_STONE, 2 + level.getRandom().nextInt(3)));
        drop(level, pos, new ItemStack(ModItems.EARTH_ROOT_GINSENG, 1 + level.getRandom().nextInt(2)));
        drop(level, pos, new ItemStack(ModItems.FIVE_ELEMENT_FRUIT));
        drop(level, pos, new ItemStack(ModItems.REALM_CORE));
        if (level.getRandom().nextFloat() < 0.5F) {
            drop(level, pos, new ItemStack(ModItems.BEAST_CORE));
        }
        if (level.getRandom().nextFloat() < 0.2F) {
            drop(level, pos, new ItemStack(ModItems.FOUNDATION_PILL));
        }
        if (level.getRandom().nextFloat() < 0.2F) {
            drop(level, pos, new ItemStack(ModItems.HEART_CLEANSING_PILL));
        }
        if (level.getRandom().nextFloat() < 0.05F) {
            drop(level, pos, new ItemStack(ModItems.STARFALL_SWORD));
        }
        ExperienceOrb.award(level, pos, 100);
    }

    private static void drop(ServerLevel level, Vec3 pos, ItemStack stack) {
        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(level, pos.x, pos.y, pos.z, stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    private static BlockPos findSafeSpawnPos(ServerLevel level, BlockPos origin) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos pos = new BlockPos(origin.getX(), y, origin.getZ());
        if (isSafeStandPosition(level, pos)) {
            return pos;
        }
        return new BlockPos(origin.getX(), Mth.clamp(y + 1, level.getMinBuildHeight() + 4, level.getMaxBuildHeight() - 4), origin.getZ());
    }

    private static boolean isSafeStandPosition(ServerLevel level, BlockPos feetPos) {
        BlockState floor = level.getBlockState(feetPos.below());
        return feetPos.getY() > level.getMinBuildHeight()
                && feetPos.getY() < level.getMaxBuildHeight() - 2
                && floor.isSolid()
                && level.getBlockState(feetPos).getCollisionShape(level, feetPos).isEmpty()
                && level.getBlockState(feetPos.above()).getCollisionShape(level, feetPos.above()).isEmpty()
                && level.getFluidState(feetPos).isEmpty()
                && level.getFluidState(feetPos.above()).isEmpty();
    }

    private static int countNearbyBosses(ServerLevel level, BlockPos center, int radius) {
        AABB area = new AABB(center).inflate(radius);
        return level.getEntitiesOfClass(WitherSkeleton.class, area, RealmBossManager::isRealmBoss).size();
    }

    private static boolean isRealmBoss(WitherSkeleton skeleton) {
        return skeleton.getTags().contains(BOSS_TAG);
    }

    private static void setAttribute(WitherSkeleton boss, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        AttributeInstance instance = boss.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    private static void removeBossBar(UUID uuid) {
        ServerBossEvent bar = BOSS_BARS.remove(uuid);
        if (bar != null) {
            bar.removeAllPlayers();
        }
    }

    private static void clearBossBars() {
        for (ServerBossEvent bar : BOSS_BARS.values()) {
            bar.removeAllPlayers();
        }
        BOSS_BARS.clear();
    }
}
