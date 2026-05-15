package com.cybercultivation.client.render;

import com.cybercultivation.CyberCultivationMod;
import com.cybercultivation.entity.ModEntities;
import com.cybercultivation.entity.custom.DemonWolfEntity;
import com.cybercultivation.entity.custom.SpiritDeerEntity;
import com.cybercultivation.entity.custom.StoneShellTurtleEntity;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class ModEntityRenderers {
    private static final ResourceLocation SPIRIT_DEER_TEXTURE = texture("spirit_deer");
    private static final ResourceLocation DEMON_WOLF_TEXTURE = texture("demon_wolf");
    private static final ResourceLocation STONE_SHELL_TURTLE_TEXTURE = texture("stone_shell_turtle");

    private ModEntityRenderers() {
    }

    public static void register() {
        EntityRendererRegistry.register(ModEntities.SPIRIT_DEER, context -> new MobRenderer<>(
                context,
                new CowModel<SpiritDeerEntity>(context.bakeLayer(ModelLayers.COW)),
                0.6F
        ) {
            @Override
            public ResourceLocation getTextureLocation(SpiritDeerEntity entity) {
                return SPIRIT_DEER_TEXTURE;
            }
        });
        EntityRendererRegistry.register(ModEntities.SPIRIT_CRANE,
                context -> new GeoEntityRenderer<>(context, new CultivationCreatureModel<>()).withScale(0.85F));
        EntityRendererRegistry.register(ModEntities.DEMON_WOLF, context -> new MobRenderer<>(
                context,
                new WolfModel<DemonWolfEntity>(context.bakeLayer(ModelLayers.WOLF)),
                0.5F
        ) {
            @Override
            public ResourceLocation getTextureLocation(DemonWolfEntity entity) {
                return DEMON_WOLF_TEXTURE;
            }
        });
        EntityRendererRegistry.register(ModEntities.STONE_SHELL_TURTLE, context -> new MobRenderer<>(
                context,
                new TurtleModel<StoneShellTurtleEntity>(context.bakeLayer(ModelLayers.TURTLE)),
                0.45F
        ) {
            @Override
            public ResourceLocation getTextureLocation(StoneShellTurtleEntity entity) {
                return STONE_SHELL_TURTLE_TEXTURE;
            }
        });
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, "textures/entity/" + name + ".png");
    }
}
