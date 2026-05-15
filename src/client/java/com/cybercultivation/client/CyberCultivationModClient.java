package com.cybercultivation.client;

import com.cybercultivation.block.ModBlocks;
import com.cybercultivation.client.animation.CultivationAnimationController;
import com.cybercultivation.client.particle.SpiritQiParticle;
import com.cybercultivation.client.render.FlyingSwordVisualRenderer;
import com.cybercultivation.client.render.ModEntityRenderers;
import com.cybercultivation.network.QiSyncPayload;
import com.cybercultivation.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.renderer.RenderType;

public class CyberCultivationModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                ModBlocks.SPIRIT_HERB_CROP,
                ModBlocks.SPIRIT_RICE_CROP,
                ModBlocks.FLAME_FLOWER,
                ModBlocks.FROST_FLOWER,
                ModBlocks.THUNDER_GRASS,
                ModBlocks.DEMON_BLOOD_VINE,
                ModBlocks.MOONLIGHT_LOTUS,
                ModBlocks.GOLDEN_SPIRIT_BAMBOO,
                ModBlocks.EARTH_ROOT_GINSENG,
                ModBlocks.SOUL_LANTERN_FLOWER,
                ModBlocks.FIVE_ELEMENT_FRUIT);

        // ---- Qi data sync ----
        ClientPlayNetworking.registerGlobalReceiver(QiSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                ClientQiData.set(
                        payload.currentQi(),
                        payload.maxQi(),
                        payload.selectedPath(),
                        payload.mainDiscipline(),
                        payload.subDisciplines(),
                        payload.element(),
                        payload.flyingSword(),
                        payload.flyingSwordItemId(),
                        payload.meditating()
                );
                if (context.client().player != null) {
                    FlyingSwordVisualState.set(context.client().player.getId(), payload.flyingSword(), payload.flyingSwordItemId());
                }
            });
        });

        // ---- HUD ----
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            QiHudOverlay.render(graphics);
        });
        HudEditController.register();

        // ---- Key bindings ----
        CultivationKeyBindings.register();

        // ---- Flying sword visual ----
        FlyingSwordVisualRenderer.register();
        ModEntityRenderers.register();

        // ---- Custom particles ----
        ParticleFactoryRegistry.getInstance().register(ModParticles.SPIRIT_QI, SpiritQiParticle.Factory::new);

        // ---- Player animations ----
        CultivationAnimationController.register();
    }
}
