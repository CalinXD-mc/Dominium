package dev.cxd.dominium.effect;

import dev.cxd.dominium.utils.AdvancementsUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;

public class SoulDebtEffect extends StatusEffect {

    public SoulDebtEffect() {
        super(StatusEffectCategory.HARMFUL, 0x074756);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof ServerPlayerEntity player) {
            AdvancementsUtils.grantAdvancement(player, "soul_debt");
        }
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }
}