package com.cybercultivation.client;

import com.cybercultivation.client.animation.CultivationAnimationController;
import com.cybercultivation.network.QiSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class CyberCultivationModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
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
                        payload.meditating()
                );
            });
        });

        // ---- HUD ----
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            QiHudOverlay.render(graphics);
        });
        HudEditController.register();

        // ---- Key bindings ----
        CultivationKeyBindings.register();

        // ---- Player animations ----
        CultivationAnimationController.register();
    }
}