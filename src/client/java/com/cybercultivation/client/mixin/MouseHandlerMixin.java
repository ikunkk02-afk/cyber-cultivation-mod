package com.cybercultivation.client.mixin;

import com.cybercultivation.client.HudEditController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    private void onGrabMouse(CallbackInfo ci) {
        if (HudEditController.isEditMode()) {
            ci.cancel();
        }
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onEditScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (HudEditController.isEditMode()) {
            HudEditController.handleScroll(vertical);
            ci.cancel();
        }
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void onEditPress(long window, int button, int action, int mods, CallbackInfo ci) {
        if (HudEditController.isEditMode() && Minecraft.getInstance().screen == null) {
            ci.cancel();
        }
    }
}