package com.cybercultivation.client.render;

import com.cybercultivation.client.ClientQiData;
import com.cybercultivation.client.FlyingSwordVisualState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class FlyingSwordVisualRenderer {
    private static final float FOOT_OFFSET = 0.48F;
    private static final float SWORD_SCALE = 1.25F;

    private FlyingSwordVisualRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel level = client.level;
            if (level == null || client.player == null) {
                FlyingSwordVisualState.clear();
                return;
            }

            for (AbstractClientPlayer player : level.players()) {
                ResourceLocation itemId = getSwordItemId(client, player);
                if (itemId != null) {
                    renderSword(
                            context.matrixStack(),
                            context.consumers(),
                            context.camera().getPosition(),
                            player,
                            itemId,
                            context.tickCounter().getGameTimeDeltaPartialTick(true)
                    );
                }
            }
        });
    }

    private static ResourceLocation getSwordItemId(Minecraft client, AbstractClientPlayer player) {
        if (player == client.player) {
            return ClientQiData.isFlyingSword() ? ClientQiData.getFlyingSwordItemId() : null;
        }
        return FlyingSwordVisualState.get(player.getId());
    }

    private static void renderSword(PoseStack matrices,
                                    MultiBufferSource consumers,
                                    Vec3 cameraPos,
                                    AbstractClientPlayer player,
                                    ResourceLocation itemId,
                                    float tickDelta) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == Items.AIR) {
            return;
        }

        Vec3 pos = player.getPosition(tickDelta);
        Vec3 direction = getFlightDirection(player);
        double horizontalSpeed = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) -(Mth.atan2(direction.y, Math.max(horizontalSpeed, 1.0E-4D)) * Mth.RAD_TO_DEG);
        ItemStack stack = new ItemStack(item);

        matrices.pushPose();
        matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y + FOOT_OFFSET, pos.z - cameraPos.z);
        matrices.mulPose(Axis.YP.rotationDegrees(-yaw));
        matrices.mulPose(Axis.XP.rotationDegrees(90.0F + Mth.clamp(pitch, -35.0F, 35.0F)));
        matrices.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        matrices.scale(SWORD_SCALE, SWORD_SCALE, SWORD_SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                matrices,
                consumers,
                player.clientLevel,
                player.getId()
        );
        matrices.popPose();
    }

    private static Vec3 getFlightDirection(AbstractClientPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        if (velocity.lengthSqr() > 1.0E-4D) {
            return velocity.normalize();
        }

        Vec3 look = player.getLookAngle();
        if (look.lengthSqr() > 1.0E-4D) {
            return look.normalize();
        }
        return new Vec3(0.0D, 0.0D, 1.0D);
    }
}
