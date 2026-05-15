package com.cybercultivation.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StoneShellTurtleEntity extends Turtle {
    public StoneShellTurtleEntity(EntityType<? extends Turtle> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Turtle.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.16D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }
}
