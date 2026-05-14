package com.cybercultivation.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class HudEditController {
    private static KeyMapping editKey;
    private static boolean editMode;
    private static double accumulatedScroll;

    private HudEditController() {
    }

    public static void register() {
        editKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.cyber-cultivation-mod.hud_edit",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.cyber-cultivation-mod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (editKey.consumeClick()) {
                toggleEditMode(client);
            }
            if (editMode && client.screen != null) {
                exitEditMode(client);
            }
        });
    }

    public static boolean isEditMode() {
        return editMode;
    }

    public static void handleScroll(double amount) {
        if (amount != 0.0) {
            accumulatedScroll += amount;
        }
    }

    public static double consumeScrollDelta() {
        double delta = accumulatedScroll;
        accumulatedScroll = 0.0;
        return delta;
    }

    private static void toggleEditMode(Minecraft client) {
        if (editMode) {
            exitEditMode(client);
        } else {
            enterEditMode(client);
        }
    }

    private static void enterEditMode(Minecraft client) {
        editMode = true;
        QiHudOverlay.stopDragging();
        client.mouseHandler.releaseMouse();
        if (client.player != null) {
            client.player.displayClientMessage(
                    Component.literal("§bHUD 编辑模式已开启，拖拽移动面板，滚轮调大小，按 H 或 ESC 退出。"),
                    true
            );
        }
    }

    private static void exitEditMode(Minecraft client) {
        editMode = false;
        QiHudOverlay.stopDragging();
        QiHudOverlay.saveConfig();
        client.mouseHandler.grabMouse();
        if (client.player != null) {
            client.player.displayClientMessage(
                    Component.literal("§aHUD 编辑模式已保存并关闭。"),
                    true
            );
        }
    }
}