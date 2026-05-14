package com.cybercultivation.client.animation;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.client.ClientQiData;
import com.cybercultivation.network.PlayerAnimationSyncPayload;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CultivationAnimationController {
    private static final int LAYER_PRIORITY = 42;
    private static final Map<UUID, ModifierLayer<IAnimation>> LAYERS = new ConcurrentHashMap<>();
    private static final Map<UUID, ActiveAnim> ACTIVE_ANIMS = new ConcurrentHashMap<>();

    private CultivationAnimationController() {
    }

    public static void register() {
        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register(CultivationAnimationController::onInit);

        ClientPlayNetworking.registerGlobalReceiver(
                PlayerAnimationSyncPayload.TYPE,
                (payload, context) -> context.client().execute(() -> onSync(payload))
        );

        // Keeps the local player's animation in sync even when only QiSyncPayload is received.
        ClientTickEvents.END_CLIENT_TICK.register(CultivationAnimationController::onTick);
    }

    private static void onInit(AbstractClientPlayer player,
                               dev.kosmx.playerAnim.api.layered.AnimationStack stack) {
        ModifierLayer<IAnimation> layer = new ModifierLayer<>();
        stack.addAnimLayer(LAYER_PRIORITY, layer);
        LAYERS.put(player.getUUID(), layer);
        ACTIVE_ANIMS.remove(player.getUUID());
    }

    private static void onSync(PlayerAnimationSyncPayload payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return;
        }

        Entity entity = client.level.getEntity(payload.entityId());
        if (entity instanceof Player player) {
            apply(player, payload.meditating(), payload.flyingSword());
        }
    }

    private static void onTick(Minecraft client) {
        if (client.player == null) {
            return;
        }
        apply(client.player, ClientQiData.isMeditating(), ClientQiData.isFlyingSword());
    }

    private static void apply(Player player, boolean meditating, boolean flyingSword) {
        ModifierLayer<IAnimation> layer = getLayer(player);
        if (layer == null) {
            return;
        }

        UUID uuid = player.getUUID();
        ActiveAnim desired = ActiveAnim.from(meditating, flyingSword);
        ActiveAnim current = ACTIVE_ANIMS.getOrDefault(uuid, ActiveAnim.VANILLA);
        if (desired == current) {
            return;
        }

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
            // Do not mark it active, so the controller can retry after resource reloads.
            layer.setAnimation(null);
            ACTIVE_ANIMS.remove(uuid);
            return;
        }

        layer.setAnimation(animation);
        ACTIVE_ANIMS.put(uuid, desired);
    }

    private static ModifierLayer<IAnimation> getLayer(Player player) {
        ModifierLayer<IAnimation> layer = LAYERS.get(player.getUUID());
        if (layer != null) {
            return layer;
        }

        // Fallback: if the event was missed for some reason, attach a layer on demand.
        if (player instanceof AbstractClientPlayer clientPlayer) {
            try {
                ModifierLayer<IAnimation> newLayer = new ModifierLayer<>();
                PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer).addAnimLayer(LAYER_PRIORITY, newLayer);
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
            if (flyingSword) {
                return FLYING_SWORD;
            }
            if (meditating) {
                return MEDITATION;
            }
            return VANILLA;
        }
    }
}
