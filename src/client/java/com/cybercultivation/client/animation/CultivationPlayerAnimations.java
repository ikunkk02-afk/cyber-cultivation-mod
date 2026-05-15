package com.cybercultivation.client.animation;

import com.cybercultivation.CyberCultivationMod;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * Loads cultivation player animations from:
 * assets/cyber-cultivation-mod/player_animations/*.animation.json
 *
 * Important:
 * PlayerAnimationRegistry uses the animation name inside the json as the id path.
 * So an animation named "meditation" is loaded as cyber-cultivation-mod:meditation.
 */
public final class CultivationPlayerAnimations {
    private static final String MOD_ID = CyberCultivationMod.MOD_ID;

    private CultivationPlayerAnimations() {
    }

    public static IAnimation createMeditationPlayer() {
        return create("meditation");
    }

    public static IAnimation createFlyingSwordPlayer() {
        return create("flying_sword");
    }

    public static IAnimation createAttack(String animationName) {
        return create(animationName);
    }

    private static IAnimation create(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        IPlayable playable = PlayerAnimationRegistry.getAnimation(id);
        if (playable == null) {
            CyberCultivationMod.LOGGER.warn(
                    "Cultivation animation '{}' was not found. Check assets/{}/player_animations/*.animation.json and make sure the animation key is exactly '{}'.",
                    id, MOD_ID, name
            );
            return null;
        }
        return playable.playAnimation();
    }
}
