package dev.cxd.dominium.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;


public class SoulStrainEffect extends StatusEffect {
    public SoulStrainEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xFFE875);

        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "123e4567-e89b-12d3-a456-426614174000",
                -7.7f,
                EntityAttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
