package com.cybercultivation.client.render;

import com.cybercultivation.CyberCultivationMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

public class CultivationCreatureModel<T extends Entity & GeoEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return resource(animatable, "geo/", ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return resource(animatable, "textures/entity/", ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, "animations/cultivation_creature.animation.json");
    }

    private ResourceLocation resource(T animatable, String prefix, String suffix) {
        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(animatable.getType());
        return ResourceLocation.fromNamespaceAndPath(CyberCultivationMod.MOD_ID, prefix + entityId.getPath() + suffix);
    }
}
