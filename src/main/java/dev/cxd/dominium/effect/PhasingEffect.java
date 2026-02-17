package dev.cxd.dominium.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import java.util.Map;
import java.util.WeakHashMap;

public class PhasingEffect extends StatusEffect {
    private static final Map<LivingEntity, Integer> startTicks = new WeakHashMap<>();

    public PhasingEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xFF004F);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.noClip = true;

        startTicks.putIfAbsent(entity, entity.age);

        int ticksActive = entity.age - startTicks.get(entity);

        if (ticksActive >= 20 && !isInsideBlock(entity)) {
            entity.noClip = false;
            entity.removeStatusEffect(this);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    private boolean isInsideBlock(LivingEntity entity) {
        return !entity.getWorld().isSpaceEmpty(entity, entity.getBoundingBox());
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.noClip = false;
        startTicks.remove(entity);
    }
}