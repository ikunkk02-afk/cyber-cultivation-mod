package com.cybercultivation.client.animation;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.client.ClientQiData;
import com.cybercultivation.client.FlyingSwordVisualState;
import com.cybercultivation.network.AttackEffectPayload;
import com.cybercultivation.network.PlayerAnimationSyncPayload;
import com.cybercultivation.util.ParticleColorHelper;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CultivationAnimationController {
    private static final int BASE_LAYER_PRIORITY = 42;
    private static final int ATTACK_LAYER_PRIORITY = 43;
    private static final Map<UUID, ModifierLayer<IAnimation>> LAYERS = new ConcurrentHashMap<>();
    private static final Map<UUID, ModifierLayer<IAnimation>> ATTACK_LAYERS = new ConcurrentHashMap<>();
    private static final Map<UUID, ActiveAnim> ACTIVE_ANIMS = new ConcurrentHashMap<>();

    private CultivationAnimationController() {
    }

    public static void register() {
        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register(CultivationAnimationController::onInit);

        ClientPlayNetworking.registerGlobalReceiver(
                PlayerAnimationSyncPayload.TYPE,
                (payload, context) -> context.client().execute(() -> onSync(payload))
        );

        ClientPlayNetworking.registerGlobalReceiver(
                AttackEffectPayload.TYPE,
                (payload, context) -> context.client().execute(() -> onAttackEffect(payload))
        );

        ClientTickEvents.END_CLIENT_TICK.register(CultivationAnimationController::onTick);
    }

    private static void onInit(AbstractClientPlayer player,
                                dev.kosmx.playerAnim.api.layered.AnimationStack stack) {
        ModifierLayer<IAnimation> baseLayer = new ModifierLayer<>();
        stack.addAnimLayer(BASE_LAYER_PRIORITY, baseLayer);
        LAYERS.put(player.getUUID(), baseLayer);

        ModifierLayer<IAnimation> attackLayer = new ModifierLayer<>();
        stack.addAnimLayer(ATTACK_LAYER_PRIORITY, attackLayer);
        ATTACK_LAYERS.put(player.getUUID(), attackLayer);

        ACTIVE_ANIMS.remove(player.getUUID());
    }

    private static void onSync(PlayerAnimationSyncPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        Entity entity = client.level.getEntity(payload.entityId());
        FlyingSwordVisualState.set(payload.entityId(), payload.flyingSword(), payload.flyingSwordItemId());
        if (entity instanceof Player player) {
            apply(player, payload.meditating(), payload.flyingSword());
        }
    }

    private static void onTick(Minecraft client) {
        if (client.player == null) return;
        apply(client.player, ClientQiData.isMeditating(), ClientQiData.isFlyingSword());
    }

    private static void onAttackEffect(AttackEffectPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        Entity entity = client.level.getEntity(payload.attackerEntityId());
        if (!(entity instanceof Player player)) return;

        String animName = ParticleColorHelper.getSwordAnimationName(payload.swordItemId());
        IAnimation anim = CultivationPlayerAnimations.createAttack(animName);
        if (anim != null) {
            ModifierLayer<IAnimation> layer = ATTACK_LAYERS.get(player.getUUID());
            if (layer != null) {
                layer.setAnimation(anim);
            }
        }

        if (!(entity instanceof AbstractClientPlayer)) return;
        ResourceLocation itemId = payload.swordItemId();
        Vector3f color = ParticleColorHelper.getSwordQiColor(
                itemId != null ? new ItemStack(BuiltInRegistries.ITEM.get(itemId)) : ItemStack.EMPTY
        );
        spawnLocalSwordQi(client.level, player.getEyePosition(), player.getLookAngle(), color);
    }

    private static void spawnLocalSwordQi(ClientLevel level, Vec3 origin, Vec3 look, Vector3f color) {
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        for (int i = 0; i < 6; i++) {
            double progress = (double) i / 5.0;
            double spread = (progress - 0.5) * 1.2;
            double forward = 0.5 + progress * 0.8;
            Vec3 pos = origin
                    .add(look.scale(forward))
                    .add(right.scale(spread));
            level.addParticle(
                    new DustParticleOptions(color, 0.8F),
                    pos.x, pos.y, pos.z,
                    look.x * 0.15, look.y * 0.15 + 0.05, look.z * 0.15
            );
        }
    }

    private static void apply(Player player, boolean meditating, boolean flyingSword) {
        ModifierLayer<IAnimation> layer = getLayer(player);
        if (layer == null) return;

        UUID uuid = player.getUUID();
        ActiveAnim desired = ActiveAnim.from(meditating, flyingSword);
        ActiveAnim current = ACTIVE_ANIMS.getOrDefault(uuid, ActiveAnim.VANILLA);
        if (desired == current) return;

        if (desired == ActiveAnim.VANILLA) {
            layer.setAnimation(null);
            ACTIVE_ANIMS.put(uuid, ActiveAnim.VANILLA);
            return;
        }

        IAnimation animation = switch (desired) {
            case FLYING_SWORD -> CultivationPlayerAnimations.createFlyingSwordPlayer();
            case MEDITATION -> CultivationPlayerAnimations.createMeditationPlayer();
            case VANILLA -> null;
        };

        if (animation == null) {
            layer.setAnimation(null);
            ACTIVE_ANIMS.remove(uuid);
            return;
        }

        layer.setAnimation(animation);
        ACTIVE_ANIMS.put(uuid, desired);
    }

    private static ModifierLayer<IAnimation> getLayer(Player player) {
        ModifierLayer<IAnimation> layer = LAYERS.get(player.getUUID());
        if (layer != null) return layer;

        if (player instanceof AbstractClientPlayer clientPlayer) {
            try {
                ModifierLayer<IAnimation> newLayer = new ModifierLayer<>();
                PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer).addAnimLayer(BASE_LAYER_PRIORITY, newLayer);
                LAYERS.put(player.getUUID(), newLayer);
                return newLayer;
            } catch (Throwable t) {
                CyberCultivationMod.LOGGER.warn("Failed to create player animation layer for {}", player.getName().getString(), t);
            }
        }
        return null;
    }

    private enum ActiveAnim {
        VANILLA,
        MEDITATION,
        FLYING_SWORD;

        static ActiveAnim from(boolean meditating, boolean flyingSword) {
            if (flyingSword) return FLYING_SWORD;
            if (meditating) return MEDITATION;
            return VANILLA;
        }
    }
}
