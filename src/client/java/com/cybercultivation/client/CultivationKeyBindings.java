package com.cybercultivation.client;

import com.cybercultivation.network.RequestAptitudeInfoPayload;
import com.cybercultivation.network.RequestCultivationInfoPayload;
import com.cybercultivation.network.ToggleFlyingSwordPayload;
import com.cybercultivation.network.ToggleMeditationPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class CultivationKeyBindings {

    private static KeyMapping meditateKey;
    private static KeyMapping flyingSwordKey;
    private static KeyMapping infoKey;
    private static KeyMapping aptitudeKey;

    private CultivationKeyBindings() {
    }

    public static void register() {
        meditateKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cyber-cultivation-mod.meditate",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.cyber-cultivation-mod"
        ));

        flyingSwordKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cyber-cultivation-mod.flying_sword",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.cyber-cultivation-mod"
        ));

        infoKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cyber-cultivation-mod.cultivation_info",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.cyber-cultivation-mod"
        ));

        aptitudeKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cyber-cultivation-mod.aptitude_info",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.cyber-cultivation-mod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.screen != null) {
                return;
            }

            while (meditateKey.consumeClick()) {
                ClientPlayNetworking.send(new ToggleMeditationPayload());
            }
            while (flyingSwordKey.consumeClick()) {
                ClientPlayNetworking.send(new ToggleFlyingSwordPayload());
            }
            while (infoKey.consumeClick()) {
                ClientPlayNetworking.send(new RequestCultivationInfoPayload());
            }
            while (aptitudeKey.consumeClick()) {
                ClientPlayNetworking.send(new RequestAptitudeInfoPayload());
            }
        });
    }
}